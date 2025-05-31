package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.GravitationPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
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
        if (!(event.phase == TickEvent.Phase.START)) return;

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) return;

            ClearParticleForVSShip(level, GravitationPool.getFromWorldID(WorldIDs));
        }
    }

    public static void ClearParticleForVSShip(ServerLevel level, GravitationPool gravitationPool) {
        List<Long> shipIds = new ArrayList<>();
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) { if (("minecraft:dimension:" + gravitationPool.WorldId).equals(ship.getChunkClaimDimension())) { shipIds.add(ship.getId()); } }

        for (Particle particle : gravitationPool.getGravitationCoreWorld()) {
            if (!particle.name.contains("VSShip-")) continue;

            long ShipId = Long.valueOf(particle.name.substring(7));
            if (!shipIds.contains(ShipId)) gravitationPool.removeParticle(particle.id);
        }
    }
}
