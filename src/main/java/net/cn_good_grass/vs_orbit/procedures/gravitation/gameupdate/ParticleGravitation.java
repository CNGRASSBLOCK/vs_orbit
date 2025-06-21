package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import org.joml.Vector3d;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Double.NaN;

public class ParticleGravitation {
    public static void UpDateParticleGravitationForAllParticle(ParticlePool World, Particle this_particle) {
        if (this_particle == null || World == null) return; // 防止崩溃

        Vector3d ParticleGravitation = new Vector3d(0, 0, 0); //计算质点总引力
        for (Particle other_particle : World.getAllParticle()) {
            if (other_particle.equals(this_particle)) continue;
            Vector3d OneParticleGravitation = GetParticleGravitationForOneParticle(this_particle, other_particle);
            ParticleGravitation.x += OneParticleGravitation.x;
            ParticleGravitation.y += OneParticleGravitation.y;
            ParticleGravitation.z += OneParticleGravitation.z;
        }

        this_particle.addForce(new Force("Gravitation", BigDecimal.valueOf(ParticleGravitation.x).multiply(this_particle.mass), BigDecimal.valueOf(ParticleGravitation.y).multiply(this_particle.mass), BigDecimal.valueOf(ParticleGravitation.z).multiply(this_particle.mass), NaN));
    }

    public static Vector3d GetParticleGravitationForOneParticle(Particle this_particle, Particle other_particle) {
        if (this_particle == null || other_particle == null) { return new Vector3d(0, 0, 0); } // 防止崩溃

        double x_difference = other_particle.x - this_particle.x; //获取当前质点在世界坐标系与目标质点之间的距离
        double y_difference = other_particle.y - this_particle.y;
        double z_difference = other_particle.z - this_particle.z;

        double planar_distance = Math.sqrt(Math.pow(x_difference, 2) + Math.pow(z_difference, 2)); //获取当前质点与目标质点在X-Z平面之间的之间距离
        double world_distance = Math.sqrt(Math.pow(x_difference, 2) + Math.pow(y_difference, 2) + Math.pow(z_difference, 2)); //获取船只与星体之间距离

        double G = Config.Gravitation_GRAVITATIONAL_CONSTANT.get(); //引力常量

        BigDecimal A = new BigDecimal(G).multiply(other_particle.mass).divide(BigDecimal.valueOf(Math.pow(world_distance, 2)), 32, RoundingMode.HALF_UP); //万有引力公式(变式 A=G*(m星/r距²)

        double planar_x_range = Math.atan2(y_difference, planar_distance);
        double world_x_range = Math.atan2(z_difference, x_difference);

        BigDecimal planar_X_A = BigDecimal.valueOf(Math.cos(planar_x_range)).multiply(A);  //在planar_distance-Y参考系中X轴上的力

        double X_acceleration = (BigDecimal.valueOf(Math.cos(world_x_range)).multiply(planar_X_A)).doubleValue(); //在世界参考系中X轴上的力
        double Y_acceleration = (BigDecimal.valueOf(Math.sin(planar_x_range)).multiply(A)).doubleValue();         //在世界参考系中Y轴上的力
        double Z_acceleration = (BigDecimal.valueOf(Math.sin(world_x_range)).multiply(planar_X_A)).doubleValue(); //在世界参考系中Z轴上的力

        return new Vector3d(X_acceleration, Y_acceleration, Z_acceleration); //返回
    }
}
