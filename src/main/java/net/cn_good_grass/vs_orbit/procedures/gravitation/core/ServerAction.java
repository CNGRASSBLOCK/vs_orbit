package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.event.ReadDataPack;
import net.cn_good_grass.vs_orbit.procedures.gravitation.event.WorldAction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.cn_good_grass.vs_orbit.config.Config;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerAction {
    public static List<AstronomicalPool> Astronomical_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void OnWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        String WorldId;
        if (event.getLevel() instanceof ServerLevel serverLevel) WorldId = serverLevel.dimension().location().toString(); else return;
        if (!Config.Gravitation_WORK_WORLD.get().contains(WorldId)) return;

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
        if (!Config.Gravitation_WORK_WORLD.get().contains(WorldID)) return;

        AstronomicalPool thisAstronomicalPool = AstronomicalPool.getFromWorldID(WorldID);

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

        for (int i = 0 ; i < listtag.size() ; i++) {
            CompoundTag compoundTag = listtag.getCompound(i);
            String StarName = compoundTag.getString("object_name");

            JsonObject StarJsonObject = ReadDataPack.StarStateData.getAsJsonObject(WorldId).getAsJsonObject("planet_data").deepCopy();
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
            newWorld.addAstronomical(astronomical);
        }
        ServerAction.Astronomical_Core_World_Bus.add(newWorld); //新建引力世界用于处理
    }
}


