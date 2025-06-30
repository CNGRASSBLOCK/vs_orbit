package net.cn_good_grass.vs_orbit.procedures.cosmos;

import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.world.level.Level;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarMap {
    public class StarMapData {
        public Vector2d pos;
        public String type;

        public StarMapData(Vector2d pos, String type) {
            this.pos = pos;
            this.type = type;
        }
    }

    public ArrayList<StarMapData> getStarMap(Level world) {
        List<Vector2d> StarList = new ArrayList<>();
        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(world.dimension().location().toString());
        for(Astronomical astronomical : astronomicalPool.getAllAstronomical()) if (astronomical.type.equals("cosmos:star") || astronomical.type.equals("cosmos:planet")) StarList.add(new Vector2d(astronomical.x, astronomical.z));

        double[] circle = calculateMinimumBoundingCircle(StarList);

        return new ArrayList<>();
    }

    public static double[] calculateMinimumBoundingCircle(List<Vector2d> points) {
        if (points == null || points.isEmpty()) return new double[]{0, 0, 0};
        List<Vector2d> shuffled = new ArrayList<>(points);
        java.util.Collections.shuffle(shuffled, new Random());
        Circle circle = welzl(shuffled, new ArrayList<>(), shuffled.size());
        return new double[]{circle.center.x, circle.center.y, circle.radius};
    }

    private static class Circle {
        Vector2d center;
        double radius;
        Circle(Vector2d center, double radius) {
            this.center = center;
            this.radius = radius;
        }
        boolean contains(Vector2d p) { return center.distance(p) <= radius + 1e-9; }
    }
    private static Circle welzl(List<Vector2d> points, List<Vector2d> boundary, int n) {
        if (n == 0 || boundary.size() == 3) {
            switch (boundary.size()) {
                case 0: return new Circle(new Vector2d(0, 0), 0);
                case 1: return new Circle(new Vector2d(boundary.get(0)), 0);
                case 2: return circleFromTwoPoints(boundary.get(0), boundary.get(1));
                default: return circleFromThreePoints(boundary.get(0), boundary.get(1), boundary.get(2));
            }
        }
        int idx = new Random().nextInt(n);
        Vector2d p = points.get(idx);
        swap(points, idx, n - 1);
        Circle d = welzl(points, new ArrayList<>(boundary), n - 1);
        if (d.contains(p)) return d;
        boundary.add(p);
        return welzl(points, new ArrayList<>(boundary), n - 1);
    }
    private static Circle circleFromTwoPoints(Vector2d a, Vector2d b) {
        Vector2d center = new Vector2d(a).add(b).mul(0.5);
        double radius = a.distance(b) * 0.5;
        return new Circle(center, radius);
    }
    private static Circle circleFromThreePoints(Vector2d a, Vector2d b, Vector2d c) {
        double bx = b.x - a.x;
        double by = b.y - a.y;
        double cx = c.x - a.x;
        double cy = c.y - a.y;

        double b2 = bx * bx + by * by;
        double c2 = cx * cx + cy * cy;
        double d = 2 * (bx * cy - by * cx);

        if (d == 0) {
            double ab = a.distance(b);
            double bc = b.distance(c);
            double ac = a.distance(c);
            if (ab >= bc && ab >= ac) return circleFromTwoPoints(a, b); else if (bc >= ab && bc >= ac) return circleFromTwoPoints(b, c); else return circleFromTwoPoints(a, c);
        }
        double centerX = a.x + (cy * b2 - by * c2) / d;
        double centerY = a.y + (bx * c2 - cx * b2) / d;
        Vector2d center = new Vector2d(centerX, centerY);
        double radius = center.distance(a);
        return new Circle(center, radius);
    }
    private static void swap(List<Vector2d> list, int i, int j) {
        Vector2d temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
