package net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.GravitationWorld;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ParticlePool {
    public String WorldId = "";
    private List<Particle> ParticlePool = new ArrayList<>();

    public static ParticlePool getFromWorldID(String worldID){
        ParticlePool thisParticlePool = new ParticlePool();
        for (ParticlePool particlePool : GravitationWorld.Gravitation_Core_World_Bus) {
            if (particlePool.WorldId.equals(worldID)) {
                thisParticlePool = particlePool;
                break;
            }
        }
        return thisParticlePool;
    }

    public JsonObject toJsonObject() {
        JsonObject main_json = new JsonObject();

        if (this.WorldId.isEmpty()) { return main_json; }

        main_json.addProperty("WorldID", this.WorldId);

        for (Particle particle : this.ParticlePool) {
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

            main_json.add(particle.name, particle_json);
        }

        return main_json;
    }

    public static ParticlePool getFromJsonObject(JsonObject json) {
        ParticlePool newWorld = new ParticlePool();

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

            newWorld.ParticlePool.add(particle);
        }

        return newWorld;
    }

    public boolean addParticle(Particle particle) {
        for (Particle particle1 : ParticlePool) if (particle1.id == particle.id) return false;
        ParticlePool.add(particle);
        return true;
    }

    public boolean removeParticle(int id) {
        ParticlePool.removeIf(particle1 -> particle1.id == id);
        return true;
    }

    public Particle getParticle(int id) {
        for (Particle particle1 : ParticlePool) if (particle1.id == id) return particle1;
        return null;
    }

    public boolean setParticle(Particle particle) {
        for (Particle particle1 : ParticlePool) if (particle1.id == particle.id) {
            ParticlePool.set(ParticlePool.indexOf(particle1), particle);
            return true;
        }
        return false;
    }

    public List<Particle> getGravitationCoreWorld() {
        return new ArrayList<>(ParticlePool);
    }
}
