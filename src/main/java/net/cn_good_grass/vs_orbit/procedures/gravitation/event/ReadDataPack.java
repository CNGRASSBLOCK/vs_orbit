package net.cn_good_grass.vs_orbit.procedures.gravitation.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Mod.EventBusSubscriber(modid = "vs_orbit")
public class ReadDataPack {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Object>() {
            @Override
            protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                return new Object();
            }

            @Override
            protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                ResourceLocation advancementLocation = new ResourceLocation("vs_orbit", "vs_orbit_data/solar_system.json");

                // 读取进度数据
                try {
                    Resource resource = resourceManager.getResource(advancementLocation).get();
                    try (InputStream inputStream = resource.open()) {
                        JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
                        System.out.println("!!!!!!!!!!!!!!!!"+jsonObject.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
