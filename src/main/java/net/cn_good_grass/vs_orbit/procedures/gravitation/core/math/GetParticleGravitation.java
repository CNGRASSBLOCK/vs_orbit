package net.cn_good_grass.vs_orbit.procedures.gravitation.core.math;

import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import org.joml.Vector3d;

import java.util.List;

public class GetParticleGravitation {
    public static Vector3d GetParticleGravitationForAllParticle(GravitationWorld World, Particle this_particle) {
        if (this_particle == null || World == null) { return new Vector3d(0, 0, 0); } // 防止崩溃

        GravitationWorld thisGravitationWorld = World;

        Vector3d ParticleGravitation = new Vector3d(0, 0, 0); //计算质点总引力
        for (Particle other_particle : thisGravitationWorld.Gravitation_Core_World) {
            if (other_particle.equals(this_particle)) { continue; }
            Vector3d OneParticleGravitation = GetParticleGravitationForOneParticle(this_particle, other_particle);
            ParticleGravitation.x += OneParticleGravitation.x;
            ParticleGravitation.y += OneParticleGravitation.y;
            ParticleGravitation.z += OneParticleGravitation.z;
        }

        return ParticleGravitation; //返回
    }

    public static Vector3d GetParticleGravitationForOneParticle(Particle this_particle, Particle other_particle) {
        if (this_particle == null || other_particle == null) { return new Vector3d(0, 0, 0); } // 防止崩溃

        double ship_mass = this_particle.mass;

        double x_difference = other_particle.x - this_particle.x; //获取当前质点在世界坐标系与目标质点之间的距离
        double y_difference = other_particle.y - this_particle.y;
        double z_difference = other_particle.z - this_particle.z;

        double planar_distance = Math.sqrt(Math.pow(x_difference, 2) + Math.pow(z_difference, 2)); //获取当前质点与目标质点在X-Z平面之间的之间距离
        double world_distance = Math.sqrt(Math.pow(x_difference, 2) + Math.pow(y_difference, 2) + Math.pow(z_difference, 2)); //获取船只与星体之间距离

        double G = 6.676; //引力常量

        double F = G * ((ship_mass * other_particle.mass) / Math.pow(world_distance, 2)); //万有引力公式 F=G*(m物*m星/r距²)

        double planar_x_range = Math.atan2(y_difference, planar_distance);
        double world_x_range = Math.atan2(z_difference, x_difference);

        double planar_X_F = Math.cos(planar_x_range) * F;  //在planar_distance-Y参考系中X轴上的力

        double X_F = Math.cos(world_x_range) * planar_X_F; //在世界参考系中X轴上的力
        double Y_F = Math.sin(planar_x_range) * F;         //在世界参考系中Y轴上的力
        double Z_F = Math.sin(world_x_range) * planar_X_F; //在世界参考系中Z轴上的力

        double X_acceleration = X_F / ship_mass; //在世界参考系中X轴上的加速度
        double Y_acceleration = Y_F / ship_mass; //在世界参考系中Y轴上的加速度
        double Z_acceleration = Z_F / ship_mass; //在世界参考系中Z轴上的加速度

        return new Vector3d(X_acceleration, Y_acceleration, Z_acceleration); //返回
    }
}
