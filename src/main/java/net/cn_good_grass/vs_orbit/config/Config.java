package net.cn_good_grass.vs_orbit.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder MAIN = new ForgeConfigSpec.Builder();
    //下面是Core的
    public static final ForgeConfigSpec.ConfigValue<Integer> Core_TICK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Double> Core_TICK_TIME;
    //下面是Gravitation的
    public static final ForgeConfigSpec.ConfigValue<List<String>> Gravitation_WORK_WORLD;
    public static final ForgeConfigSpec.ConfigValue<Double> Gravitation_GRAVITATIONAL_CONSTANT;
    //下面是ValkyrienSkies的
    public static final ForgeConfigSpec.ConfigValue<Boolean> ValkyrienSkies_ENABLE;
    public static final ForgeConfigSpec.ConfigValue<Double> ValkyrienSkies_ACCELERATION_SCALING;

    static {
        MAIN.push("Core");
        Core_TICK_SPEED = MAIN.comment("每秒多少次刻。\nHow many ticks per second.\n请注意:当瓦尔基里的运算速度与本mod物理核心运算速度不一致时将会出现严重的精度问题。\nPlease note: Serious accuracy issues will occur when Valkyrie's speed does not match the speed of the mod's physics core.").define("tick_speed", 100);
        Core_TICK_TIME = MAIN.comment("单位刻相当于现实的多少秒。\nThe unit tick is equivalent to the number of seconds in reality.\n当目标速度为1.0时该值应为(1除以Core_TICK_SPEED)所得到的值。\nWhen the target speed is 1.0, the value should be (1 divided by Core_TICK_SPEED).").define("tick_time", 0.01);
        MAIN.pop();

        MAIN.push("Gravitation");
        Gravitation_WORK_WORLD = MAIN.comment("重力计算在哪些世界中工作，需要维度的注册名。\nIn which worlds gravity calculations work, the registration name of the dimension is required.").define("work_world", new ArrayList<>(Arrays.asList("cosmos:solar_system")));
        Gravitation_GRAVITATIONAL_CONSTANT = MAIN.comment("引力常数。\nGravitational constant.").define("gravitational_constant", 6.676E-11);
        MAIN.pop();

        MAIN.push("ValkyrienSkies");
        ValkyrienSkies_ENABLE = MAIN.comment("是否启用对瓦尔基里的运算。\nWhether or not compatibility with Valkyrie is enabled.").define("enable", true);
        ValkyrienSkies_ACCELERATION_SCALING = MAIN.comment("物理体的加速度缩放。\nAcceleration scaling of a physical body.").define("acceleration_scaling", 1.0);
        MAIN.pop();

        MAIN.push("ExperimentalFeatures");
        MAIN.pop();

        SPEC = MAIN.build();
    }
}
