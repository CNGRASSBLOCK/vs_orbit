package net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Force;
import net.minecraft.nbt.TagParser;
import org.joml.Quaterniond;

import java.util.List;

public class SplinterAstronomical extends Astronomical {
    public SplinterAstronomical(int id, String name, boolean compute, double mass, double x, double y, double z) { super(id, name, compute, mass, x, y, z); }

    @Override
    public JsonObject toJsonObject() {
        return super.toJsonObject();
    }

    private static SplinterAstronomical FromJsonObject(JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        boolean compute = jsonObject.get("compute").getAsBoolean();
        double x = 0, y = 0, z = 0;
        double mass = jsonObject.get("mass").getAsDouble();
        List<JsonElement> pos = jsonObject.get("pos").getAsJsonArray().asList();
        if (pos.size() == 3) {
            x = pos.get(0).getAsDouble();
            y = pos.get(1).getAsDouble();
            z = pos.get(2).getAsDouble();
        }

        SplinterAstronomical astronomical = new SplinterAstronomical(id, name, compute, mass, x, y, z);

        List<JsonElement> speed = jsonObject.get("speed").getAsJsonArray().asList();
        if (speed.size() != 3) return astronomical;
        astronomical.x_speed = speed.get(0).getAsDouble();
        astronomical.y_speed = speed.get(1).getAsDouble();
        astronomical.z_speed = speed.get(2).getAsDouble();

        List<JsonElement> rotate = jsonObject.get("rotate").getAsJsonArray().asList();
        if (rotate.size() != 4) return astronomical;
        astronomical.rotate = new Quaterniond(rotate.get(0).getAsDouble(), rotate.get(1).getAsDouble(), rotate.get(2).getAsDouble(), rotate.get(3).getAsDouble());
        astronomical.rotate_speed = jsonObject.get("rotate_speed").getAsDouble();

        JsonObject Force_json = jsonObject.get("force").getAsJsonObject();
        for (String key : Force_json.keySet()) {
            Force force = Force.getFromJsonObject(Force_json.getAsJsonObject(key));
            if (force != null) astronomical.addForce(force);
        }

        try { astronomical.Tag = TagParser.parseTag(jsonObject.get("tag").getAsString()); } catch (Exception ignored) { }

        return astronomical;
    }
}
