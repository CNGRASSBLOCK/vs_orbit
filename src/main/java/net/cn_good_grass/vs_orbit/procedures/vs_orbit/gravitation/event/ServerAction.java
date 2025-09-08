package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core.AstronomicalThread;
import net.lointain.cosmos.CosmosMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerAction {
    public static final List<AstronomicalPool> Astronomical_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void OnWorldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        String WorldId;
        if (event.getLevel() instanceof ServerLevel serverLevel) WorldId = serverLevel.dimension().location().toString(); else return;
        if (!VSOrbitDataPack.OrbitWorld.contains(WorldId)) return;

        WorldAction worldAction = WorldAction.get(serverLevel);

        if (worldAction.getJsonData() != null) { //如果有数据就读取
            AstronomicalPool astronomicalPool = AstronomicalPool.getFromJsonObject(new com.google.gson.Gson().fromJson(worldAction.getJsonData(), com.google.gson.JsonObject.class));
            if (astronomicalPool != null) ServerAction.Astronomical_Core_World_Bus.add(astronomicalPool);
        } else {
            CreateAstronomicalWorld(serverLevel); //没有就创建
        }
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        String WorldID = serverLevel.dimension().location().toString();
        if (!VSOrbitDataPack.OrbitWorld.contains(WorldID)) return;

        AstronomicalPool thisAstronomicalPool = AstronomicalPool.getFromWorldID(WorldID);
        if (thisAstronomicalPool == null) return;

        WorldAction worldAction = WorldAction.get(serverLevel);
        worldAction.setJsonData(thisAstronomicalPool.toJsonObject().toString());
        worldAction.setDirty(true);
    }

    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent event) {
        Astronomical_Core_World_Bus.clear();
        AstronomicalThread.StartThread();
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event) {
        AstronomicalThread.StopThread();
        Astronomical_Core_World_Bus.clear();
    }

    public static void CreateAstronomicalWorld(Level World) {
        String WorldId = World.dimension().location().toString();

        for (AstronomicalPool oneWorld : ServerAction.Astronomical_Core_World_Bus) if (oneWorld.WorldId.equals(WorldId)) return; //如果已经有了取消

        AstronomicalPool newWorld = new AstronomicalPool(World);

        ListTag listtag = StarAPI.getAllStarData(World, true);
        if (listtag == null) {
            CosmosMod.queueServerWork(20, () -> CreateAstronomicalWorld(World));
            return;
        }

        for (int i = 0 ; i < listtag.size() ; i++) {
            CompoundTag compoundTag = listtag.getCompound(i);
            String StarName = compoundTag.getString("object_name");

            JsonObject StarJsonObject = VSOrbitDataPack.OrbitData.getAsJsonObject(WorldId).getAsJsonObject("planet_data").deepCopy();
            if (!StarJsonObject.has(StarName)) continue;
            List<JsonElement> pos = StarJsonObject.getAsJsonObject(StarName).get("pos").getAsJsonArray().asList();
            if (pos.size() != 3) continue;
            String type = "cosmos:planet";
            if (compoundTag.contains("core_color")) type = "cosmos:star";
            Astronomical astronomical = new Astronomical(i, "CosmosStar-" + StarName, type, StarJsonObject.getAsJsonObject(StarName).get("astronomical_compute").getAsBoolean(), StarJsonObject.getAsJsonObject(StarName).get("mass").getAsDouble(), pos.get(0).getAsDouble(), pos.get(1).getAsDouble(), pos.get(2).getAsDouble());

            List<JsonElement> speed = StarJsonObject.getAsJsonObject(StarName).get("speed").getAsJsonArray().asList();
            if (speed.size() != 3) continue;
            astronomical.x_speed = speed.get(0).getAsDouble();
            astronomical.y_speed = speed.get(1).getAsDouble();
            astronomical.z_speed = speed.get(2).getAsDouble();

            List<JsonElement> rotating_shaft = StarJsonObject.getAsJsonObject(StarName).get("rotating_shaft").getAsJsonArray().asList();
            if (rotating_shaft.size() != 3) continue;
            astronomical.rotate = new Quaterniond().rotateXYZ(rotating_shaft.get(0).getAsDouble(), rotating_shaft.get(1).getAsDouble(),rotating_shaft.get(2).getAsDouble());
            astronomical.rotate_speed = 2 * Math.PI / StarJsonObject.getAsJsonObject(StarName).get("rotating_cycle").getAsDouble();

            CompoundTag CosmosData = new CompoundTag();
            CosmosData.putDouble("scale", compoundTag.getDouble("scale"));
            CosmosData.putString("travel_to", compoundTag.getString("travel_to"));
            astronomical.Tag.put("cosmos:data", CosmosData);

            newWorld.addAstronomical(astronomical);
        }

        VSOrbitMod.LOGGER.info("[VSOrbit] [Game] Create a celestial simulation dimension:" + newWorld.WorldId);

        ServerAction.Astronomical_Core_World_Bus.add(newWorld); //新建引力世界用于处理
    }
}


