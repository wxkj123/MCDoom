package mod.azure.doom.item.eggs;

import mod.azure.doom.DoomMod;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;

public class DoomSpawnEgg extends SpawnEggItem {

	public DoomSpawnEgg(EntityType<?> type) {
		super(type, 11022961, 11035249, new Item.Settings().maxCount(64).group(DoomMod.DoomEggItemGroup));
	}

}