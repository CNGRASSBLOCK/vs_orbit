package net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Force {
    public final String name;
    public double x, y, z;
    public double time;

    public Force(String name, double x, double y, double z, double time) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
    }

    @Override public String toString() { return "{name:\"%s\",x:%s,y:%s,z:%s,time:%s}".formatted(this.name, this.x, this.y, this.z, this.time); }

    public void add(Force other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Force force)) return false;
        return Objects.equals(name, force.name);
    }

    @Override public int hashCode() {
        return Objects.hashCode(name);
    }

    public static Vector3d decomposeForce(double forceMagnitude, Quaterniondc orientation) {
        Vector3d direction = new Vector3d(0, 0, 1);
        orientation.transform(direction);
        return direction.mul(forceMagnitude);
    }

    public Vector3d toVector3d() { return new Vector3d(this.x, this.y, this.z); }

    public JsonObject toJsonObject() {
        JsonObject Force_json = new JsonObject();

        Force_json.addProperty("name", this.name);

        JsonArray force = new JsonArray();
        force.add(this.x);
        force.add(this.y);
        force.add(this.z);
        Force_json.add("force", force);

        Force_json.addProperty("time", this.time);

        return Force_json;
    }

    @Nullable
    public static Force getFromJsonObject(JsonObject jsonObject) {
        String name;
        double x, y, z;
        double time;

        if (jsonObject.has("name")) name = jsonObject.get("name").getAsString(); else return null;
        if (jsonObject.has("force")) {
            List<JsonElement> force = jsonObject.get("force").getAsJsonArray().asList();
            if (force.size() != 3) return null;
            x = force.get(0).getAsDouble();
            y = force.get(1).getAsDouble();
            z = force.get(2).getAsDouble();
        } else return null;
        if (jsonObject.has("time")) time = jsonObject.get("time").getAsDouble(); else return null;

        return new Force(name, x, y, z, time);
    }



    @JsonProperty("name") public String getName() { return name; }
    @JsonProperty("x") public double getX() { return x; }
    @JsonProperty("y") public double getY() { return y; }
    @JsonProperty("z") public double getZ() { return z; }
    @JsonProperty("time") public double getTime() { return time; }
}
