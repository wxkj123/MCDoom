package mod.azure.doom.entity.projectiles;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;

public class CustomSmallFireballEntity extends SmallFireball {

	public CustomSmallFireballEntity(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ,
			float directHitDamage) {
		super(worldIn, shooter, accelX, accelY, accelZ);
		this.directHitDamage = directHitDamage;
	}

	private float directHitDamage = 5.0F;

	@Override
	protected void onHitEntity(EntityHitResult p_213868_1_) {
		if (!this.level.isClientSide) {
			Entity entity = p_213868_1_.getEntity();
			if (!entity.fireImmune()) {
				Entity entity1 = this.getOwner();
				int i = entity.getRemainingFireTicks();
				entity.setSecondsOnFire(5);
				boolean flag = entity.hurt(DamageSource.fireball(this, entity1), directHitDamage);
				if (!flag) {
					entity.setRemainingFireTicks(i);
				} else if (entity1 instanceof LivingEntity) {
					this.doEnchantDamageEffects((LivingEntity) entity1, entity);
				}
			}

		}
	}

}
