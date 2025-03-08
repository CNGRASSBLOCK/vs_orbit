package net.cn_good_grass.vs_orbit.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    //下面是core的
    public static final ForgeConfigSpec.ConfigValue<Integer> TICK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Double> TICK_TIME;

    static {
        BUILDER.push("core");
        TICK_SPEED = BUILDER.comment("每秒多少次刻").define("tick_speed", 20);
        TICK_TIME = BUILDER.comment("单位刻相当于现实的多少秒").define("tick_time", 0.05);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
