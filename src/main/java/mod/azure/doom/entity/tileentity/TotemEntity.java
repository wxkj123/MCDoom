package mod.azure.doom.entity.tileentity;

import java.util.List;
import java.util.Random;

import mod.azure.doom.entity.DemonEntity;
import mod.azure.doom.util.registry.ModEntityTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TotemEntity extends BlockEntity implements IAnimatable, TickableBlockEntity {

	protected final Random random = new Random();
	private final AnimationFactory factory = new AnimationFactory(this);

	private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
		return PlayState.CONTINUE;
	}

	public TotemEntity() {
		super(ModEntityTypes.TOTEM.get());
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<TotemEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	@Override
	public void tick() {
		if (this.level.getGameTime() % 80L == 0L) {
			this.applyEffects();
		}
		Level world = this.getLevel();
		if (world != null) {
			BlockPos blockpos = this.getBlockPos();
			if (this.level.isClientSide) {
				double d0 = (double) blockpos.getX() + 1.0D * (this.random.nextDouble() - 0.25D) * 2.0D;
				double d1 = (double) blockpos.getY() + 1.0D * (this.random.nextDouble() - 0.5D) * 2.0D;
				double d2 = (double) blockpos.getZ() + 1.0D * (this.random.nextDouble() - 0.25D) * 2.0D;
				for (int k = 0; k < 4; ++k) {
					world.addParticle(DustParticleOptions.REDSTONE, d0, d1, d2,
							(this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
							(this.random.nextDouble() - 0.5D) * 2.0D);
				}
			}
		}
	}

	@Override
	public void setRemoved() {
		this.removeEffects();
		super.setRemoved();
	}

	private void applyEffects() {
		if (!this.level.isClientSide) {
			AABB axisalignedbb = (new AABB(this.worldPosition)).inflate(40).expandTowards(0.0D,
					(double) this.level.getMaxBuildHeight(), 0.0D);
			List<DemonEntity> list = this.level.getEntitiesOfClass(DemonEntity.class, axisalignedbb);
			for (DemonEntity entity : list) {
				entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 1));
				entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1000, 1));
				entity.setGlowing(true);
			}
		}
	}

	private void removeEffects() {
		if (!this.level.isClientSide) {
			AABB axisalignedbb = (new AABB(this.worldPosition)).inflate(40).expandTowards(0.0D,
					(double) this.level.getMaxBuildHeight(), 0.0D);
			List<DemonEntity> list = this.level.getEntitiesOfClass(DemonEntity.class, axisalignedbb);
			for (DemonEntity entity : list) {
				entity.setGlowing(false);
				entity.removeAllEffects();
			}
		}
	}
}