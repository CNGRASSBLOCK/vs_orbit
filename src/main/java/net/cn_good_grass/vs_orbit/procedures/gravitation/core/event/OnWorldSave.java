package net.cn_good_grass.vs_orbit.procedures.gravitation.core.event;

import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.thread.CreateThread;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/*@Mod.EventBusSubscriber
public class OnWorldSave {
    @SubscribeEvent
    public static void OnWorldLoad(LevelEvent.Unload event) {
        String WorldId = ((Level) event.getLevel()).dimension().location().toString();

        if (!WorldId.equals("cosmos:solar_system")) { return; } //如果不是目标维度就不建线程了

        GravitationWorld newWorld = new GravitationWorld();
        newWorld.WorldId = WorldId;

        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(((Level) event.getLevel()));

        if (!worldVars.collision_data_map.contains(WorldId)) { return; }
        Tag collision_data_map = worldVars.collision_data_map.get(WorldId); //星球数据
        Tag light_source_map = worldVars.light_source_map.get(WorldId); //恒星数据
        ListTag listtag = new ListTag();
        if (collision_data_map instanceof ListTag listTag) { listtag.addAll(listTag.copy()); }
        if (light_source_map instanceof ListTag listTag) { listtag.addAll(listTag.copy()); }
        if (listtag.isEmpty()) { return; }

        System.out.println(listtag.toString());

        for (int i = 0 ; i < listtag.size() ; i++) {
            CompoundTag compoundTag = listtag.getCompound(i);
            Particle particle = new Particle();
            particle.id = i;
            particle.name = "CosmosStar-" + compoundTag.getString("object_name");
            particle.x = compoundTag.getInt("x");
            particle.y = compoundTag.getInt("y");
            particle.z = compoundTag.getInt("z");
            particle.mass = 10L;
            newWorld.Gravitation_Core_World.add(particle);
        }
        Gravitation_Core_AllWorld.add(newWorld); //新建引力世界用于处理

        CreateThread.CreateThread(); //新建线程
    }
}*/


