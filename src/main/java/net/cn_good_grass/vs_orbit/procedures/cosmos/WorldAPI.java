package net.cn_good_grass.vs_orbit.procedures.cosmos;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

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
        double PlanetRadius = astronomical.Tag.getCompound("cosmos:data").getDouble("scale") / 2;
        double scalar = PlanetRadius / MapRadius;

        Vector2d map_absolute_pos = new Vector2d(position).sub(MapCenter);
        map_absolute_pos = new Matrix2d().rotate(java.lang.Math.toRadians(MapRotate)).transform(map_absolute_pos, new Vector2d());

        PlanetRadius = PlanetRadius * 1.25;

        Vector3d out_pos = new Vector3d();
        Vector2d map_relatively_pos;
        if (ProjectionMethod.equals("preset_1")) {
            if (-MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius / 3.0 && -MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius / 3.0) {
                //中心-顶面
                map_relatively_pos = new Vector2d(map_absolute_pos);
                map_relatively_pos.mul(scalar);

                out_pos = new Quaterniond().transform(new Vector3d(map_relatively_pos.x(), PlanetRadius, map_relatively_pos.y()), new Vector3d());
            } else if (-MapRadius / 3.0 <= map_absolute_pos.x && map_absolute_pos.x <= MapRadius / 3.0 && -MapRadius / 3.0 <= map_absolute_pos.y && map_absolute_pos.y <= MapRadius / 3.0) {
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
        }

        out_pos = new Quaterniond(astronomical.rotate).transform(out_pos, new Vector3d());

        return out_pos.add(new Vector3d(astronomical.x, astronomical.y, astronomical.z));
    }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vector3d position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }

    public static Vector3d getSpacePosFormWorldPos(Level world, Vec3 position) { return getSpacePosFormWorldPos(world, new Vector2d(position.x(), position.z())); }



    public static Vector3d getTexturePosFormSpacePos(Astronomical astronomical, Vector3d position) {
        double PlanetRadius = astronomical.Tag.getCompound("cosmos:data").getDouble("scale") / 2;

        //顶面
        Vector3d[] plane = new Vector3d[]{new Vector3d(astronomical.x, astronomical.y + PlanetRadius, astronomical.z), new Vector3d(1,0,0).rotate(astronomical.rotate), new Vector3d(0,0,1).rotate(astronomical.rotate)};

        Vector3d ProjectionPos_UP = Math.getPointProjection(plane, position);

//        if (java.lang.Math.abs(plane_intersect[0].x()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[0].z()) > PlanetRadius + 1e-6) plane_intersect[0] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        //底面
//        plane[0] = new Vector3d(0, -PlanetRadius, 0);
//        plane[1] = new Vector3d(0, -1, 0);
//        plane_intersect[1] = Math.getPlaneIntersection(plane, rotate_position);
//        plane_pos[1] = new Vector2d(plane_intersect[1].x(), plane_intersect[1].z());
//        plane_pos[1].mul(0.5 / PlanetRadius).add(0.5, 0.5);
//        if (java.lang.Math.abs(plane_intersect[1].x()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[1].z()) > PlanetRadius + 1e-6) plane_intersect[1] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        //北面
//        plane[0] = new Vector3d(0, 0, -PlanetRadius);
//        plane_intersect[1] = new Vector3d(0, 0, -1);
//        plane_intersect[2] = Math.getPlaneIntersection(plane, rotate_position);
//        plane_pos[2] = new Vector2d(plane_intersect[2].x(), plane_intersect[2].y());
//        plane_pos[2].mul(0.5 / PlanetRadius).add(0.5, 0.5);
//        if (java.lang.Math.abs(plane_intersect[2].x()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[2].y()) > PlanetRadius + 1e-6) plane_intersect[2] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        //南面
//        plane[0] = new Vector3d(0, 0, PlanetRadius);
//        plane[1] = new Vector3d(0, 0, 1);
//        plane_intersect[3] = Math.getPlaneIntersection(plane, rotate_position);
//        plane_pos[3] = new Vector2d(plane_intersect[3].x(), plane_intersect[3].y());
//        plane_pos[3].mul(0.5 / PlanetRadius).add(0.5, 0.5);
//        if (java.lang.Math.abs(plane_intersect[3].x()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[3].y()) > PlanetRadius + 1e-6) plane_intersect[3] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        //东面
//        plane[0] = new Vector3d(PlanetRadius,0, 0);
//        plane[1] = new Vector3d(1, 0, 0);
//        plane_intersect[4] = Math.getPlaneIntersection(plane, rotate_position);
//        plane_pos[4] = new Vector2d(plane_intersect[4].y(), plane_intersect[4].z());
//        plane_pos[4].mul(0.5 / PlanetRadius).add(0.5, 0.5);
//        if (java.lang.Math.abs(plane_intersect[4].y()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[4].z()) > PlanetRadius + 1e-6) plane_intersect[4] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//        //西面
//        plane[0] = new Vector3d(-PlanetRadius,0, 0);
//        plane[1] = new Vector3d(-1, 0, 0);
//        plane_intersect[5] = Math.getPlaneIntersection(plane, rotate_position);
//        plane_pos[5] = new Vector2d(plane_intersect[5].y(), plane_intersect[5].z());
//        plane_pos[5].mul(0.5 / PlanetRadius).add(0.5, 0.5);
//        if (java.lang.Math.abs(plane_intersect[5].y()) > PlanetRadius + 1e-6 || java.lang.Math.abs(plane_intersect[5].z()) > PlanetRadius + 1e-6) plane_intersect[5] = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//
//
//        plane_intersect[1] = new Vector3d(Double.MAX_VALUE);
//        plane_intersect[2] = new Vector3d(Double.MAX_VALUE);
//        plane_intersect[3] = new Vector3d(Double.MAX_VALUE);
//        plane_intersect[4] = new Vector3d(Double.MAX_VALUE);
//        plane_intersect[5] = new Vector3d(Double.MAX_VALUE);
//        double min_intersect_length = Double.MAX_VALUE;
//        int min_intersect_length_pos = 0;
//        for (int i = 0; i < plane_intersect.length; i++) if (new Vector3d(rotate_position).sub(plane_intersect[i]).length() < min_intersect_length) {
//            min_intersect_length = new Vector3d(rotate_position).sub(plane_intersect[i]).length();
//            min_intersect_length_pos = i;
//        }

        return ProjectionPos_UP;
    }

    public static Vector3d getTexturePosFormSpacePos(Astronomical astronomical, Vec3 position) { return getTexturePosFormSpacePos(astronomical, new Vector3d(position.x(), position.y(), position.z())); }



    static class Math {
        public static Vector3d getPointProjection(Vector3d[] plane, Vector3d point) {
            if (plane.length != 3) return new Vector3d();

            Vector3d P = new Vector3d(plane[0]);
            Vector3d X = new Vector3d(plane[1]);
            Vector3d Y = new Vector3d(plane[2]);

            if (java.lang.Math.abs(X.dot(Y) / (X.length() * Y.length())) > 0.001) return new Vector3d(); // 非正交则退出

            double Xr = point.dot(X) / X.length() / point.length();
            double Yr = point.dot(Y) / Y.length() / point.length();

            return new Vector3d(P).add(new Vector3d(X).normalize().mul(Xr * point.length())).add(new Vector3d(Y).normalize().mul(Yr * point.length()));
        }
    }
}
