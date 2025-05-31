package net.cn_good_grass.vs_orbit.cilent.render.PlanetEngine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import static java.lang.Math.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DisplayPlanetEngineFire {
    private static void main() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        for (PlanetEngineFire fire : PlanetEngineFire.fires_cilent) {
            if (player.position().distanceTo(fire.blockPos.getCenter()) >= Minecraft.getInstance().options.renderDistance().get() * 480) continue;
            for (int i = 0; i < 30; i++) {
                double start_x = cos(toRadians(i*12)) * fire.r;
                double start_z = sin(toRadians(i*12)) * fire.r;
                double end_x = cos(toRadians((i + 1)*12)) * fire.r;
                double end_z = sin(toRadians((i + 1)*12)) * fire.r;
                if (begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR, true)) {
                    add(fire.blockPos.getX() + start_x, fire.blockPos.getY(), fire.blockPos.getZ() + start_z, 0, 0, 0xFFBFFFFF);
                    add(fire.blockPos.getX() + start_x, fire.blockPos.getY() + fire.h, fire.blockPos.getZ() + start_z, 0, 0, 0x0000FFFF);
                    add(fire.blockPos.getX() + end_x, fire.blockPos.getY() + fire.h, fire.blockPos.getZ() + end_z, 0, 0, 0x0000FFFF);
                    add(fire.blockPos.getX() + end_x, fire.blockPos.getY(), fire.blockPos.getZ() + end_z, 0, 0, 0xFFBFFFFF);
                    add(fire.blockPos.getX() + start_z, fire.blockPos.getY(), fire.blockPos.getZ() + start_x, 0, 0, 0xFFBFFFFF);
                    add(fire.blockPos.getX() + start_z, fire.blockPos.getY() + fire.h, fire.blockPos.getZ() + start_x, 0, 0, 0x0000FFFF);
                    add(fire.blockPos.getX() + end_z, fire.blockPos.getY() + fire.h, fire.blockPos.getZ() + end_x, 0, 0, 0x0000FFFF);
                    add(fire.blockPos.getX() + end_z, fire.blockPos.getY(), fire.blockPos.getZ() + end_x, 0, 0, 0xFFBFFFFF);
                    end();
                }
                if (target(2)) {
                    renderShape(shape(), 0, 0, 0, 0, 0, 0, 1, 1, 1, 0xFFFFFFFF);
                    release();
                }
            }
        }
    }



    private static BufferBuilder bufferBuilder = null;
    private static VertexBuffer vertexBuffer = null;
    private static VertexFormat.Mode mode = null;
    private static VertexFormat format = null;
    private static PoseStack poseStack = null;
    private static Matrix4f projectionMatrix = null;
    private static boolean worldCoordinate = true;
    private static Vec3 offset = Vec3.ZERO;
    private static int currentStage = 0;
    private static int targetStage = 0; // NONE: 0, SKY: 1, WORLD: 2

    private static void add(double x, double y, double z, float u, float v, int color) {
        if (bufferBuilder == null || !bufferBuilder.building())
            return;
        if (format == DefaultVertexFormat.POSITION_COLOR) {
            bufferBuilder.vertex(x, y, z).color(color).endVertex();
        } else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
            bufferBuilder.vertex(x, y, z).uv(u, v).color(color).endVertex();
        }
    }

    private static boolean begin(VertexFormat.Mode mode, VertexFormat format, boolean update) {
        if (DisplayPlanetEngineFire.bufferBuilder == null || !DisplayPlanetEngineFire.bufferBuilder.building()) {
            if (update)
                clear();
            if (DisplayPlanetEngineFire.vertexBuffer == null) {
                if (format == DefaultVertexFormat.POSITION_COLOR) {
                    DisplayPlanetEngineFire.mode = mode;
                    DisplayPlanetEngineFire.format = format;
                    DisplayPlanetEngineFire.bufferBuilder = Tesselator.getInstance().getBuilder();
                    DisplayPlanetEngineFire.bufferBuilder.begin(mode, DefaultVertexFormat.POSITION_COLOR);
                    return true;
                } else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
                    DisplayPlanetEngineFire.mode = mode;
                    DisplayPlanetEngineFire.format = format;
                    DisplayPlanetEngineFire.bufferBuilder = Tesselator.getInstance().getBuilder();
                    DisplayPlanetEngineFire.bufferBuilder.begin(mode, DefaultVertexFormat.POSITION_TEX_COLOR);
                    return true;
                }
            }
        }
        return false;
    }

    private static void clear() { if (vertexBuffer != null) { vertexBuffer.close(); vertexBuffer = null; } }

    private static void end() {
        if (bufferBuilder == null || !bufferBuilder.building())
            return;
        if (vertexBuffer != null)
            vertexBuffer.close();
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vertexBuffer.bind();
        vertexBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }


    private static void release() { targetStage = 0; }
    private static VertexBuffer shape() { return vertexBuffer; }

    private static boolean target(int targetStage) {
        if (targetStage == currentStage) {
            DisplayPlanetEngineFire.targetStage = targetStage;
            return true;
        }
        return false;
    }

    private static void renderShape(VertexBuffer vertexBuffer, double x, double y, double z, float yaw, float pitch, float roll, float xScale, float yScale, float zScale, int color) {
        if (currentStage == 0 || currentStage != targetStage)
            return;
        if (poseStack == null || projectionMatrix == null)
            return;
        if (vertexBuffer == null)
            return;
        float i, j, k;
        if (worldCoordinate) {
            Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            i = (float) (x - pos.x());
            j = (float) (y - pos.y());
            k = (float) (z - pos.z());
        } else {
            i = (float) x;
            j = (float) y;
            k = (float) z;
        }
        poseStack.pushPose();
        poseStack.translate(i, j, k);
        poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
        poseStack.scale(xScale, yScale, zScale);
        poseStack.translate(offset.x(), offset.y(), offset.z());
        RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
        vertexBuffer.bind();
        vertexBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, vertexBuffer.getFormat().hasUV(0) ? GameRenderer.getPositionTexColorShader() : GameRenderer.getPositionColorShader());
        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            currentStage = 1;
            RenderSystem.depthMask(false);
            renderShapes(event);
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            currentStage = 0;
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            currentStage = 2;
            RenderSystem.depthMask(true);
            renderShapes(event);
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            currentStage = 0;
        }
    }

    private static void renderShapes(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
        if (level != null && entity != null) {
            poseStack = event.getPoseStack();
            projectionMatrix = event.getProjectionMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            main();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }
    }
}

