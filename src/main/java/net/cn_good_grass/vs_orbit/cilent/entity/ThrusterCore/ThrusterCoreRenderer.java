
package net.cn_good_grass.vs_orbit.cilent.entity.ThrusterCore;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cn_good_grass.vs_orbit.entity.ThrusterCore.ThrusterCoreEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrusterCoreRenderer extends GeoEntityRenderer<ThrusterCoreEntity> {
	public ThrusterCoreRenderer(EntityRendererProvider.Context renderManager) { super(renderManager, new ThrusterCoreModel()); this.shadowRadius = 0f; }

	@Override public RenderType getRenderType(ThrusterCoreEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) { return RenderType.entityTranslucent(getTextureLocation(animatable)); }

	@Override
	public void preRender(PoseStack poseStack, ThrusterCoreEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.scaleHeight = entity.getEntityData().get(ThrusterCoreEntity.scare);
		this.scaleWidth = entity.getEntityData().get(ThrusterCoreEntity.scare);
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override protected float getDeathMaxRotation(ThrusterCoreEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
