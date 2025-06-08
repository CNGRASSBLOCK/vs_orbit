package net.cn_good_grass.vs_orbit.network;

import com.google.gson.JsonParser;
import net.cn_good_grass.vs_orbit.cilent.render.PlanetEngine.PlanetEngineFire;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.GravitationWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class VariablesUpdate {
    public static List<ParticlePool> New_Gravitation_Core_World_Bus = new ArrayList<>();
    public static List<ParticlePool> Old_Gravitation_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Entity entity = event.player;

        if (event.phase == TickEvent.Phase.START) { return; }

        if (event.side.isServer()) {
            //引力数据
            StringBuilder DataPack = new StringBuilder();
            for (ParticlePool particlePool : GravitationWorld.Gravitation_Core_World_Bus) {
                if (!DataPack.isEmpty()) {
                    DataPack.append("【分隔符】");
                }
                DataPack.append(particlePool.toJsonObject().toString());
            }

            final String FinalDataPack = DataPack.toString();
            entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.GlobalVariables = FinalDataPack;
                capability.syncPlayerVariables(entity);
            });
            //行星发动机火焰列表
            StringBuilder planetfirelist = new StringBuilder();
            for (PlanetEngineFire planetEngineFire : PlanetEngineFire.fires_server) {
                if (!planetfirelist.isEmpty()) {
                    planetfirelist.append("【分隔符】");
                }
                planetfirelist.append(planetEngineFire.toString());
            }

            final String planetfirelistDataPack = planetfirelist.toString();
            entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.PlanetEngineFireList = planetfirelistDataPack;
                capability.syncPlayerVariables(entity);
            });
        } else if (event.side.isClient()) {
            //引力数据
            Old_Gravitation_Core_World_Bus = new ArrayList<>(New_Gravitation_Core_World_Bus);

            List<ParticlePool> Gravitation_Core_World_Bus = new ArrayList<>();

            List<String> DataPackList = new ArrayList<>(List.of((entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).orElse(new GravitationCoreNetWork.PlayerVariables())).GlobalVariables.split("【分隔符】")));
            for (String string : DataPackList) {
                if (string.isEmpty()) { continue; }

                ParticlePool particlePool = ParticlePool.getFromJsonObject(JsonParser.parseString(string).getAsJsonObject());
                Gravitation_Core_World_Bus.add(particlePool);
            }

            New_Gravitation_Core_World_Bus = Gravitation_Core_World_Bus;
            //行星发动机火焰列表
            List<String> FireDataPackList = new ArrayList<>(List.of((entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).orElse(new GravitationCoreNetWork.PlayerVariables())).PlanetEngineFireList.split("【分隔符】")));
            PlanetEngineFire.fires_cilent.clear();

            for (String string : FireDataPackList) {
                if (string.isEmpty()) { continue; }

                String pos = string.split("，")[0].replace("{","").replace("}","");
                BlockPos blockPos = new BlockPos(Integer.parseInt(pos.split(",")[0]), Integer.parseInt(pos.split(",")[1]), Integer.parseInt(pos.split(",")[2]));
                PlanetEngineFire planetEngineFire = new PlanetEngineFire(blockPos, string.split("，")[1], Integer.parseInt(string.split("，")[2]), Integer.parseInt(string.split("，")[3]));
                PlanetEngineFire.fires_cilent.add(planetEngineFire);
            }
        }
    }
}
