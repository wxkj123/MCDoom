package mod.azure.doom.item.weapons;

import java.util.List;

import mod.azure.doom.DoomMod;
import mod.azure.doom.client.Keybindings;
import mod.azure.doom.client.render.weapons.ChainsawRender;
import mod.azure.doom.util.enums.DoomTier;
import mod.azure.doom.util.packets.DoomPacketHandler;
import mod.azure.doom.util.packets.weapons.ChainsawEternalLoadingPacket;
import mod.azure.doom.util.registry.DoomItems;
import mod.azure.doom.util.registry.ModSoundEvents;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChainsawAnimated extends Item implements IAnimatable {

	public AnimationFactory factory = new AnimationFactory(this);
	private String controllerName = "controller";

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("running", true));
		return PlayState.CONTINUE;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController(this, controllerName, 1, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	public ChainsawAnimated() {
		super(new Item.Properties().tab(DoomMod.DoomWeaponItemGroup).stacksTo(1).durability(601)
				.setISTER(() -> ChainsawRender::new));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return DoomTier.CHAINSAW.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent(
				"Fuel: " + (stack.getMaxDamage() - stack.getDamageValue() - 1) + " / " + (stack.getMaxDamage() - 1))
						.withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		LivingEntity user = (LivingEntity) entityIn;
		Player player = (Player) entityIn;
		if (player.getMainHandItem().sameItemStackIgnoreDurability(stack)
				&& stack.getDamageValue() < (stack.getMaxDamage() - 1)) {
			final AABB aabb = new AABB(entityIn.blockPosition().above()).inflate(1D, 1D, 1D);
			entityIn.getCommandSenderWorld().getEntities(user, aabb).forEach(e -> doDamage(user, e));
			entityIn.getCommandSenderWorld().getEntities(user, aabb).forEach(e -> damageItem(user, stack));
			entityIn.getCommandSenderWorld().getEntities(user, aabb).forEach(e -> addParticle(e));
			worldIn.playSound((Player) null, user.getX(), user.getY(), user.getZ(),
					ModSoundEvents.CHAINSAW_IDLE.get(), SoundSource.PLAYERS, 0.05F,
					1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.25F * 0.5F);
		}
		if (worldIn.isClientSide) {
			if (player.getMainHandItem().sameItemStackIgnoreDurability(stack)) {
				while (Keybindings.RELOAD.consumeClick() && isSelected) {
					DoomPacketHandler.CHAINSAW_ETERNAL.sendToServer(new ChainsawEternalLoadingPacket(itemSlot));
				}
			}
		}
	}

	public static void reload(Player user, InteractionHand hand) {
		if (user.getItemInHand(hand).getItem() instanceof ChainsawAnimated) {
			while (user.getItemInHand(hand).getDamageValue() != 0
					&& user.inventory.countItem(DoomItems.GAS_BARREL.get()) > 0) {
				removeAmmo(DoomItems.GAS_BARREL.get(), user);
				user.getItemInHand(hand).hurtAndBreak(-200, user, s -> user.broadcastBreakEvent(hand));
				user.getItemInHand(hand).setPopTime(3);
			}
		}
	}

	public static void removeAmmo(Item ammo, Player playerEntity) {
		if (!playerEntity.isCreative()) {
			for (ItemStack item : playerEntity.inventory.offhand) {
				if (item.getItem() == ammo) {
					item.shrink(1);
					break;
				}
				for (ItemStack item1 : playerEntity.inventory.items) {
					if (item1.getItem() == ammo) {
						item1.shrink(1);
						break;
					}
				}
			}
		}
	}

	private void doDamage(LivingEntity user, final Entity target) {
		if (target instanceof LivingEntity) {
			target.setDeltaMovement(0, 0, 0);
			target.invulnerableTime = 0;
			target.hurt(DamageSource.playerAttack((Player) user), 2F);
			user.level.playSound((Player) null, user.getX(), user.getY(), user.getZ(),
					ModSoundEvents.CHAINSAW_ATTACKING.get(), SoundSource.PLAYERS, 0.3F,
					1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.25F * 0.5F);
		}
	}

	private void damageItem(LivingEntity user, ItemStack stack) {
		Player player = (Player) user;
		if (!player.abilities.instabuild) {
			stack.setDamageValue(stack.getDamageValue() + 1);
		}
	}

	private void addParticle(Entity target) {
		if (target instanceof LivingEntity) {
			target.level.addParticle(DustParticleOptions.REDSTONE, target.getRandomX(0.5D), target.getRandomY(),
					target.getRandomZ(0.5D), 0.0D, 0D, 0D);
		}
	}

}