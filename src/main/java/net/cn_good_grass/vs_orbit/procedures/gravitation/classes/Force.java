package net.cn_good_grass.vs_orbit.procedures.gravitation.classes;

public class Force {
    public String name;
    public double x, y, z;
    public double time;

    public Force(String name, double x, double y, double z, double time) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
    }

    @Override
    public String toString() {
        return "{name:\"%s\",x:%s,y:%s,z:%s,time:%s}".formatted(this.name, this.x, this.y, this.z, this.time);
    }

    public void add(Force other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }
}
