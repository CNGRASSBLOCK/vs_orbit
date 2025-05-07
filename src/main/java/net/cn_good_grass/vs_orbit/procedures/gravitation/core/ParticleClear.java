package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.GravitationPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.Particle;
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
public class ParticleClear {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) { //质点打扫
        if (!(event.phase == TickEvent.Phase.START)) { return; }

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) { return; }

            GravitationPool thisGravitationPool = GravitationPool.getFromWorldID(WorldIDs);

            thisGravitationPool.Gravitation_Core_World = ClearParticleForVSShip(level, thisGravitationPool.Gravitation_Core_World, WorldIDs);
        }
    }

    public static List<Particle> ClearParticleForVSShip(ServerLevel level, List<Particle> particleList, String WorldID) {
        List<Long> shipIds = new ArrayList<>();
        List<Particle> particleListnew = new ArrayList<>(particleList);
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) { if (("minecraft:dimension:" + WorldID).equals(ship.getChunkClaimDimension())) { shipIds.add(ship.getId()); } }

        for (Particle particle : particleList) {
            if (!particle.name.contains("VSShip-")) { continue; }

            long ShipId = Long.valueOf(particle.name.substring(7));
            if (!shipIds.contains(ShipId)) { particleListnew.remove(particle); }
        }

        return particleListnew;
    }
}
