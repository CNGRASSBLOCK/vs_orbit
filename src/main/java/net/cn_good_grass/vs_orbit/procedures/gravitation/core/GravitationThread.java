package net.cn_good_grass.vs_orbit.procedures.gravitation.core;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3d;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class GravitationThread {
    public static double core_tick_speed = Config.Core_TICK_SPEED.get();
    public static double core_tick_time = Config.Core_TICK_TIME.get();

    public static void CreateThread() {
        if (WorldOperate.Gravitation_Core_World_Bus.isEmpty()) { return; }

        String NewThreadName = "GravitationThread-" + (WorldOperate.Gravitation_Core_World_Bus.size() - 1);

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (long ThreadId : threadMXBean.getAllThreadIds()) {
            if (threadMXBean.getThreadInfo(ThreadId).getThreadName().equals(NewThreadName)) {
                VSOrbitMod.LOGGER.error("[VSOrbit] [Core] Can't create new thread! There is a thread with the same name in the thread pool!");
                return;
            }
        }

        Timer timer = new Timer(NewThreadName);

        TimerTask task = new TimerTask() {
            public void run() {
                boolean run = true;
                if (ServerLifecycleHooks.getCurrentServer() != null) { if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()) { if (Minecraft.getInstance().isPaused()) { run = false; } } }

                if (run) {
                    String ThreadNamme = Thread.currentThread().getName();
                    Integer WorldListPos = Integer.parseInt(ThreadNamme.substring(18));
                    GravitationWorld gravitationWorld = WorldOperate.Gravitation_Core_World_Bus.get(WorldListPos); //获取世界
                    //下面是各种事件更新
                    AccelerationUpdate(gravitationWorld); //更新质点加速度
                    SpeedUpdates(gravitationWorld); //更新质点速度
                    LocationUpdates(gravitationWorld); //更新质点位置
                }
            }
        };

        Integer Tick_RunTime = (int) Math.floor(1000.0 / core_tick_speed);
        if (Tick_RunTime != 0) {
            timer.scheduleAtFixedRate(task, 0, Tick_RunTime); // 立即开始执行，之后每隔50毫秒执行一次
        } else {
            VSOrbitMod.LOGGER.error("[VSOrbit] [Core] Can't create new thread! TickSpeed is zero!");
        }
    }

    public static void AccelerationUpdate(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            Vector3d Gravitation = ParticleGravitation.GetParticleGravitationForAllParticle(World, particle); //获取加速度

            if (particle.start.equals("fixed")) { continue; } //如果质点不应该参与运动就不更新

            particle.x_acceleration = Gravitation.x; //更新加速度
            particle.y_acceleration = Gravitation.y;
            particle.z_acceleration = Gravitation.z;
        }
    }


    public static void SpeedUpdates(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            Vector3d Gravitation = new Vector3d(particle.x_acceleration, particle.y_acceleration, particle.z_acceleration); //获取加速度

            particle.x_speed += core_tick_time * Gravitation.x; //更新速度
            particle.y_speed += core_tick_time * Gravitation.y;
            particle.z_speed += core_tick_time * Gravitation.z;
        }
    }

    public static void LocationUpdates(GravitationWorld World) {
        if (World == null) { return; }

        for (Particle particle : World.Gravitation_Core_World) {
            if (!particle.start.equals("common")) { continue; } //如果质点不应该参与运动就不更新

            particle.x += core_tick_time * particle.x_speed; //更新位置
            particle.y += core_tick_time * particle.y_speed;
            particle.z += core_tick_time * particle.z_speed;
        }
    }
}
