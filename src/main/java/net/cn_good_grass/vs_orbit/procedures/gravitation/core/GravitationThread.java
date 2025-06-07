package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.ParticleGravitation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3d;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public abstract class GravitationThread {
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
                    for (ParticlePool gravitationPool : ThreadStart.Gravitation_Core_World_Bus) {
                        //下各种事件更新
                        ForceUpdate(gravitationPool); //更新质点加速度
                        SpeedUpdates(gravitationPool); //更新质点速度
                        LocationUpdates(gravitationPool); //更新质点位置
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



    public static void ForceUpdate(ParticlePool World) {
        if (World == null) { return; }

        for (Particle particle : World.getGravitationCoreWorld()) {
            ParticleGravitation.UpDateParticleGravitationForAllParticle(World, particle);
            particle.forceTimeUpdata(core_tick_time);
        }
    }

    public static void SpeedUpdates(ParticlePool World) {
        if (World == null) { return; }

        for (Particle particle : World.getGravitationCoreWorld()) {
            Vector3d Gravitation = particle.getAcceleration();

            particle.x_speed += core_tick_time * Gravitation.x; //更新速度
            particle.y_speed += core_tick_time * Gravitation.y;
            particle.z_speed += core_tick_time * Gravitation.z;

            World.setParticle(particle);
        }
    }

    public static void LocationUpdates(ParticlePool World) {
        if (World == null) { return; }

        for (Particle particle : World.getGravitationCoreWorld()) {
            if (!particle.start.equals("common")) { continue; } //如果质点不应该参与运动就不更新

            particle.x += core_tick_time * particle.x_speed; //更新位置
            particle.y += core_tick_time * particle.y_speed;
            particle.z += core_tick_time * particle.z_speed;

            World.setParticle(particle);
        }
    }
}
