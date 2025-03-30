package net.cn_good_grass.vs_orbit.cilent.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cn_good_grass.vs_orbit.config.CilentConfig;
import net.cn_good_grass.vs_orbit.modclass.GravitationWorld;
import net.cn_good_grass.vs_orbit.modclass.Particle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = "vs_orbit", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DisplayParticleData {
    @SubscribeEvent
    public static void renderModels(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {;
            RenderSystem.disableDepthTest();
            RenderSystem.setShaderFogStart(2147463647);
            RenderSystem.setShaderFogEnd(2147463647);

            Minecraft minecraft = Minecraft.getInstance();
            String WorldID = ((Level) minecraft.level).dimension().location().toString();

            GravitationWorld thisGravitationWorld = GravitationWorld.getFromWorldID(WorldID);

            for (Particle particle : thisGravitationWorld.Gravitation_Core_World) {
                Vec3 speed_line_center = new Vec3(particle.x, particle.y, particle.z);
                Vec3 speed_line_end = new Vec3(particle.x + (particle.x_speed * CilentConfig.SPEED_SHOW_SCALING.get()), particle.y + (particle.y_speed * CilentConfig.SPEED_SHOW_SCALING.get()), particle.z + (particle.z_speed * CilentConfig.SPEED_SHOW_SCALING.get()));

                renderLine(event, speed_line_center, speed_line_end, 255 << 24 | 255 << 16 | 255 << 8 | 0);

                Vec3 acceleration_line_center = new Vec3(particle.x, particle.y, particle.z);
                Vec3 acceleration_line_end = new Vec3(particle.x + (particle.x_acceleration * CilentConfig.ACCELERATION_SHOW_SCALING.get()), particle.y + (particle.y_acceleration * CilentConfig.ACCELERATION_SHOW_SCALING.get()), particle.z + (particle.z_acceleration * CilentConfig.ACCELERATION_SHOW_SCALING.get()));

                renderLine(event, acceleration_line_center, acceleration_line_end, 255 << 24 | 255 << 16 | 0 | 0);
            }

            RenderSystem.enableDepthTest();
        }
    }

    public static void renderLine(RenderLevelStageEvent event, Vec3 start_pos, Vec3 end_pos, int color) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 pos = event.getCamera().getPosition();
        Vector3f normal = new Vec3(end_pos.x - start_pos.x, end_pos.y - start_pos.y, end_pos.z - start_pos.z).normalize().toVector3f();
        Matrix4f matrix4f = event.getPoseStack().last().pose();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        vertexConsumer.vertex(matrix4f, (float) (start_pos.x - pos.x()), (float) (start_pos.y - pos.y()), (float) (start_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
        vertexConsumer.vertex(matrix4f, (float) (end_pos.x - pos.x()), (float) (end_pos.y - pos.y()), (float) (end_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }
}
