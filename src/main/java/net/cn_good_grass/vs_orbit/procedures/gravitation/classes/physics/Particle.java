package net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics;

import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3d;

import java.lang.Object;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Particle {
    public final Integer id; //ID
    public final String name; //名字

    public String start; //质点状态

    public double x; //位置
    public double y;
    public double z;

    public BigDecimal mass; //质量

    public double x_speed = 0; //速度
    public double y_speed = 0;
    public double z_speed = 0;

    private List<Force> forces = new ArrayList<>();

    public CompoundTag Tag = new CompoundTag();

    @Override public String toString() { return "{name:\"%s\",id:%d,pos(%s,%s,%s)}".formatted(name, id, x, y, z); }
    @Override public boolean equals(Object obj) { if (obj == null) return false; return Objects.equals(id, ((Particle) obj).id) && Objects.equals(name, ((Particle) obj).name) && Objects.equals(start, ((Particle) obj).start) && Objects.equals(mass, ((Particle) obj).mass); }
    @Override public int hashCode() { return Objects.hash(id, name, start, mass); }

    public Particle(Integer id, String name, String start, BigDecimal mass, Double x, Double y, Double z) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean addForce(Force force){
        if (forces == null) return false;
        if (forces.contains(force)) {
            forces.set(forces.indexOf(force), force);
            return true;
        }
        return forces.add(force);
    }

    public boolean removeForce(Force force){
        if (forces == null) return false;
        return forces.remove(force);
    }

    public Force getAllForce(){
        Force allForce = new Force("name", new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), 0);
        for (Force force : forces) {
            allForce.add(force);
        }
        return allForce;
    }

    public void forceTimeUpdata(double time){
        for (Force force : forces) {
            if (force.time <= 0) forces.remove(force);
            force.time -= time;
        }
    }

    public Vector3d getAcceleration(){
        if (this.mass.doubleValue() == 0) return new Vector3d(0, 0, 0);
        return new Vector3d((this.getAllForce().x.divide(this.mass, 16, RoundingMode.HALF_UP)).doubleValue(), (this.getAllForce().y.divide(this.mass, 16, RoundingMode.HALF_UP)).doubleValue(), (this.getAllForce().z.divide(this.mass, 16, RoundingMode.HALF_UP)).doubleValue()); //获取加速度
    }
}
