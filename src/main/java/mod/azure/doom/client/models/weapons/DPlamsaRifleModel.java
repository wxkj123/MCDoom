package mod.azure.doom.client.models.weapons;

import mod.azure.doom.DoomMod;
import mod.azure.doom.item.weapons.DPlasmaRifle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DPlamsaRifleModel extends AnimatedGeoModel<DPlasmaRifle> {
	@Override
	public ResourceLocation getModelLocation(DPlasmaRifle object) {
		return new ResourceLocation(DoomMod.MODID, "geo/doomed_plasma_rifle.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DPlasmaRifle object) {
		return new ResourceLocation(DoomMod.MODID, "textures/items/doomed_plasma_rifle.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DPlasmaRifle animatable) {
		return new ResourceLocation(DoomMod.MODID, "animations/doomed_plasma_rifle.animation.json");
	}
}
