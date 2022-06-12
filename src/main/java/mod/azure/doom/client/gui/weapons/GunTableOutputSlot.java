package mod.azure.doom.client.gui.weapons;

import java.util.Optional;

import mod.azure.doom.compat.PMMOCompat;
import mod.azure.doom.recipes.GunTableRecipe;
import mod.azure.doom.recipes.GunTableRecipe.Type;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class GunTableOutputSlot extends Slot {
	private final DoomGunInventory gunTableInventory;
	private final Player player;
	private int removeCount;

	public GunTableOutputSlot(Player player, DoomGunInventory gunTableInventory, int index, int x, int y) {
		super(gunTableInventory, index, x, y);
		this.player = player;
		this.gunTableInventory = gunTableInventory;
	}

	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	public ItemStack remove(int amount) {
		if (this.hasItem()) {
			this.removeCount += Math.min(amount, this.getItem().getCount());
		}

		return super.remove(amount);
	}

	protected void onQuickCraft(ItemStack stack, int amount) {
		this.removeCount += amount;
		this.checkTakeAchievements(stack);
	}

	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(this.player.level, this.player, this.removeCount);
		this.removeCount = 0;
	}

	public void onTake(Player player, ItemStack stack) {
		this.checkTakeAchievements(stack);
		Optional<GunTableRecipe> optionalGunTableRecipe = player.level.getRecipeManager().getRecipeFor(Type.INSTANCE,
				gunTableInventory, player.level);
		if (optionalGunTableRecipe.isPresent()) {
			GunTableRecipe gunTableRecipe = optionalGunTableRecipe.get();
			NonNullList<ItemStack> NonNullList = gunTableRecipe.getRemainingItems(gunTableInventory);

			for (int i = 0; i < NonNullList.size(); ++i) {
				ItemStack itemStack = this.gunTableInventory.getItem(i);
				ItemStack itemStack2 = NonNullList.get(i);
				if (!itemStack.isEmpty()) {
					this.gunTableInventory.removeItem(i, gunTableRecipe.countRequired(i));
					itemStack = this.gunTableInventory.getItem(i);
				}

				if (!itemStack2.isEmpty()) {
					if (itemStack.isEmpty()) {
						this.gunTableInventory.setItem(i, itemStack2);
					} else if (ItemStack.isSameIgnoreDurability(itemStack, itemStack2)
							&& ItemStack.isSame(itemStack, itemStack2)) {
						itemStack2.shrink(itemStack.getCount());
						this.gunTableInventory.setItem(i, itemStack2);
					} else if (!this.player.getInventory().add(itemStack2)) {
						this.player.drop(itemStack2, false);
					}
				}
			}
		}
		if (ModList.get().isLoaded("pmmo")) {
			PMMOCompat.awardCrafting(stack);
		}
		this.setChanged();
	}
}
