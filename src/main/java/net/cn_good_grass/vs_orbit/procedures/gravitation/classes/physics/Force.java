package net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics;

import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.math.BigDecimal;
import java.util.Objects;

public class Force {
    public String name;
    public BigDecimal x, y, z;
    public double time;

    public Force(String name, BigDecimal x, BigDecimal y, BigDecimal z, double time) {
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
        this.x = this.x.add(other.x);
        this.y = this.y.add(other.y);
        this.z = this.z.add(other.z);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Force force)) return false;
        return Objects.equals(name, force.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public static Vector3d decomposeForce(double forceMagnitude, Quaterniondc orientation) {
        Vector3d direction = new Vector3d(0, 0, 1);
        orientation.transform(direction);
        return direction.mul(forceMagnitude);
    }

    public Vector3d toVector3d(){ return new Vector3d(this.x.doubleValue(), this.y.doubleValue(), this.z.doubleValue()); }
}
