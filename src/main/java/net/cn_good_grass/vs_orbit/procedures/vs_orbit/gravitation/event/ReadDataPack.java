package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cn_good_grass.vs_orbit.config.Config;
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
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "vs_orbit")
public class ReadDataPack {
    public static JsonObject StarStateData = new JsonObject();
    
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Object>() {
            @Override
            protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                return new Object();
            }

            @Override
            protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                List<String> GravitationWorlds = new ArrayList<String>(Config.Gravitation_WORK_WORLD.get());
                for (String FilePos : GravitationWorlds) {
                    String FileName = FilePos.substring(FilePos.indexOf(":") + 1);
                    ResourceLocation advancementLocation = new ResourceLocation("vs_orbit", "vs_orbit_data/" + FileName + ".json");
                    // 读取进度数据
                    try {
                        Resource resource = resourceManager.getResource(advancementLocation).get();
                        try (InputStream inputStream = resource.open()) {
                            ReadDataPack.StarStateData.add(FilePos, JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
