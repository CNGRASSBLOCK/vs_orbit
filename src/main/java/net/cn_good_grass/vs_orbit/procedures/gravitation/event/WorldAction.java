package net.cn_good_grass.vs_orbit.procedures.gravitation.event;

import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.nio.file.Path;

public class WorldAction extends SavedData {
    private String jsonData;

    // 从NBT加载数据
    public static WorldAction load(CompoundTag tag) {
        WorldAction data = new WorldAction();
        data.jsonData = tag.getString("jsonData");
        return data;
    }

    // 保存数据到NBT
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("jsonData", jsonData);
        return tag;
    }

    public static WorldAction get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(WorldAction::load, WorldAction::new, "orbit_data");
    }

    public void setJsonData(String json) {
        this.jsonData = json;
        this.setDirty(); // 标记需要保存
    }

    public String getJsonData() {
        return jsonData;
    }
}