package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.minecraft.client.Minecraft;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ParticleThread {
    public static double core_tick_speed = Config.Core_TICK_SPEED.get();
    public static double core_tick_time = Config.Core_TICK_TIME.get();
    public static boolean pause = false;

    public static void CreateThread() {
        String NewThreadName = "GravitationThread";

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (long ThreadId : threadMXBean.getAllThreadIds()) if (threadMXBean.getThreadInfo(ThreadId).getThreadName().equals(NewThreadName)) VSOrbitMod.LOGGER.error("[VS_Orbit] [Core] Can't create new thread! There is a thread with the same name in the thread pool!");

        Timer timer = new Timer(NewThreadName);

        TimerTask task = new TimerTask() {
            public void run() {
                boolean run = !pause;
                if (ServerLifecycleHooks.getCurrentServer() != null) { if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()) { if (Minecraft.getInstance().isPaused()) { run = false; } } }

                if (run) {
                    for (ParticlePool gravitationPool : ParticleWorld.Gravitation_Core_World_Bus) {
                        //下各种事件更新
                        gravitationPool.ForceUpdate(core_tick_time);
                        gravitationPool.SpeedUpdates(core_tick_time);
                        gravitationPool.LocationUpdates(core_tick_time);
                    }
                }
            }
        };

        int Tick_RunTime = (int) Math.floor(1000.0 / core_tick_speed);
        if (Tick_RunTime != 0) {
            timer.scheduleAtFixedRate(task, 0, Tick_RunTime); // 立即开始执行，之后每隔50毫秒执行一次
        } else {
            VSOrbitMod.LOGGER.error("[VS_Orbit] [Core] Can't create new thread! TickSpeed is zero!");
        }
    }
}
