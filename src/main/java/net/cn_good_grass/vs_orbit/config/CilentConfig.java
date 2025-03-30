package net.cn_good_grass.vs_orbit.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CilentConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder MAIN = new ForgeConfigSpec.Builder();
    //下面是Render的
    public static final ForgeConfigSpec.ConfigValue<Double> SPEED_SHOW_SCALING;
    public static final ForgeConfigSpec.ConfigValue<Double> ACCELERATION_SHOW_SCALING;

    static {
        MAIN.push("Render");
        SPEED_SHOW_SCALING = MAIN.comment("速度渲染的缩放").define("speed_show_scaling", 1.0);
        ACCELERATION_SHOW_SCALING = MAIN.comment("加速度渲染的缩放").define("acceleration_show_scaling", 1.0);
        MAIN.pop();

        SPEC = MAIN.build();
    }
}
