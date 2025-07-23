package net.cn_good_grass.vs_orbit.cilent.render.object.Orbit;

import net.cn_good_grass.vs_orbit.config.ClientConfig;
import net.cn_good_grass.vs_orbit.network.SyncDataTick;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "vs_orbit", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OrbitPredictionTick {
    public static int tick = 0;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null && Minecraft.getInstance().level == null) return;

        if (tick >= ClientConfig.OrbitPrediction_TICK.get()) {
            tick = 0;
            OrbitPrediction.Prediction(ClientConfig.OrbitPrediction_FREQUENCY.get(), ClientConfig.OrbitPrediction_TIME.get());
        }
        tick++;
    }
}
