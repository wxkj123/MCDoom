package mod.azure.doom.entity.ai.goal;

import java.util.EnumSet;

import mod.azure.doom.entity.tierfodder.LostSoulEntity;
import mod.azure.doom.entity.tierheavy.PainEntity;
import mod.azure.doom.util.registry.DoomEntities;
import mod.azure.doom.util.registry.DoomSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class PainAttackGoal extends Goal {
	private final PainEntity entity;
	private int attackTime = -1;

	public PainAttackGoal(PainEntity mob) {
		this.entity = mob;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		return this.entity.getTarget() != null;
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.attackTime = 0;
		this.entity.setAttacking(true);
	}

	@Override
	public void stop() {
		super.stop();
		this.entity.setAttacking(false);
		this.entity.setAttackingState(0);
		this.attackTime = -1;
	}

	@Override
	public void tick() {
		LivingEntity livingentity = this.entity.getTarget();
		if (livingentity != null) {
			boolean inLineOfSight = this.entity.getVisibilityCache().canSee(livingentity);
			this.attackTime++;
			this.entity.lookAtEntity(livingentity, 30.0F, 30.0F);
			double d0 = this.entity.squaredDistanceTo(livingentity.getX(), livingentity.getY(), livingentity.getZ());
			double d1 = (double) (this.entity.getWidth() * 2.5F * this.entity.getWidth() * 2.5F + entity.getWidth());
			if (inLineOfSight) {
				if (this.entity.distanceTo(livingentity) >= 12.0D) {
					this.entity.getNavigation().startMovingTo(livingentity, 1);
					this.entity.getLookControl().lookAt(livingentity.getX(), livingentity.getEyeY(),
							livingentity.getZ());
					if (this.attackTime == 1) {
						this.entity.setAttackingState(0);
					}
					if (this.attackTime == 12) {
						this.entity.setAttackingState(1);
						entity.playSound(DoomSounds.PAIN_HURT, 1.0F, 1.0F);
						if (this.entity.getVariant() == 1 || this.entity.getVariant() == 3) {
							LostSoulEntity lost_soul = DoomEntities.LOST_SOUL.create(entity.world);
							lost_soul.refreshPositionAndAngles(this.entity.getX(), this.entity.getY(),
									this.entity.getZ(), 0, 0);
							entity.world.spawnEntity(lost_soul);
						} else {
							LostSoulEntity lost_soul = DoomEntities.LOST_SOUL.create(entity.world);
							lost_soul.refreshPositionAndAngles(this.entity.getX(), this.entity.getY(),
									this.entity.getZ(), 0, 0);
							entity.world.spawnEntity(lost_soul);

							LostSoulEntity lost_soul1 = DoomEntities.LOST_SOUL.create(entity.world);
							lost_soul1.refreshPositionAndAngles(this.entity.getX(), this.entity.getY(),
									this.entity.getZ(), 0, 0);
							entity.world.spawnEntity(lost_soul1);
						}

						boolean isInsideWaterBlock = entity.world.isWater(entity.getBlockPos());
						entity.spawnLightSource(this.entity, isInsideWaterBlock);
					}
					if (this.attackTime == 20) {
						this.attackTime = 0;
						this.entity.setAttackingState(0);
					}
				} else {
					this.entity.getLookControl().lookAt(livingentity.getX(), livingentity.getEyeY(),
							livingentity.getZ());
					if (this.attackTime == 3) {
						if (d0 <= d1) {
							this.entity.setAttackingState(1);
							this.entity.tryAttack(livingentity);
						}
					}
					if (this.attackTime == 20) {
						this.attackTime = 0;
						this.entity.setAttackingState(0);
					}
				}
			}
		}
	}
}
