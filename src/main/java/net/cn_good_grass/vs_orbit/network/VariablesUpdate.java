package net.cn_good_grass.vs_orbit.network;

import com.google.gson.JsonParser;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.WorldOperate;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class VariablesUpdate {
    public static List<GravitationWorld> New_Gravitation_Core_World_Bus = new ArrayList<>();
    public static List<GravitationWorld> Old_Gravitation_Core_World_Bus = new ArrayList<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Entity entity = event.player;

        if (event.phase == TickEvent.Phase.START) { return; }

        if (event.side.isServer()) {
            StringBuilder DataPack = new StringBuilder();
            for (GravitationWorld gravitationWorld : WorldOperate.Gravitation_Core_World_Bus) {
                if (!DataPack.isEmpty()) {
                    DataPack.append("【分隔符】");
                }
                DataPack.append(gravitationWorld.toJsonObject().toString());
            }

            final String FinalDataPack = DataPack.toString();
            entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.GlobalVariables = FinalDataPack;
                capability.syncPlayerVariables(entity);
            });
        } else if (event.side.isClient()) {
            Old_Gravitation_Core_World_Bus = new ArrayList<>(New_Gravitation_Core_World_Bus);

            List<GravitationWorld> Gravitation_Core_World_Bus = new ArrayList<GravitationWorld>();

            List<String> DataPackList = new ArrayList<>(List.of((entity.getCapability(GravitationCoreNetWork.PLAYER_VARIABLES_CAPABILITY, null).orElse(new GravitationCoreNetWork.PlayerVariables())).GlobalVariables.split("【分隔符】")));
            for (String string : DataPackList) {
                if (string.isEmpty()) { continue; }

                GravitationWorld gravitationWorld = GravitationWorld.getFromJsonObject(JsonParser.parseString(string).getAsJsonObject());
                Gravitation_Core_World_Bus.add(gravitationWorld);
            }
            New_Gravitation_Core_World_Bus = new ArrayList<>(Gravitation_Core_World_Bus);
        }
    }
}
