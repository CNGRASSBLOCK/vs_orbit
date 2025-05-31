package net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics;

import org.joml.Quaterniond;

import java.math.BigDecimal;

public class Object extends Particle{
    public Quaterniond rotating = new Quaterniond(0, 0, 0, 0);

    public Object(Integer id, String name, String start, BigDecimal mass, Double x, Double y, Double z) {
        super(id, name, start, mass, x, y, z);
    }
}
