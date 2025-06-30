package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.client.Minecraft;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AstronomicalThread {
    public static double core_tick_speed = Config.Core_TICK_SPEED.get();
    public static double core_tick_time = Config.Core_TICK_TIME.get();
    public static boolean pause = false;

    private static int tick_record;
    private static long last_time;
    public static int tick;

    public static void CreateThread() {

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (long ThreadId : threadMXBean.getAllThreadIds()) if (threadMXBean.getThreadInfo(ThreadId).getThreadName().equals("AstronomicalThread")) {
            VSOrbitMod.LOGGER.error("[VS_Orbit] [Core] Can't create new thread! There is a thread with the same name in the thread pool!");
            return;
        }
        

        Timer timer = new Timer("AstronomicalThread");

        TimerTask task = new TimerTask() {
            public void run() {
                boolean run = !pause;
                if (ServerLifecycleHooks.getCurrentServer() != null) { if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()) { if (Minecraft.getInstance().isPaused()) { run = false; } } }

                if (run) {
                    for (AstronomicalPool gravitationPool : ServerStart.Gravitation_Core_World_Bus) {
                        //下各种事件更新
                        gravitationPool.ForceUpdate(core_tick_time);
                        gravitationPool.SpeedUpdates(core_tick_time);
                        gravitationPool.LocationUpdates(core_tick_time);
                    }
                }

                tick_record++;
                if (System.currentTimeMillis() >= last_time + 1000) {
                    tick = tick_record;
                    tick_record = 0;
                    last_time = System.currentTimeMillis();
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
