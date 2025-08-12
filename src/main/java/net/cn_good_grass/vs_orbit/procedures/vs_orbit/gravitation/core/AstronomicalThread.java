package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.event.ServerAction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AstronomicalThread {
    private static Timer timer;
    private static TimerTask task;

    public static double core_tick_speed = VSOrbitModConfig.Core_TICK_SPEED.get();
    public static double core_tick_time = VSOrbitModConfig.Core_TICK_TIME.get();
    public static boolean pause = false;

    private static int tick_record;
    private static long last_time;
    public static int tick;

    public static void StartThread() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (long ThreadId : threadMXBean.getAllThreadIds()) if (threadMXBean.getThreadInfo(ThreadId).getThreadName().equals("AstronomicalThread")) {
            VSOrbitMod.LOGGER.error("[VS_Orbit] [Core] Can't create new thread! There is a thread with the same name in the thread pool!");
            return;
        }
        //初始化
        core_tick_speed = VSOrbitModConfig.Core_TICK_SPEED.get();
        core_tick_time = VSOrbitModConfig.Core_TICK_TIME.get();
        pause = false;

        AstronomicalThread.timer = new Timer("AstronomicalThread");

        AstronomicalThread.task = new TimerTask() {
            public void run() {
                boolean run = !pause;
                if (ServerLifecycleHooks.getCurrentServer() != null) if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()) if (Minecraft.getInstance().isPaused()) run = false;

                if (run) {
                    synchronized (ServerAction.Astronomical_Core_World_Bus) {
                        for (AstronomicalPool gravitationPool : ServerAction.Astronomical_Core_World_Bus) {
                            //下各种事件更新
                            gravitationPool.ForceUpdate(core_tick_time);
                            gravitationPool.SpeedUpdates(core_tick_time);
                            gravitationPool.LocationUpdates(core_tick_time);

                            gravitationPool.RotateUpdates(core_tick_time);
                        }
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
            timer.scheduleAtFixedRate(task, 0, Tick_RunTime);
            VSOrbitMod.LOGGER.info("[VS_Orbit] [Core] Can't create new thread! TickSpeed is zero!");
        } else {
            VSOrbitMod.LOGGER.error("[VS_Orbit] [Core] Can't create new thread! TickSpeed is zero!");
        }
    }

    public static void StopThread() {
        task.cancel();
        timer.cancel();
        timer.purge();

        core_tick_speed = VSOrbitModConfig.Core_TICK_SPEED.get();
        core_tick_time = VSOrbitModConfig.Core_TICK_TIME.get();
        pause = false;
    }
}
