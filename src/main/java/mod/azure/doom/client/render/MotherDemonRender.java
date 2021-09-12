package mod.azure.doom.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.azure.doom.client.models.MotherDemonModel;
import mod.azure.doom.entity.tierboss.MotherDemonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MotherDemonRender extends GeoEntityRenderer<MotherDemonEntity> {

	public MotherDemonRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new MotherDemonModel());
	}

	@Override
	public RenderType getRenderType(MotherDemonEntity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(MotherDemonEntity entityLivingBaseIn) {
		return 0.0F;
	}

}