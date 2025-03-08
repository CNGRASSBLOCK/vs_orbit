package net.cn_good_grass.vs_orbit.procedures.gravitation.ship;

import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.event.OnWorldLoad;

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

import java.util.List;

@Mod.EventBusSubscriber
public class ShipTick {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) { //引力更新基于游戏刻 而不是物理帧
        ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("cosmos:solar_system")));
        if (event.phase == TickEvent.Phase.START) {
            String WorldID = level.dimension().location().toString();
            if (!WorldID.equals("cosmos:solar_system")) { return; } //不是太空就退出

            List<Particle> particleList = null;
            for (GravitationWorld gravitationWorld : OnWorldLoad.Gravitation_Core_AllWorld) { if (gravitationWorld.WorldId.equals(WorldID)) { particleList = gravitationWorld.Gravitation_Core_World; } }
            if (particleList == null) { return; }

            for (Ship ship : VSGameUtilsKt.getAllShips(level)) { //遍历世界中的船只
                long shipId = ship.getId();  //获取船只 感谢SpaceEye的帮助

                Vector3d Gravitation = new Vector3d(0, 0, 0);

                Particle particle = null;
                for (Particle oneparticle : particleList) { if (oneparticle.name.equals("VSShip-" + shipId)) { particle = oneparticle; } }
                if (particle == null) {
                    Particle newparticle = new Particle();
                    newparticle.id = particleList.size();
                    newparticle.name = "VSShip-" + shipId;
                    newparticle.x = ship.getTransform().getPositionInWorld().x();
                    newparticle.y = ship.getTransform().getPositionInWorld().y();
                    newparticle.z = ship.getTransform().getPositionInWorld().z();
                    if (ship instanceof ServerShip serverShip) { newparticle.mass = serverShip.getInertiaData().getMass(); }
                    newparticle.start = "follow";
                    particleList.add(newparticle);
                } else {
                    Gravitation.x = particle.x_acceleration;
                    Gravitation.y = particle.y_acceleration;
                    Gravitation.z = particle.z_acceleration;
                }

                LoadedServerShip loadedServerShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(shipId);
                if (loadedServerShip == null) {continue;}
                GameTickForceApplier applier = loadedServerShip.getAttachment(GameTickForceApplier.class);
                if (applier != null) {
                    applier.applyInvariantForce(Gravitation); //施加力
                }
            }
        }
    }
}