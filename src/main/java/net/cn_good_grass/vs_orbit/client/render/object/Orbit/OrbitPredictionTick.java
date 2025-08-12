package net.cn_good_grass.vs_orbit.client.render.object.Orbit;

import net.cn_good_grass.vs_orbit.config.VSOrbitModClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "vs_orbit", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OrbitPredictionTick {
    public static int tick = 0;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null && Minecraft.getInstance().level == null) return;

        if (tick >= VSOrbitModClientConfig.OrbitPrediction_TICK.get()) {
            tick = 0;
            OrbitPrediction.Prediction(VSOrbitModClientConfig.OrbitPrediction_FREQUENCY.get(), VSOrbitModClientConfig.OrbitPrediction_TIME.get());
        }
        tick++;
    }
}
