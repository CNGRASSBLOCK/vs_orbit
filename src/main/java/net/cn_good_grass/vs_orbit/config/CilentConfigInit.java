package net.cn_good_grass.vs_orbit.config;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = VSOrbitMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CilentConfigInit {
    @SubscribeEvent
    public static void register(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CilentConfig.SPEC, "vs_orbit_client_config.toml");
        });
    }
}
