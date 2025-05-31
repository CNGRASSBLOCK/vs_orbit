package net.cn_good_grass.vs_orbit.cilent.render.Particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cn_good_grass.vs_orbit.config.CilentConfig;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.GravitationPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = "vs_orbit", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DisplayLine {
    @SubscribeEvent
    public static void renderModels(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES && Minecraft.getInstance().options.renderDebug) {;
            RenderSystem.disableDepthTest();
            RenderSystem.setShaderFogStart(2147463647);
            RenderSystem.setShaderFogEnd(2147463647);

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) return;

            for (Particle particle : GravitationPool.getFromWorldID(minecraft.level.dimension().location().toString()).getGravitationCoreWorld()) {
                renderLine(event, new Vec3(particle.x, particle.y, particle.z), new Vec3(particle.x + (particle.x_speed * CilentConfig.SPEED_SHOW_SCALING.get()), particle.y + (particle.y_speed * CilentConfig.SPEED_SHOW_SCALING.get()), particle.z + (particle.z_speed * CilentConfig.SPEED_SHOW_SCALING.get())), 255 << 24 | 255 << 16 | 255 << 8 | 0);
                renderLine(event, new Vec3(particle.x, particle.y, particle.z), new Vec3(particle.x + (particle.getAcceleration().x * CilentConfig.ACCELERATION_SHOW_SCALING.get()), particle.y + (particle.getAcceleration().x * CilentConfig.ACCELERATION_SHOW_SCALING.get()), particle.z + (particle.getAcceleration().x * CilentConfig.ACCELERATION_SHOW_SCALING.get())), 255 << 24 | 255 << 16 | 0 | 0);
            }

            RenderSystem.enableDepthTest();
        }
    }

    public static void renderLine(RenderLevelStageEvent event, Vec3 start_pos, Vec3 end_pos, int color) {
        Vec3 pos = event.getCamera().getPosition();
        Vector3f normal = new Vec3(end_pos.x - start_pos.x, end_pos.y - start_pos.y, end_pos.z - start_pos.z).normalize().toVector3f();
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        vertexConsumer.vertex(event.getPoseStack().last().pose(), (float) (start_pos.x - pos.x()), (float) (start_pos.y - pos.y()), (float) (start_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
        vertexConsumer.vertex(event.getPoseStack().last().pose(), (float) (end_pos.x - pos.x()), (float) (end_pos.y - pos.y()), (float) (end_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }
}
