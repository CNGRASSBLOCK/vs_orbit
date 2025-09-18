package net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes;

import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.network.SyncDataTick;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.event.ServerAction;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.core.AstronomicalGravitation;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AstronomicalPool {
    public final String WorldId;
    private final List<Astronomical> astronomicalPool = new ArrayList<>();

    public AstronomicalPool(Level world) { this.WorldId = world.dimension().location().toString(); }

    public AstronomicalPool(String worldId) {
        this.WorldId = worldId;
    }

    @Nullable public static AstronomicalPool getFromWorldID(String worldID) {
        for (AstronomicalPool astronomicalPool : ServerAction.Astronomical_Core_World_Bus) if (astronomicalPool.WorldId.equals(worldID)) return astronomicalPool;
        return null;
    }

    @Nullable public static AstronomicalPool getFromWorldIDCilent(String worldID, boolean isOld) {
        if (isOld) {
            for (AstronomicalPool astronomicalPool : SyncDataTick.Old_Gravitation_Core_World_Bus) if (astronomicalPool.WorldId.equals(worldID)) return astronomicalPool;
        } else {
            for (AstronomicalPool astronomicalPool : SyncDataTick.New_Gravitation_Core_World_Bus) if (astronomicalPool.WorldId.equals(worldID)) return astronomicalPool;
        }
        return null;
    }

    public JsonObject toJsonObject() {
        JsonObject main_json = new JsonObject();

        if (this.WorldId.isEmpty()) { return main_json; }

        main_json.addProperty("WorldID", this.WorldId);

        for (Astronomical astronomical : this.astronomicalPool) main_json.add(astronomical.name, astronomical.toJsonObject());

        return main_json;
    }

    @Nullable
    public static AstronomicalPool getFromJsonObject(JsonObject json) {
        String WorldId;
        if (json.has("WorldID")) WorldId = json.get("WorldID").getAsString(); else return null;
        AstronomicalPool newWorld = new AstronomicalPool(WorldId);

        for (String key : json.keySet()) {
            if (key.equals("WorldID")) continue;

            Astronomical astronomical = Astronomical.getFromJsonObject(json.getAsJsonObject(key));
            if (astronomical != null) newWorld.astronomicalPool.add(astronomical);
        }

        return newWorld;
    }

    public List<Astronomical> getAllAstronomical() { return new ArrayList<>(astronomicalPool); }

    public boolean addAstronomical(Astronomical astronomical) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical1 : astronomicalPool) if (astronomical1.id == astronomical.id) return false;
            astronomicalPool.add(astronomical);
            return true;
        }
    }

    public boolean removeAstronomical(int id) {
        synchronized (astronomicalPool) {
            astronomicalPool.removeIf(astronomical1 -> astronomical1.id == id);
            return true;
        }
    }

    public boolean removeAstronomical(String name) {
        synchronized (astronomicalPool) {
            astronomicalPool.removeIf(astronomical1 -> astronomical1.name.equals(name));
            return true;
        }
    }

    @Nullable
    public Astronomical getAstronomical(int id) {
        for (Astronomical astronomical1 : astronomicalPool) if (astronomical1.id == id) return astronomical1;
        return null;
    }

    @Nullable
    public Astronomical getAstronomical(String name) {
        for (Astronomical astronomical1 : astronomicalPool) if (astronomical1.name.equals(name)) return astronomical1;
        return null;
    }

    public boolean setAstronomical(Astronomical astronomical) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical1 : astronomicalPool)
                if (astronomical1.id == astronomical.id) {
                    astronomicalPool.set(astronomicalPool.indexOf(astronomical1), astronomical);
                    return true;
                }
            return false;
        }
    }

    public int size() { return astronomicalPool.size(); }



    public void ForceUpdate(double time) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical : astronomicalPool) {
                AstronomicalGravitation.UpDateAstronomicalGravitationForAllAstronomical(this, astronomical);
                astronomical.forceTimeUpdata(time);
            }
        }
    }

    public void SpeedUpdates(double time) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical : astronomicalPool) {
                Vector3d Acceleration = astronomical.getAcceleration();

                astronomical.x_speed += time * Acceleration.x; //更新速度
                astronomical.y_speed += time * Acceleration.y;
                astronomical.z_speed += time * Acceleration.z;
            }
        }
    }

    public void LocationUpdates(double time) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical : astronomicalPool) {
                astronomical.x += time * astronomical.x_speed; //更新位置
                astronomical.y += time * astronomical.y_speed;
                astronomical.z += time * astronomical.z_speed;
            }
        }
    }

    public void RotateUpdates(double time) {
        synchronized (astronomicalPool) {
            for (Astronomical astronomical : astronomicalPool) astronomical.rotate.mul(new Quaterniond().rotateY(astronomical.rotate_speed * time)).normalize();
        }
    }
}
