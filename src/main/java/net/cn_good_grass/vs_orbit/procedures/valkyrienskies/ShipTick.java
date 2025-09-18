package net.cn_good_grass.vs_orbit.procedures.valkyrienskies;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.PhysicalBodyAstronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Force;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.core.AstronomicalThread;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier.ForcerInducedShips;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber
public class ShipTick {
    public static boolean ship_speed_update = false;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) { //引力更新基于游戏刻 而不是物理帧
        if (!(event.phase == TickEvent.Phase.START)) return;
        if (!VSOrbitModConfig.ValkyrienSkies_ENABLE.get()) return;

        for (String WorldIDs : VSOrbitDataPack.OrbitWorld) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) continue;
            String WorldID = level.dimension().location().toString();

            AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldID);
            if (astronomicalPool == null) continue;

            for (Ship ship : VSGameUtilsKt.getAllShips(level)) { //遍历世界中的船只
                if (!("minecraft:dimension:" + WorldID).equals(ship.getChunkClaimDimension())) continue;

                long shipId = ship.getId();

                Vector3d Gravitation;

                if (!(astronomicalPool.getAstronomical("VSShip-" + shipId) instanceof PhysicalBodyAstronomical astronomical)) {
                    double mass = 0;
                    if (ship instanceof ServerShip serverShip) { mass = serverShip.getInertiaData().getMass(); }

                    PhysicalBodyAstronomical newastronomical = new PhysicalBodyAstronomical(astronomicalPool.size(), "VSShip-" + shipId, true, mass, ship.getTransform().getPositionInWorld().x(), ship.getTransform().getPositionInWorld().y(), ship.getTransform().getPositionInWorld().z(), ship);

                    AstronomicalPool astronomicalPool1 = AstronomicalPool.getFromWorldID(WorldID);
                    if (astronomicalPool1 == null) continue;
                    astronomicalPool1.addAstronomical(newastronomical);
                    continue;
                } else {
                    double speed_scale = VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get();

                    Gravitation = astronomical.getAllForce().toVector3d();

                    LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, ship.getTransform().getPositionInShip());
                    if (serverShip == null) {
                        if (ship instanceof ServerShip serverShip1) {
                            ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
                            shipWorld.teleportShip(serverShip1, new ShipTeleportDataImpl(
                                    new Vector3d(astronomical.x, astronomical.y, astronomical.z),
                                    serverShip1.getTransform().getShipToWorldRotation(),
                                    new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed).mul(speed_scale),
                                    new Vector3d(),
                                    serverShip1.getChunkClaimDimension(), 1.0
                            ));
                        }
                        continue;
                    };

                    if (VSOrbitModConfig.ValkyrienSkies_SYNC_MODE.get()) {
                        if (serverShip.getTransform().getPositionInWorld().distance(astronomical.x, astronomical.y, astronomical.z) >= 2 || ship_speed_update) {
                            if (!level.isLoaded(BlockPos.containing(astronomical.x, 0, astronomical.z))) continue;

                            ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

                            if (Double.isInfinite(astronomical.x) || Double.isNaN(astronomical.x) || Double.isInfinite(astronomical.y) || Double.isNaN(astronomical.y) || Double.isInfinite(astronomical.z) || Double.isNaN(astronomical.z)) continue;

                            shipWorld.teleportShip(serverShip, new ShipTeleportDataImpl(
                                    new Vector3d(astronomical.x, astronomical.y, astronomical.z),
                                    serverShip.getTransform().getShipToWorldRotation(),
                                    new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed).mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get()),
                                    new Vector3d(),
                                    serverShip.getChunkClaimDimension(), 1.0
                            ));
                            ship_speed_update = false;
                        } else {
                            if (ship.getVelocity().length() <= 1e-3) { //等于0除了刚建的一瞬间怕也只有区块加载的时候了
                                ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
                                shipWorld.teleportShip(serverShip, new ShipTeleportDataImpl(
                                        new Vector3d(astronomical.x, astronomical.y, astronomical.z),
                                        serverShip.getTransform().getShipToWorldRotation(),
                                        new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed).mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get()),
                                        new Vector3d(),
                                        serverShip.getChunkClaimDimension(), 1.0
                                ));
                                continue;
                            }

                            Vector3d ship_speed = new Vector3d(serverShip.getVelocity());
                            if (Math.abs(ship_speed.length() - new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed).length()) > 1)
                            astronomical.x_speed = ship_speed.x() / speed_scale;
                            astronomical.y_speed = ship_speed.y() / speed_scale;
                            astronomical.z_speed = ship_speed.z() / speed_scale;
                            astronomical.rotate = new Quaterniond(ship.getTransform().getShipToWorldRotation());
                        }
                    } else {
                        astronomical.x = serverShip.getTransform().getPositionInWorld().x();
                        astronomical.y = serverShip.getTransform().getPositionInWorld().y();
                        astronomical.z = serverShip.getTransform().getPositionInWorld().z();
                        astronomical.rotate = new Quaterniond(ship.getTransform().getShipToWorldRotation());
                    }

                    double mass = serverShip.getInertiaData().getMass();
                    CompoundTag addMass = astronomical.Tag.getCompound("vs_orbit:add_mass");
                    for (String key : addMass.getAllKeys()) {
                        if (!level.getBlockState(BlockPos.of(Long.parseLong(key))).getBlock().equals(VSOrbitModBlocks.mass_generator.get())) addMass.remove(key);
                        mass += addMass.getDouble(key);
                    }
                    astronomical.mass = mass;
                }
                Gravitation.mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get());

                ForcerInducedShips forcerInducedShips = ForcerInducedShips.getFromShip(ship);
                if (forcerInducedShips == null) continue;
                forcerInducedShips.addForce(new Force("Gravitation", Gravitation.x, Gravitation.y, Gravitation.z, 1));
            }
        }
    }
}