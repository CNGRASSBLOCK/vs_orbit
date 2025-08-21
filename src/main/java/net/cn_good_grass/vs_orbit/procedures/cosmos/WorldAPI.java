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
        Astronomical astronomical = StarAPI.getAstronomicalFormLevel(world);
        if (astronomical == null) return new Vector3d();

        String ProjectionMethod = WorldData.get("position_projection_method").getAsString();
        Vector2d MapCenter = new Vector2d(CenterData.get(0).getAsDouble(), CenterData.get(1).getAsDouble());
        double MapRadius = WorldData.get("radius").getAsDouble();
        double MapRotate = WorldData.get("rotate").getAsDouble();
        double PlanetRadius = astronomical.Tag.getCompound("CelestialBodyData").getDouble("scale") / 2;
        double scalar = PlanetRadius / MapRadius;

        Vector2d map_absolute_pos = new Vector2d(position).sub(MapCenter);
        map_absolute_pos = new Matrix2d().rotate(Math.toRadians(MapRotate)).transform(map_absolute_pos, new Vector2d());

        PlanetRadius = PlanetRadius * 1.25;

        Vector3d out_pos = new Vector3d();
        Vector2d map_relatively_pos;
        if (ProjectionMethod.equals("preset_1")) {
            if (-MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius / 3.0 && -MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius / 3.0) {
                //中心-顶面
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond().transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            } else if (-MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius / 3.0 && MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius) {
                //上-北
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.y -= MapRadius / 1.5;
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond(-0.7071067811865475, 0, 0, 0.7071067811865475).transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            } else if (-MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius / 3.0 && -MapRadius <= map_absolute_pos.y && map_absolute_pos.y <= -MapRadius / 3.0) {
                //下-南
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.y += MapRadius / 1.5;
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond(0.7071067811865475, 0, 0, 0.7071067811865475).transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            } else if (-MapRadius <= map_absolute_pos.x && map_absolute_pos.x <= -MapRadius / 3.0 && -MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius / 3.0) {
                //左-西
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.x += MapRadius / 1.5;
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond(0, 0, 0.7071067811865475, 0.7071067811865475).transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            } else if (MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius && -MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius / 3.0) {
                //右-东
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.x -= MapRadius / 1.5;
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond(0, 0, -0.7071067811865475, 0.7071067811865475).transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            }
            out_pos = astronomical.rotate.transform(out_pos, new Vector3d());
        }

        return out_pos.add(new Vector3d(astronomical.x, astronomical.y, astronomical.z));
    }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vector3d position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vec3 position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }
}
