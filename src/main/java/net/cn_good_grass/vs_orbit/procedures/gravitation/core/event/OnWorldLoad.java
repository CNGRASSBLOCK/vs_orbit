package net.cn_good_grass.vs_orbit.procedures.gravitation.core.event;

import net.cn_good_grass.vs_orbit.procedures.gravitation.core.thread.CreateThread;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.cn_good_grass.vs_orbit.procedures.gravitation.GlobalVariables.Gravitation_Core_AllWorld;

@Mod.EventBusSubscriber
public class OnWorldLoad {
    @SubscribeEvent
    public static void OnWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
        if (!((Level) event.getLevel()).dimension().location().toString().equals("cosmos:solar_system")) { return; }

        if (false) { //如果有数据就读取
            return;
        } else { //没有就创建
            CreateNewGravitationWorld.CreateNewGravitationWorld((Level) event.getLevel());
        }

        CreateThread.CreateThread(); //新建线程
    }
}


