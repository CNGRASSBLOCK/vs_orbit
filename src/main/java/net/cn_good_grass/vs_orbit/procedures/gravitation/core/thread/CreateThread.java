package net.cn_good_grass.vs_orbit.procedures.gravitation.core.thread;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.event.OnWorldLoad;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.event.ParticleUpdate;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateThread {
    public static void CreateThread() {
        Timer timer = new Timer("GravitationThread-" + (OnWorldLoad.Gravitation_Core_AllWorld.size() - 1));
        TimerTask task = new TimerTask() {
            public void run() {
                String ThreadNamme = Thread.currentThread().getName();
                Integer WorldListPos = Integer.parseInt(ThreadNamme.substring(18));
                GravitationWorld gravitationWorld = OnWorldLoad.Gravitation_Core_AllWorld.get(WorldListPos); //获取世界
                //下面是各种事件更新
                if (!Minecraft.getInstance().isPaused()) { //游戏暂停时停止更新
                    ParticleUpdate.AccelerationUpdate(gravitationWorld); //更新质点加速度
                    ParticleUpdate.SpeedUpdates(gravitationWorld); //更新质点速度
                    ParticleUpdate.LocationUpdates(gravitationWorld); //更新质点位置
                }
            }
        };

        Integer Tick_RunTime = (int) Math.floor(1000.0 / Config.TICK_SPEED.get());
        if (Tick_RunTime != 0) {
            timer.scheduleAtFixedRate(task, 0, Tick_RunTime); // 立即开始执行，之后每隔50毫秒执行一次
        } else {
            VSOrbitMod.LOGGER.error("Can't create new thread! TickSpeed is zero!");
        }
    }
}
