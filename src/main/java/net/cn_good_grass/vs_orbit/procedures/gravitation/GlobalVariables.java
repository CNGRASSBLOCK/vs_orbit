package net.cn_good_grass.vs_orbit.procedures.gravitation;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {
    public static List<GravitationWorld> Gravitation_Core_AllWorld = new ArrayList<>(); //存重力世界数据的

    public static JsonObject StarStateData = new JsonObject();
}
