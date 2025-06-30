package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.event.ReadDataPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.cn_good_grass.vs_orbit.config.Config;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerStart {
    public static List<AstronomicalPool> Gravitation_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent public static void OnServerStart(ServerStartedEvent event) { AstronomicalThread.CreateThread(); }

    @SubscribeEvent
    public static void OnWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        Gravitation_Core_World_Bus.clear();
        for (String WorldId : Config.Gravitation_WORK_WORLD.get()) {
            if (!Config.Gravitation_WORK_WORLD.get().contains(WorldId)) continue;

            if (HasData(event, WorldId)) { //如果有数据就读取
                ReadData(event, WorldId);
            } else {
                MinecraftServer server = event.getLevel().getServer();
                if (server == null) return;
                Level level = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldId)));
                if (level == null) continue;
                CreateNewGravitationWorld(level); //没有就创建
            }
        }
    }

    @SubscribeEvent
    public static void OnWorldUnLoad(LevelEvent.Unload event) { //保存数据
        MinecraftServer server = event.getLevel().getServer();
        if (server == null) return;

        String WorldID = ((Level) event.getLevel()).dimension().location().toString();
        if (!Config.Gravitation_WORK_WORLD.get().contains(WorldID)) return;

        AstronomicalPool thisAstronomicalPool = AstronomicalPool.getFromWorldID(WorldID);

        JsonObject jsonObject = thisAstronomicalPool.toJsonObject();
        if (jsonObject.size() == 0) { return; }

        String WorldFile = FMLPaths.GAMEDIR.get().toString() + server.getWorldPath(LevelResource.ROOT);
        File DataFile = new File(WorldFile.substring(0, WorldFile.length() - 2) + "\\data\\orbitdata\\" + WorldID.replace(":", "_") + ".json");

        if (!DataFile.exists()) {
            try {
                DataFile.getParentFile().mkdirs();
                DataFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(DataFile);
            fileWriter.write(mainGSONBuilderVariable.toJson(jsonObject));
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        for (int i = 0; i < Gravitation_Core_World_Bus.size(); i++) { if (Gravitation_Core_World_Bus.get(i).WorldId.equals(WorldID)) { Gravitation_Core_World_Bus.remove(i); } }
    }



    public static void CreateNewGravitationWorld(Level World) {
        String WorldId = World.dimension().location().toString();

        for (AstronomicalPool oneWorld : ServerStart.Gravitation_Core_World_Bus) { if (oneWorld.WorldId.equals(WorldId)) { return; } } //如果已经有了取消

        AstronomicalPool newWorld = new AstronomicalPool(World);

        ListTag listtag = StarAPI.getAllStarData(World);

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
        ServerStart.Gravitation_Core_World_Bus.add(newWorld); //新建引力世界用于处理
    }

    public static boolean HasData(net.minecraftforge.event.level.LevelEvent.Load event, String WorldID) {
        MinecraftServer server = event.getLevel().getServer();
        if (server == null) { return false; }
        String WorldFile = FMLPaths.GAMEDIR.get().toString() + server.getWorldPath(LevelResource.ROOT);
        File DataFile = new File(WorldFile.substring(0, WorldFile.length() - 2) + "\\data\\orbitdata");

        List<String> fileNames = null;
        if (DataFile.exists() && DataFile.isDirectory()) { fileNames = List.of(DataFile.list()); }
        if (fileNames == null) { return false; }
        if (!fileNames.contains(WorldID.replace(":", "_") + ".json")) { return false; }
        return true;
    }

    public static void ReadData(net.minecraftforge.event.level.LevelEvent.Load event, String WorldID) {
        MinecraftServer server = event.getLevel().getServer();
        if (server == null) { return; }
        String WorldFile = FMLPaths.GAMEDIR.get().toString() + server.getWorldPath(LevelResource.ROOT);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(WorldFile.substring(0, WorldFile.length() - 2) + "\\data\\orbitdata\\" + WorldID.replace(":", "_") + ".json"));
            StringBuilder jsonstringbuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonstringbuilder.append(line);
            }
            bufferedReader.close();
            JsonObject json = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);

            AstronomicalPool astronomicalPool = AstronomicalPool.getFromJsonObject(json);
            if (astronomicalPool != null) { ServerStart.Gravitation_Core_World_Bus.add(astronomicalPool); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


