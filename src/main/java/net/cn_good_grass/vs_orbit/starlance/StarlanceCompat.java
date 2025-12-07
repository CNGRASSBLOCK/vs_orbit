package net.cn_good_grass.vs_orbit.starlance;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class StarlanceCompat {

    private static Class<?> levelDataClass;
    private static Method getLevelDataMethod;
    private static Field lowerDimensionsField;

    private static Class<?> planetDataClass;
    private static Method setPositionMethod;

    private static boolean reflectionInitialized = false;
    private static boolean enabled = true;

    private static void initReflection() {
        if (reflectionInitialized)
            return;
        reflectionInitialized = true;
        try {
            levelDataClass = Class.forName("net.jcm.vsch.util.wapi.LevelData");
            getLevelDataMethod = levelDataClass.getMethod("get", Level.class);
            lowerDimensionsField = levelDataClass.getDeclaredField("lowerDimensions");
            lowerDimensionsField.setAccessible(true);

            planetDataClass = Class.forName("net.jcm.vsch.util.wapi.PlanetData");
            setPositionMethod = planetDataClass.getMethod("setPosition", Vec3.class);
        } catch (Throwable t) {
            System.err.println("[VS_Orbit] Starlance compatibility failed to initialize: " + t.getMessage());
            enabled = false;
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!enabled || event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel serverLevel))
            return;

        initReflection();
        if (!enabled)
            return;

        try {
            String systemDimension = serverLevel.dimension().location().toString();
            AstronomicalPool pool = AstronomicalPool.getFromWorldID(systemDimension);
            if (pool == null)
                return;
            List<Astronomical> astronomicals = pool.getAllAstronomical();

            Object levelData = getLevelDataMethod.invoke(null, serverLevel);
            Map<ResourceKey<Level>, Object> planetsMap = (Map<ResourceKey<Level>, Object>) lowerDimensionsField
                    .get(levelData);

            if (planetsMap == null || planetsMap.isEmpty())
                return;

            for (Map.Entry<ResourceKey<Level>, Object> entry : planetsMap.entrySet()) {
                String planetDim = entry.getKey().location().toString();
                Object planetData = entry.getValue();

                for (Astronomical astro : astronomicals) {
                    if (astro instanceof CosmosAstronomical cosmosAstro) {
                        String travelTo = cosmosAstro.getTravelTo();
                        if (travelTo != null && travelTo.equals(planetDim)) {
                            setPositionMethod.invoke(planetData, new Vec3(cosmosAstro.x, cosmosAstro.y, cosmosAstro.z));
                            break;
                        }
                    }
                }
            }

        } catch (Throwable t) {
            // Rate limit errors?
        }
    }
}
