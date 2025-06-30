package net.cn_good_grass.vs_orbit.cilent.render.entity.ThrusterCore;

import net.cn_good_grass.vs_orbit.entity.ThrusterCore.ThrusterCoreEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThrusterCoreModel extends GeoModel<ThrusterCoreEntity> {
	@Override public ResourceLocation getAnimationResource(ThrusterCoreEntity entity) { return new ResourceLocation("vs_orbit", "animations/thruster_core.animation.json"); }

	@Override public ResourceLocation getModelResource(ThrusterCoreEntity entity) { return new ResourceLocation("vs_orbit", "geo/thruster_core.geo.json"); }

	@Override public ResourceLocation getTextureResource(ThrusterCoreEntity entity) { return new ResourceLocation("vs_orbit", "textures/entity/thruster_core.png"); }
}
