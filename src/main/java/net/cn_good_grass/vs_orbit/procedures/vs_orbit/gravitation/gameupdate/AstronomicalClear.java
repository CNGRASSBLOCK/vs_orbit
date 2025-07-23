package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
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
public class AstronomicalClear {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) { //质点打扫
        if (!(event.phase == TickEvent.Phase.START)) return;

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
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
            if (!astronomical.type.equals("valkyrienskies:ship")) continue;

            long ShipId = Long.parseLong(astronomical.name.substring(7));
            if (!shipIds.contains(ShipId)) astronomicalPool.removeAstronomical(astronomical.id);
        }
    }
}
