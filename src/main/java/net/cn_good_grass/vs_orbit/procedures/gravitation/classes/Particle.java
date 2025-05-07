package net.cn_good_grass.vs_orbit.procedures.gravitation.classes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Particle {
    public Integer id; //ID
    public String name; //名字

    public String start; //质点状态

    public double x; //位置
    public double y;
    public double z;

    public BigDecimal mass; //质量

    public double x_speed = 0; //速度
    public double y_speed = 0;
    public double z_speed = 0;

    public double x_acceleration = 0; //加速度
    public double y_acceleration = 0;
    public double z_acceleration = 0;

    public List<Force> forces = new ArrayList<>();

    @Override public String toString() { return "{name:\"%s\",id:%d,pos(%s,%s,%s)}".formatted(name, id, x, y, z); }

    public Particle(Integer id, String name, String start, BigDecimal mass, Double x, Double y, Double z) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
