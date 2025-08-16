package net.cn_good_grass.vs_orbit.procedures.cosmos;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix2d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.List;

public abstract class WorldAPI {
    public static Vector3d getSpacePosFormWorldPos(Level world, Vector2d position) {
        if (!VSOrbitDataPack.PlanetWorld.contains(world.dimension().location().toString())) return new Vector3d();

        JsonObject WorldData = VSOrbitDataPack.PlanetData.getAsJsonObject(world.dimension().location().toString());

        List<JsonElement> CenterData = WorldData.get("center_position").getAsJsonArray().asList();
        if (CenterData.size() != 2) return new Vector3d();
         String ProjectionMethod = WorldData.get("position_projection_method").getAsString();
        Vector2d Center = new Vector2d(CenterData.get(0).getAsDouble(), CenterData.get(1).getAsDouble());
        double Radius = WorldData.get("radius").getAsDouble();
        double Rotate = WorldData.get("rotate").getAsDouble();

        Astronomical astronomical = StarAPI.getAstronomicalFormLevel(world);
        if (astronomical == null) return new Vector3d();

        Vector2d map_pos = new Vector2d(position).sub(Center);
        map_pos = new Matrix2d().rotate(Math.toRadians(Rotate)).transform(map_pos, new Vector2d());

        Vector3d out_pos = new Vector3d();
        if (ProjectionMethod.equals("preset_1")) {
            double scale = astronomical.Tag.getCompound("CelestialBodyData").getDouble("scale") / 2.0;
            map_pos.mul(scale / Radius);
            out_pos = new Vector3d(map_pos.x(), scale, map_pos.y());
            if (-Radius / 3.0 <= map_pos.x && map_pos.x <= Radius / 3.0 && -Radius / 3.0 <= map_pos.y && map_pos.y <= Radius / 3.0)
                out_pos = new Quaterniond().transform(out_pos, new Vector3d());  //中心-顶面
            else if (-Radius / 3.0 <= map_pos.x && map_pos.x <= Radius / 3.0 && Radius / 3.0 <= map_pos.y && map_pos.y <= Radius)
                out_pos = new Quaterniond(-0.7071, 0, 0, 0.7071).transform(out_pos, new Vector3d()); //上-北
            else if (-Radius / 3.0 <= map_pos.x && map_pos.x <= Radius / 3.0 && -Radius <= map_pos.y && map_pos.y <= -Radius / 3.0)
                out_pos = new Quaterniond(0.7071, 0, 0, 0.7071).transform(out_pos, new Vector3d()); //下-南
            else if (-Radius <= map_pos.x && map_pos.x <= -Radius / 3.0 && -Radius / 3.0 <= map_pos.y && map_pos.y <= Radius / 3.0)
                out_pos = new Quaterniond(0, 0, 0.7071, 0.7071).transform(out_pos, new Vector3d()); //左-西
            else if (Radius / 3.0 <= map_pos.x && map_pos.x <= Radius && -Radius / 3.0 <= map_pos.y && map_pos.y <= Radius / 3.0)
                out_pos = new Quaterniond(0, 0, -0.7071, 0.7071).transform(out_pos, new Vector3d()); //右-东
            out_pos = astronomical.rotate.transform(out_pos, new Vector3d());
        }

        return out_pos.add(new Vector3d(astronomical.x, astronomical.y, astronomical.z));
    }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vector3d position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vec3 position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }
}
