package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;

import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;

import net.cn_good_grass.vs_orbit.procedures.gravitation.core.AstronomicalThread;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.math.BigDecimal;

@Mod.EventBusSubscriber
public class ShipTick {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) { //引力更新基于游戏刻 而不是物理帧
        if (!(event.phase == TickEvent.Phase.START)) { return; }
        if (!Config.ValkyrienSkies_ENABLE.get()) { return; }

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) { return; }
            String WorldID = level.dimension().location().toString();

            AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldID);

            for (Ship ship : VSGameUtilsKt.getAllShips(level)) { //遍历世界中的船只
                if (!("minecraft:dimension:" + WorldID).equals(ship.getChunkClaimDimension())) { return; }

                long shipId = ship.getId();  //获取船只 感谢SpaceEye的帮助

                Vector3d Gravitation;

                Astronomical astronomical = astronomicalPool.getAstronomical("VSShip-" + shipId);
                
                if (astronomical == null) {
                    double mass = 0;
                    if (ship instanceof ServerShip serverShip) { mass = serverShip.getInertiaData().getMass(); }

                    Astronomical newastronomical = new Astronomical(astronomicalPool.size(), "VSShip-" + shipId, "valkyrienskies:ship", true, mass, ship.getTransform().getPositionInWorld().x(), ship.getTransform().getPositionInWorld().y(), ship.getTransform().getPositionInWorld().z());

                    AstronomicalPool.getFromWorldID(WorldID).addAstronomical(newastronomical);
                    continue;
                } else {
                    Gravitation = astronomical.getAllForce().toVector3d();

                    astronomical.x = ship.getTransform().getPositionInWorld().x();
                    astronomical.y = ship.getTransform().getPositionInWorld().y();
                    astronomical.z = ship.getTransform().getPositionInWorld().z();

                    if (!(ship instanceof ServerShip serverShip)) return;
                    double mass = serverShip.getInertiaData().getMass();
                    for (String key : astronomical.Tag.getCompound("add_mass").getAllKeys()) mass += astronomical.Tag.getCompound("add_mass").getLong(key);
                    astronomical.mass = mass;
                }
                Vector3d ShipGravitation = new Vector3d(Gravitation.x * Config.ValkyrienSkies_ACCELERATION_SCALING.get() * 3 * (AstronomicalThread.core_tick_time / Config.Core_TICK_TIME.get()), Gravitation.y * Config.ValkyrienSkies_ACCELERATION_SCALING.get() * 3 * (AstronomicalThread.core_tick_time / Config.Core_TICK_TIME.get()), Gravitation.z * Config.ValkyrienSkies_ACCELERATION_SCALING.get() * 3 * (AstronomicalThread.core_tick_time / Config.Core_TICK_TIME.get()));
                LoadedServerShip loadedServerShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(shipId);
                if (loadedServerShip == null) { continue; }
                GameTickForceApplier applier = loadedServerShip.getAttachment(GameTickForceApplier.class);
                if (applier != null) applier.applyInvariantForce(ShipGravitation); //施加力
            }
        }
    }
}