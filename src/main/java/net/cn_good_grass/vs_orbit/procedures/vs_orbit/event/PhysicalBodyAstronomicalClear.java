package net.cn_good_grass.vs_orbit.procedures.vs_orbit.event;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.PhysicalBodyAstronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class PhysicalBodyAstronomicalClear {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) { //质点打扫
        if (!(event.phase == TickEvent.Phase.START)) return;

        for (String WorldIDs : VSOrbitDataPack.OrbitWorld) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) return;

            AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldIDs);
            if (astronomicalPool == null) return;
            ClearAstronomicalForVSShip(level, astronomicalPool);
        }
    }

    public static void ClearAstronomicalForVSShip(ServerLevel level, AstronomicalPool astronomicalPool) {
        List<Long> shipIds = new ArrayList<>();
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) { if (("minecraft:dimension:" + astronomicalPool.WorldId).equals(ship.getChunkClaimDimension())) { shipIds.add(ship.getId()); } }

        for (Astronomical astronomical : astronomicalPool.getAllAstronomical()) {
            if (!(astronomical instanceof PhysicalBodyAstronomical)) continue;

            long ShipId = Long.parseLong(astronomical.name.substring(7));
            if (!shipIds.contains(ShipId)) astronomicalPool.removeAstronomical(astronomical.id);
        }
    }
}
