package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;

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
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.math.BigDecimal;
import java.util.List;

@Mod.EventBusSubscriber
public class ShipTick {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) { //引力更新基于游戏刻 而不是物理帧
        if (!(event.phase == TickEvent.Phase.START)) { return; }
        if (!Config.ValkyrienSkies_ENABLE.get()) { return; }

        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
            if (level == null) { return; }
            String WorldID = level.dimension().location().toString();

            List<Particle> particleList = GravitationPool.getFromWorldID(WorldID).Gravitation_Core_World;

            for (Ship ship : VSGameUtilsKt.getAllShips(level)) { //遍历世界中的船只
                if (!("minecraft:dimension:" + WorldID).equals(ship.getChunkClaimDimension())) { return; }

                long shipId = ship.getId();  //获取船只 感谢SpaceEye的帮助

                Vector3d Gravitation = new Vector3d(0, 0, 0);

                Particle particle = null;
                for (Particle oneparticle : particleList) { if (oneparticle.name.equals("VSShip-" + shipId)) { particle = oneparticle; } }
                
                if (particle == null) {
                    BigDecimal mass = new BigDecimal(0);
                    String start = "common";
                    if (Config.ValkyrienSkies_MOVEMENT_MODE.get().equals("VS_FOLLOW_PARTICLE")) { start = "common"; } else { start = "common"; }
                    if (ship instanceof ServerShip serverShip) { mass = new BigDecimal(serverShip.getInertiaData().getMass()); }

                    Particle newparticle = new Particle(particleList.size(), "VSShip-" + shipId, start, mass, ship.getTransform().getPositionInWorld().x(), ship.getTransform().getPositionInWorld().y(), ship.getTransform().getPositionInWorld().z());

                    particleList.add(newparticle);
                    continue;
                } else {
                    Gravitation.x = particle.x_acceleration;
                    Gravitation.y = particle.y_acceleration;
                    Gravitation.z = particle.z_acceleration;

                    particle.x = ship.getTransform().getPositionInWorld().x();
                    particle.y = ship.getTransform().getPositionInWorld().y();
                    particle.z = ship.getTransform().getPositionInWorld().z();
                }

                Vector3d ShipGravitation = new Vector3d(Gravitation.x * Config.ValkyrienSkies_ACCELERATION_SCALING.get(), Gravitation.y * Config.ValkyrienSkies_ACCELERATION_SCALING.get(), Gravitation.z * Config.ValkyrienSkies_ACCELERATION_SCALING.get());

                LoadedServerShip loadedServerShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(shipId);
                if (loadedServerShip == null) {continue;}
                GameTickForceApplier applier = loadedServerShip.getAttachment(GameTickForceApplier.class);
                if (applier != null) {
                    applier.applyInvariantForce(ShipGravitation); //施加力
                }
            }
        }
    }
}