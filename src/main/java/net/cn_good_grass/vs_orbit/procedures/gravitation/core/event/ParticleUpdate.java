package net.cn_good_grass.vs_orbit.procedures.gravitation.core.event;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;

import net.cn_good_grass.vs_orbit.procedures.gravitation.core.math.GetParticleGravitation;
import org.joml.Vector3d;

public class ParticleUpdate {
    public static void AccelerationUpdate(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            Vector3d Gravitation = GetParticleGravitation.GetParticleGravitationForAllParticle(World, particle); //获取加速度

            if (particle.start.equals("fixed")) { continue; } //如果质点不应该参与运动就不更新

            particle.x_acceleration = Gravitation.x; //更新加速度
            particle.y_acceleration = Gravitation.y;
            particle.z_acceleration = Gravitation.z;
        }
    }


    public static void SpeedUpdates(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            Vector3d Gravitation = new Vector3d(particle.x_acceleration, particle.y_acceleration, particle.z_acceleration); //获取加速度

            double time = Config.TICK_TIME.get(); //单位时间

            particle.x_speed += time * Gravitation.x; //更新速度
            particle.y_speed += time * Gravitation.y;
            particle.z_speed += time * Gravitation.z;
        }
    }

    public static void LocationUpdates(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            Vector3d Speed = new Vector3d(particle.x_speed, particle.y_speed, particle.z_speed); //获取速度

            double time = Config.TICK_TIME.get(); //单位时间

            if (!particle.start.equals("common")) { continue; } //如果质点不应该参与运动就不更新

            particle.x += time * Speed.x; //更新位置
            particle.y += time * Speed.y;
            particle.z += time * Speed.z;
        }
    }
}
