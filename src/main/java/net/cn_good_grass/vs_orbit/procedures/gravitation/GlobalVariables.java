package net.cn_good_grass.vs_orbit.procedures.gravitation;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {
    public static double core_tick_speed = Config.TICK_SPEED.get();
    public static double core_tick_time = Config.TICK_TIME.get();

    public static List<GravitationWorld> Gravitation_Core_AllWorld = new ArrayList<>(); //存重力世界数据的

    public static JsonObject StarStateData = new JsonObject();
}
