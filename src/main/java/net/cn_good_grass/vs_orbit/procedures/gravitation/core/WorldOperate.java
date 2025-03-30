package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.event.ReadDataPack;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.cn_good_grass.vs_orbit.config.Config;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class WorldOperate {
    public static List<GravitationWorld> Gravitation_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void OnWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) { return; }

        String WorldID = ((Level) event.getLevel()).dimension().location().toString();
        if (!Config.Gravitation_WORK_WORLD.get().contains(WorldID)) { return; }

        if (HasData(event, WorldID)) { //如果有数据就读取
            ReadData(event, WorldID);
        } else {
            CreateNewGravitationWorld((Level) event.getLevel());
        }

        GravitationThread.CreateThread(); //新建线程
    }

    public static void CreateNewGravitationWorld(Level World) {
        String WorldId = World.dimension().location().toString();

        for (GravitationWorld oneWorld : WorldOperate.Gravitation_Core_World_Bus) { if (oneWorld.WorldId.equals(WorldId)) { return; } } //如果已经有了取消

        GravitationWorld newWorld = new GravitationWorld();
        newWorld.WorldId = WorldId;

        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(World);

        if (!worldVars.collision_data_map.contains(WorldId)) { return; }
        Tag collision_data_map = worldVars.collision_data_map.get(WorldId); //星球数据
        Tag light_source_map = worldVars.light_source_map.get(WorldId); //恒星数据
        ListTag listtag = new ListTag();
        if (collision_data_map instanceof ListTag listTag) { listtag.addAll(listTag.copy()); }
        if (light_source_map instanceof ListTag listTag) { listtag.addAll(listTag.copy()); }
        if (listtag.isEmpty()) { return; }

        for (int i = 0 ; i < listtag.size() ; i++) {
            CompoundTag compoundTag = listtag.getCompound(i);
            String StarName = compoundTag.getString("object_name");

            JsonObject StarJsonObject = ReadDataPack.StarStateData.getAsJsonObject(WorldId).getAsJsonObject("planet_data").deepCopy();
            if (!StarJsonObject.has(StarName)) { continue; }
            Particle particle = new Particle();
            particle.id = i;
            particle.name = "CosmosStar-" + StarName;
            particle.x = compoundTag.getInt("x");
            particle.y = compoundTag.getInt("y");
            particle.z = compoundTag.getInt("z");
            particle.mass = (long) (StarJsonObject.getAsJsonObject(StarName).get("mass").getAsDouble() * ReadDataPack.StarStateData.getAsJsonObject(WorldId).get("relative_quality").getAsLong());
            particle.x_speed = StarJsonObject.getAsJsonObject(StarName).get("x_start_speed").getAsDouble();
            particle.y_speed = StarJsonObject.getAsJsonObject(StarName).get("y_start_speed").getAsDouble();
            particle.z_speed = StarJsonObject.getAsJsonObject(StarName).get("z_start_speed").getAsDouble();
            particle.start = StarJsonObject.getAsJsonObject(StarName).get("particle_state").getAsString();
            newWorld.Gravitation_Core_World.add(particle);
        }
        WorldOperate.Gravitation_Core_World_Bus.add(newWorld); //新建引力世界用于处理
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

            GravitationWorld gravitationWorld = GravitationWorld.getFromJsonObject(json);
            if (gravitationWorld != null) { WorldOperate.Gravitation_Core_World_Bus.add(gravitationWorld); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void OnWorldUnLoad(LevelEvent.Unload event) {
        MinecraftServer server = event.getLevel().getServer();
        if (server == null) { return; }

        String WorldID = ((Level) event.getLevel()).dimension().location().toString();

        GravitationWorld thisGravitationWorld = GravitationWorld.getFromWorldID(WorldID);

        JsonObject jsonObject = thisGravitationWorld.toJsonObject();
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

    @SubscribeEvent
    public static void OnWorldUnLoad(ServerStoppedEvent event) {
        Gravitation_Core_World_Bus.clear();
    }
}


