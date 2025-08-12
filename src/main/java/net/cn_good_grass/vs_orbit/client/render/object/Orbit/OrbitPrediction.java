package net.cn_good_grass.vs_orbit.client.render.object.Orbit;

import net.cn_good_grass.vs_orbit.network.SyncDataTick;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import org.valkyrienskies.core.impl.shadow.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OrbitPrediction {
    public static List<List<AstronomicalPool>> DataSave = new ArrayList<>();

    public static void Prediction(int frequency, double time) {
        DataSave.clear();
        List<AstronomicalPool> Data = new ArrayList<>();
        for (AstronomicalPool astronomicalPool : SyncDataTick.New_Gravitation_Core_World_Bus) Data.add(AstronomicalPool.getFromJsonObject(astronomicalPool.toJsonObject()));
        DataSave.add(Data);

        for (int i = 0; i < frequency - 1; i++) {
            List<AstronomicalPool> the_data = new ArrayList<>();
            for (AstronomicalPool astronomicalPool : new ArrayList<>(DataSave.get(DataSave.size() - 1))) {
                AstronomicalPool astronomicalPool1 = AstronomicalPool.getFromJsonObject(astronomicalPool.toJsonObject());
                if (astronomicalPool1 == null) continue;

                astronomicalPool1.ForceUpdate(time);
                astronomicalPool1.SpeedUpdates(time);
                astronomicalPool1.LocationUpdates(time);

                the_data.add(astronomicalPool1);
            }
            DataSave.add(the_data);
        }
    }

    public static List<AstronomicalPool> getAllDataFromWorldID(String WorldID) {
        List<AstronomicalPool> data = new ArrayList<>();
        for (List<AstronomicalPool> one_data: DataSave) for (AstronomicalPool one_pool: one_data) if (one_pool.WorldId.equals(WorldID)) data.add(one_pool);
        return data;
    }
}
