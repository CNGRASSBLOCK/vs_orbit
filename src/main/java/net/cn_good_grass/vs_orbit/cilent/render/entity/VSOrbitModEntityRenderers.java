package net.cn_good_grass.vs_orbit.cilent.render.entity;

import net.cn_good_grass.vs_orbit.cilent.render.entity.ThrusterCore.ThrusterCoreRenderer;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VSOrbitModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(VSOrbitModEntities.THRUSTER_CORE.get(), ThrusterCoreRenderer::new);
	}
}
