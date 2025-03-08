package net.cn_good_grass.vs_orbit.procedures.gravitation.star;

import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.event.OnWorldLoad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class StarTick {
    public static Vec3 OnStarRender(String WorldID , CompoundTag StarTag) { //星球更新
        GravitationWorld thisGravitationWorld = null;
        for (GravitationWorld gravitationWorld : OnWorldLoad.Gravitation_Core_AllWorld) { if (gravitationWorld.WorldId.equals(WorldID)) { thisGravitationWorld = gravitationWorld; } }
        if (thisGravitationWorld == null) { return new Vec3(0, 0, 0); }

        String StarID = StarTag.getString("object_name");
        if (StarID.equals("")) { return new Vec3(0, 0, 0); }
        Vector3d Pos = new Vector3d(0, 0, 0);
        for (Particle particle : thisGravitationWorld.Gravitation_Core_World) {
            if (particle.name.equals("CosmosStar-" + StarID)) {
                Pos.x = particle.x;
                Pos.y = particle.y;
                Pos.z = particle.z;
            }
        }
        return new Vec3(Pos.x, Pos.y, Pos.z);
    }
}
