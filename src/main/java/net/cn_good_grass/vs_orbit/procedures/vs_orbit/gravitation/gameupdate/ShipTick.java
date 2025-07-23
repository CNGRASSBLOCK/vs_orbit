package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core.AstronomicalThread;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier.ForcerInducedShips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber
public class ShipTick {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) { //引力更新基于游戏刻 而不是物理帧
        if (!(event.phase == TickEvent.Phase.START)) return;
        if (!Config.ValkyrienSkies_ENABLE.get()) return;

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) continue;
            String WorldID = level.dimension().location().toString();

            AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldID);
            if (astronomicalPool == null) continue;

            for (Ship ship : VSGameUtilsKt.getAllShips(level)) { //遍历世界中的船只
                if (!("minecraft:dimension:" + WorldID).equals(ship.getChunkClaimDimension())) continue;

                long shipId = ship.getId();

                Vector3d Gravitation;

                Astronomical astronomical = astronomicalPool.getAstronomical("VSShip-" + shipId);
                
                if (astronomical == null) {
                    double mass = 0;
                    if (ship instanceof ServerShip serverShip) { mass = serverShip.getInertiaData().getMass(); }

                    Astronomical newastronomical = new Astronomical(astronomicalPool.size(), "VSShip-" + shipId, "valkyrienskies:ship", true, mass, ship.getTransform().getPositionInWorld().x(), ship.getTransform().getPositionInWorld().y(), ship.getTransform().getPositionInWorld().z());

                    AstronomicalPool astronomicalPool1 = AstronomicalPool.getFromWorldID(WorldID);
                    if (astronomicalPool1 == null) continue;
                    astronomicalPool1.addAstronomical(newastronomical);
                    continue;
                } else {
                    Gravitation = astronomical.getAllForce().toVector3d();

                    astronomical.x = ship.getTransform().getPositionInWorld().x();
                    astronomical.y = ship.getTransform().getPositionInWorld().y();
                    astronomical.z = ship.getTransform().getPositionInWorld().z();

                    astronomical.x_speed = ship.getVelocity().x();
                    astronomical.y_speed = ship.getVelocity().y();
                    astronomical.z_speed = ship.getVelocity().z();

                    if (!(ship instanceof ServerShip serverShip)) continue;
                    double mass = serverShip.getInertiaData().getMass();
                    CompoundTag addMass = astronomical.Tag.getCompound("vs_orbit:add_mass");
                    for (String key : addMass.getAllKeys()) {
                        if (!level.getBlockState(BlockPos.of(Long.parseLong(key))).getBlock().equals(VSOrbitModBlocks.mass_generator.get())) addMass.remove(key);
                        mass += addMass.getDouble(key);
                    }
                    astronomical.mass = mass;
                }
                Gravitation.mul(Config.ValkyrienSkies_ACCELERATION_SCALING.get() * (AstronomicalThread.core_tick_time / Config.Core_TICK_TIME.get()));

                ForcerInducedShips forcerInducedShips = ForcerInducedShips.getFromShip(ship);
                if (forcerInducedShips == null) continue;
                forcerInducedShips.addForce(new Force("Gravitation", Gravitation.x, Gravitation.y, Gravitation.z, 1));
            }
        }
    }
}