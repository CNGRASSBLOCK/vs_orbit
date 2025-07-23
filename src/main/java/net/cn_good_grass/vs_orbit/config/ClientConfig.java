package net.cn_good_grass.vs_orbit.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder MAIN = new ForgeConfigSpec.Builder();
    //下面是Render的
    public static final ForgeConfigSpec.ConfigValue<Double> SPEED_SHOW_SCALING;
    public static final ForgeConfigSpec.ConfigValue<Double> ACCELERATION_SHOW_SCALING;
    //下面是OrbitPrediction的
    public static final ForgeConfigSpec.ConfigValue<Integer> OrbitPrediction_TICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> OrbitPrediction_FREQUENCY;
    public static final ForgeConfigSpec.ConfigValue<Double> OrbitPrediction_TIME;

    static {
        MAIN.push("Render");
        SPEED_SHOW_SCALING = MAIN.comment("速度渲染的缩放。\nScaling of velocity rendering.").define("speed_show_scaling", 1.0);
        ACCELERATION_SHOW_SCALING = MAIN.comment("加速度渲染的缩放。\nScaling of accelerated rendering.").define("acceleration_show_scaling", 1.0);
        MAIN.pop();

        MAIN.push("OrbitPrediction");
        OrbitPrediction_TICK = MAIN.comment("轨道预测每经过多少游戏刻进行一次。\nThe track predicts how many game ticks it passes.").define("orbit_prediction_tick", 20);
        OrbitPrediction_FREQUENCY = MAIN.comment("轨道预测步数。\nTrack predicts the number of steps.").define("orbit_prediction_frequency", 20);
        OrbitPrediction_TIME = MAIN.comment("轨道预测步长。\nOrbit predicts step size.").define("orbit_prediction_time", 1000.0);
        MAIN.pop();

        SPEC = MAIN.build();
    }
}
