package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
import org.joml.Vector3d;

import static java.lang.Double.NaN;

public class AstronomicalGravitation {
    public static void UpDateAstronomicalGravitationForAllAstronomical(AstronomicalPool World, Astronomical this_astronomical) {
        if (this_astronomical == null || World == null) return; // 防止崩溃

        Vector3d AstronomicalGravitation = new Vector3d(0, 0, 0); //计算质点总引力
        for (Astronomical other_astronomical : World.getAllAstronomical()) {
            if (other_astronomical.equals(this_astronomical) || !other_astronomical.compute) continue;
            Vector3d OneAstronomicalGravitation = GetAstronomicalGravitationForOneAstronomical(this_astronomical, other_astronomical);
            AstronomicalGravitation.x += OneAstronomicalGravitation.x;
            AstronomicalGravitation.y += OneAstronomicalGravitation.y;
            AstronomicalGravitation.z += OneAstronomicalGravitation.z;
        }

        this_astronomical.addForce(new Force("Gravitation", AstronomicalGravitation.x * this_astronomical.mass, AstronomicalGravitation.y * this_astronomical.mass, AstronomicalGravitation.z * this_astronomical.mass, NaN));
    }

    public static Vector3d GetAstronomicalGravitationForOneAstronomical(Astronomical this_astronomical, Astronomical other_astronomical) {
        if (this_astronomical == null || other_astronomical == null) return new Vector3d(0, 0, 0);

        double dx = other_astronomical.x - this_astronomical.x;
        double dy = other_astronomical.y - this_astronomical.y;
        double dz = other_astronomical.z - this_astronomical.z;

        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq == 0.0) return new Vector3d(0, 0, 0);

        double factor = VSOrbitModConfig.Gravitation_GRAVITATIONAL_CONSTANT.get() * other_astronomical.mass / (distanceSq * Math.sqrt(distanceSq));

        return new Vector3d(factor * dx, factor * dy, factor * dz);
    }
}
