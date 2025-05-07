package net.cn_good_grass.vs_orbit.procedures.gravitation.classes;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.WorldOperate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GravitationPool {
    public String WorldId = "";
    public List<Particle> Gravitation_Core_World = new ArrayList<>();

    public static GravitationPool getFromWorldID(String worldID){
        GravitationPool thisGravitationPool = new GravitationPool();
        for (GravitationPool gravitationPool : WorldOperate.Gravitation_Core_World_Bus) {
            if (gravitationPool.WorldId.equals(worldID)) {
                thisGravitationPool = gravitationPool;
                break;
            }
        }
        return thisGravitationPool;
    }

    public JsonObject toJsonObject() {
        JsonObject main_json = new JsonObject();

        if (this.WorldId.isEmpty()) { return main_json; }

        main_json.addProperty("WorldID", this.WorldId);

        for (Particle particle : this.Gravitation_Core_World) {
            JsonObject particle_json = new JsonObject();

            particle_json.addProperty("id", particle.id);

            particle_json.addProperty("start", particle.start);

            particle_json.addProperty("x", particle.x);
            particle_json.addProperty("y", particle.y);
            particle_json.addProperty("z", particle.z);

            particle_json.addProperty("mass", particle.mass);

            particle_json.addProperty("x_speed", particle.x_speed);
            particle_json.addProperty("y_speed", particle.y_speed);
            particle_json.addProperty("z_speed", particle.z_speed);

            particle_json.addProperty("x_acceleration", particle.x_acceleration);
            particle_json.addProperty("y_acceleration", particle.y_acceleration);
            particle_json.addProperty("z_acceleration", particle.z_acceleration);

            main_json.add(particle.name, particle_json);
        }

        return main_json;
    }

    public static GravitationPool getFromJsonObject(JsonObject json) {
        GravitationPool newWorld = new GravitationPool();

        if (json.has("WorldID")) { newWorld.WorldId = json.get("WorldID").getAsString(); } else { return null; }

        for (String key : json.keySet()) {
            if (key.equals("WorldID")) { continue; }
            JsonObject particle_json = json.getAsJsonObject(key);

            int id = 0;
            String start = "common";
            double x = 0;
            double y = 0;
            double z = 0;
            BigDecimal mass = new BigDecimal(0);
            if (particle_json.has("id")) { id = particle_json.get("id").getAsInt(); }
            if (particle_json.has("start")) { start = particle_json.get("start").getAsString(); }
            if (particle_json.has("x")) { x = particle_json.get("x").getAsDouble(); }
            if (particle_json.has("y")) { y = particle_json.get("y").getAsDouble(); }
            if (particle_json.has("z")) { z = particle_json.get("z").getAsDouble(); }
            if (particle_json.has("mass")) { mass = particle_json.get("mass").getAsBigDecimal(); }

            Particle particle = new Particle(id, key, start, mass, x, y, z);

            if (particle_json.has("x_speed")) { particle.x_speed = particle_json.get("x_speed").getAsDouble(); }
            if (particle_json.has("y_speed")) { particle.y_speed = particle_json.get("y_speed").getAsDouble(); }
            if (particle_json.has("z_speed")) { particle.z_speed = particle_json.get("z_speed").getAsDouble(); }
            if (particle_json.has("x_acceleration")) { particle.x_acceleration = particle_json.get("x_acceleration").getAsDouble(); }
            if (particle_json.has("y_acceleration")) { particle.y_acceleration = particle_json.get("y_acceleration").getAsDouble(); }
            if (particle_json.has("z_acceleration")) { particle.z_acceleration = particle_json.get("z_acceleration").getAsDouble(); }

            newWorld.Gravitation_Core_World.add(particle);
        }

        return newWorld;
    }
}
