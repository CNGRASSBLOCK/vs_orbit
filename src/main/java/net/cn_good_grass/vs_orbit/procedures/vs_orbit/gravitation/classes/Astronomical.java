package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Astronomical {
    public final Integer id; //ID
    public final String name; //名字

    public final String type; //注册名

    public boolean compute; //质点状态

    public double x, y, z; //位置

    public double x_speed = 0; //速度
    public double y_speed = 0;
    public double z_speed = 0;

    public Quaterniond rotate; //自转
    public double rotate_speed = 0; //自转速度

    public double mass; //质量

    private final List<Force> forces = new ArrayList<>();

    public CompoundTag Tag = new CompoundTag();

    @Override public String toString() { return "{name:\"%s\",id:%d,type:\"%s\",pos(%s,%s,%s)}".formatted(name, id, type, x, y, z); }
    @Override public boolean equals(Object obj) { if (obj == null) return false; return Objects.equals(id, ((Astronomical) obj).id) && Objects.equals(name, ((Astronomical) obj).name) && Objects.equals(compute, ((Astronomical) obj).compute) && Objects.equals(mass, ((Astronomical) obj).mass); }
    @Override public int hashCode() { return Objects.hash(id, name, compute, mass); }

    public Astronomical(int id, String name, String type, boolean compute, double mass, double x, double y, double z) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.compute = compute;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotate = new Quaterniond(0, 1, 0, 0);
    }

    public boolean addForce(Force force){
        synchronized(forces) {
            if (forces == null) return false;
            if (forces.contains(force)) {
                forces.set(forces.indexOf(force), force);
                return false;
            }
            return forces.add(force);
        }
    }

    public boolean removeForce(Force force){
        synchronized(forces) {
            if (forces == null) return false;
            return forces.remove(force);
        }
    }

    public void removeAllForce() { synchronized(forces) { forces.clear(); } }

    public Force getAllForce(){
        Force allForce = new Force("all-force", 0, 0, 0, 0);
        for (Force force : forces) allForce.add(force);
        return allForce;
    }


    public void forceTimeUpdata(double time) {
        Force rforce = null;
        for (Force force : forces) {
            force.time -= time;
            if (force.time < 0) rforce = force;
        }
        removeForce(rforce);
    }

    public Vector3d getAcceleration(){
        if (this.mass == 0) return new Vector3d(0, 0, 0);
        return new Vector3d(this.getAllForce().x / this.mass, this.getAllForce().y / this.mass, this.getAllForce().z / this.mass); //获取加速度
    }

    public JsonObject toJsonObject() {
        JsonObject Astronomical_json = new JsonObject();

        Astronomical_json.addProperty("id", this.id);
        Astronomical_json.addProperty("name", this.name);
        Astronomical_json.addProperty("type", this.type);

        Astronomical_json.addProperty("compute", this.compute);

        JsonArray pos = new JsonArray();
        pos.add(this.x);
        pos.add(this.y);
        pos.add(this.z);
        Astronomical_json.add("pos", pos);
        Astronomical_json.addProperty("mass", this.mass);

        JsonArray speed = new JsonArray();
        speed.add(this.x_speed);
        speed.add(this.y_speed);
        speed.add(this.z_speed);
        Astronomical_json.add("speed", speed);

        JsonArray rotate = new JsonArray();
        rotate.add(this.rotate.x);
        rotate.add(this.rotate.y);
        rotate.add(this.rotate.z);
        rotate.add(this.rotate.w);
        Astronomical_json.add("rotate", rotate);
        Astronomical_json.addProperty("rotate_speed", this.rotate_speed);

        JsonObject Force_json = new JsonObject();
        for (Force force : forces) Force_json.add(force.name, force.toJsonObject());
        Astronomical_json.add("force", Force_json);

        Astronomical_json.addProperty("tag", this.Tag.toString());

        return Astronomical_json;
    }

    @Nullable
    public static Astronomical getFromJsonObject(JsonObject jsonObject) {
        int id;
        String name;
        String type;
        boolean compute;
        double x, y, z;
        double mass;
        if (jsonObject.has("id")) id = jsonObject.get("id").getAsInt(); else return null;
        if (jsonObject.has("name")) name = jsonObject.get("name").getAsString(); else return null;
        if (jsonObject.has("type")) type = jsonObject.get("type").getAsString(); else return null;
        if (jsonObject.has("compute")) compute = jsonObject.get("compute").getAsBoolean(); else return null;
        if (jsonObject.has("pos")) {
            List<JsonElement> pos = jsonObject.get("pos").getAsJsonArray().asList();
            if (pos.size() != 3) return null;
            x = pos.get(0).getAsDouble();
            y = pos.get(1).getAsDouble();
            z = pos.get(2).getAsDouble();
        } else return null;
        if (jsonObject.has("mass")) mass = jsonObject.get("mass").getAsDouble(); else return null;

        Astronomical astronomical = new Astronomical(id, name, type, compute, mass, x, y, z);

        if (jsonObject.has("speed")) {
            List<JsonElement> speed = jsonObject.get("speed").getAsJsonArray().asList();
            if (speed.size() != 3) return null;
            astronomical.x_speed = speed.get(0).getAsDouble();
            astronomical.y_speed = speed.get(1).getAsDouble();
            astronomical.z_speed = speed.get(2).getAsDouble();
        } else return null;

        if (jsonObject.has("rotate")) {
            List<JsonElement> rotate = jsonObject.get("rotate").getAsJsonArray().asList();
            if (rotate.size() != 4) return null;
            astronomical.rotate = new Quaterniond(rotate.get(0).getAsDouble(), rotate.get(1).getAsDouble(), rotate.get(2).getAsDouble(), rotate.get(3).getAsDouble());
        }
        if (jsonObject.has("rotate_speed")) astronomical.rotate_speed = jsonObject.get("rotate_speed").getAsDouble();

        if (jsonObject.has("force")) {
            JsonObject Force_json = jsonObject.get("force").getAsJsonObject();
            for (String key : Force_json.keySet()) {
                Force force = Force.getFromJsonObject(Force_json.getAsJsonObject(key));
                if (force != null) astronomical.addForce(force);
            }
        }

        if (jsonObject.has("tag")) { try { astronomical.Tag = TagParser.parseTag(jsonObject.get("tag").getAsString()); } catch (Exception ignored) { } }

        return astronomical;
    }
}
