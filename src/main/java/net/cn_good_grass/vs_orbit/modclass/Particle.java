package net.cn_good_grass.vs_orbit.modclass;

import java.math.BigInteger;

public class Particle {
    public Integer id = 0; //ID
    public String name = ""; //名字

    public String start = "common"; //质点状态

    public double x = 0; //位置
    public double y = 0;
    public double z = 0;

    public double mass = 0; //质量

    public double x_speed = 0; //速度
    public double y_speed = 0;
    public double z_speed = 0;

    public double x_acceleration = 0; //加速度
    public double y_acceleration = 0;
    public double z_acceleration = 0;

    @Override
    public String toString() {
        return "{name:" + name + ",id:" + id + ",pos(" + x + "," + y + "," + z + ")}";
    }
}
