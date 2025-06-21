package net.cn_good_grass.vs_orbit.network;

import net.cn_good_grass.vs_orbit.cilent.render.PlanetEngine.PlanetEngineFire;
import net.cn_good_grass.vs_orbit.network.packet.SyncParticlePoolPacket;
import net.cn_good_grass.vs_orbit.network.packet.SyncPlanetEngineDataPacket;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static net.cn_good_grass.vs_orbit.procedures.gravitation.core.ParticleWorld.Gravitation_Core_World_Bus;

@Mod.EventBusSubscriber
public class SyncDataTick {
    public static List<ParticlePool> New_Gravitation_Core_World_Bus = new ArrayList<>();
    public static List<ParticlePool> Old_Gravitation_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Player player = event.player;

        if (player instanceof ServerPlayer serverPlayer) {
            //引力数据
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncParticlePoolPacket(Gravitation_Core_World_Bus, System.currentTimeMillis()));

            //行星发动机火焰数据
            StringBuilder planetfirelist = new StringBuilder();
            for (PlanetEngineFire planetEngineFire : PlanetEngineFire.fires_server) {
                if (!planetfirelist.isEmpty()) {
                    planetfirelist.append("【分隔符】");
                }
                planetfirelist.append(planetEngineFire.toString());
            }
            final String planetfirelistDataPack = planetfirelist.toString();

            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPlanetEngineDataPacket(planetfirelistDataPack));
        } else {
            Old_Gravitation_Core_World_Bus = new ArrayList<>(New_Gravitation_Core_World_Bus);
            New_Gravitation_Core_World_Bus = new ArrayList<>(SyncParticlePoolPacket.Gravitation_Core_World_Bus_Data_Save);
        }
    }
}
