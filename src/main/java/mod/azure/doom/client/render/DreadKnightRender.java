package mod.azure.doom.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.azure.doom.client.models.DreadknightModel;
import mod.azure.doom.entity.tierheavy.Hellknight2016Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DreadKnightRender extends GeoEntityRenderer<Hellknight2016Entity> {

	public DreadKnightRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new DreadknightModel());
	}

	@Override
	public RenderType getRenderType(Hellknight2016Entity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(Hellknight2016Entity entityLivingBaseIn) {
		return 0.0F;
	}

}