package net.cn_good_grass.vs_orbit.config;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = VSOrbitMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigInit {
    @SubscribeEvent
    public static void register(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "vs_orbit_config.toml");
        });
    }
}
