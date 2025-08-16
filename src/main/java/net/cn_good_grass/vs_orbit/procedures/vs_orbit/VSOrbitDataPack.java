package net.cn_good_grass.vs_orbit.procedures.vs_orbit;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "vs_orbit")
public class VSOrbitDataPack {
    public static List<String> OrbitWorld = new ArrayList<>();
    public static JsonObject OrbitData = new JsonObject();

    public static List<String> PlanetWorld = new ArrayList<>();
    public static JsonObject PlanetData = new JsonObject();
    
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Object>() {
            @Override
            protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                return new Object();
            }

            @Override
            protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                {
                    Map<ResourceLocation, Resource> resources = resourceManager.listResources("orbit_data", path -> path.toString().endsWith(".json"));
                    for (ResourceLocation resourceLocation : resources.keySet()) OrbitWorld.add("cosmos:" + resourceLocation.getPath().substring(11, resourceLocation.getPath().length() - 5));
                    for (String FilePos : OrbitWorld) {
                        String FileName = FilePos.substring(FilePos.indexOf(":") + 1);
                        ResourceLocation advancementLocation = new ResourceLocation("vs_orbit", "orbit_data/" + FileName + ".json");
                        try {
                            Resource resource = resourceManager.getResource(advancementLocation).get();
                            try (InputStream inputStream = resource.open()) {OrbitData.add(FilePos, JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject());}
                        } catch (IOException ignored) {}
                    }
                }

                {
                    Map<ResourceLocation, Resource> resources = resourceManager.listResources("world_data", path -> path.toString().endsWith(".json"));
                    for (ResourceLocation resourceLocation : resources.keySet()) {
                        Resource resource = resourceManager.getResource(resourceLocation).get();
                        try (InputStream inputStream = resource.open()) {
                            JsonObject data = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
                            String WorldID = data.get("dimension_id").getAsString();

                            PlanetWorld.add(WorldID);
                            PlanetData.add(WorldID, data);
                        } catch (IOException ignored) {}
                    }
                }
            }
        });
    }
}
