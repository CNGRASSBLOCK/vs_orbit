package net.cn_good_grass.vs_orbit.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder MAIN = new ForgeConfigSpec.Builder();
    //下面是core的
    public static final ForgeConfigSpec.ConfigValue<Integer> TICK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Double> TICK_TIME;
    //下面是gravitation的
    public static final ForgeConfigSpec.ConfigValue<List<String>> WORK_WORLD;

    static {
        MAIN.push("core");
        TICK_SPEED = MAIN.comment("每秒多少次刻").define("tick_speed", 20);
        TICK_TIME = MAIN.comment("单位刻相当于现实的多少秒").define("tick_time", 0.05);
        MAIN.pop();

        MAIN.push("gravitation");
        WORK_WORLD = MAIN.comment("重力计算在哪些世界中工作，写注册名").define("work_world", new ArrayList<String>(Arrays.asList("cosmos:solar_system")));
        MAIN.pop();

        SPEC = MAIN.build();
    }
}
