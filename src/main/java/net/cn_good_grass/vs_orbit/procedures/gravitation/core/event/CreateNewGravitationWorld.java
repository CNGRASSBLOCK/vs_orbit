package net.cn_good_grass.vs_orbit.procedures.gravitation.core.event;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.GlobalVariables;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.math.BigInteger;


public class CreateNewGravitationWorld {
    public static void CreateNewGravitationWorld(Level World) {
        String WorldId = World.dimension().location().toString();

        for (GravitationWorld oneWorld : GlobalVariables.Gravitation_Core_AllWorld) { if (oneWorld.WorldId.equals(WorldId)) { return; } } //如果已经有了取消

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

            JsonObject StarJsonObject = GlobalVariables.StarStateData.getAsJsonObject("planet_data").deepCopy();
            if (!StarJsonObject.has(StarName)) { continue; }
            Particle particle = new Particle();
            particle.id = i;
            particle.name = "CosmosStar-" + StarName;
            particle.x = compoundTag.getInt("x");
            particle.y = compoundTag.getInt("y");
            particle.z = compoundTag.getInt("z");
            particle.mass = (double) (StarJsonObject.getAsJsonObject(StarName).get("mass").getAsDouble() * 10000);
            particle.x_speed = StarJsonObject.getAsJsonObject(StarName).get("x_start_speed").getAsDouble();
            particle.y_speed = StarJsonObject.getAsJsonObject(StarName).get("y_start_speed").getAsDouble();
            particle.z_speed = StarJsonObject.getAsJsonObject(StarName).get("z_start_speed").getAsDouble();
            particle.start = StarJsonObject.getAsJsonObject(StarName).get("particle_state").getAsString();
            newWorld.Gravitation_Core_World.add(particle);
        }
        GlobalVariables.Gravitation_Core_AllWorld.add(newWorld); //新建引力世界用于处理
    }
}
