package net.cn_good_grass.vs_orbit.client.render.object.PlanetEngine;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PlanetEngineFire {
    public static final List<PlanetEngineFire> fires_server = new ArrayList<>();
    public static final List<PlanetEngineFire> fires_cilent = new ArrayList<>();

    public BlockPos blockPos;
    public String WorldId;
    public int r;
    public int h;

    public PlanetEngineFire(BlockPos blockPos, String WorldId, int r, int h) {
        this.blockPos = blockPos;
        this.WorldId = WorldId;
        this.r = r;
        this.h = h;
    }
    
    @Override public String toString() { return "{" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + "}，" + WorldId + "，" + r + "，" + h; }
    @Override public boolean equals(Object obj) { return (obj instanceof PlanetEngineFire && this.toString().equals(obj.toString())); }
    public static boolean has(PlanetEngineFire planetEngineFire) {
        for (PlanetEngineFire planetEngineFires : PlanetEngineFire.fires_server) if (planetEngineFires.blockPos.equals(planetEngineFire.blockPos)) return true;
        return false;
    }
    public static int indexOf(PlanetEngineFire planetEngineFire) {
        for (int i = 0;i < PlanetEngineFire.fires_server.size();i++) if (PlanetEngineFire.fires_server.get(i).blockPos.equals(planetEngineFire.blockPos)) return i;
        return -1;
    }

    public void renderFire(PoseStack poseStack, MultiBufferSource buffer) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.translate(this.blockPos.getX() - camPos.x + 0.5, this.blockPos.getY() - camPos.y + 0.5, this.blockPos.getZ() - camPos.z + 0.5);

        buildConeModel(buffer, poseStack.last().pose(), poseStack.last().normal(), this);
        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void buildConeModel(MultiBufferSource buffer, Matrix4f matrix, Matrix3f normalMatrix, PlanetEngineFire fire) {
        Vector3f normal = new Vector3f(0, 1, 0);
        normal.mul(normalMatrix);
        VertexConsumer consumer;
        if (fire.r < 3) fire.r = 3;
        if (fire.h < 1) fire.h = 1;
        float r;
        int h;

        consumer = buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation("vs_orbit:textures/object/planet_engine_fire/fire_" + (int) (Math.random() * 4 + 1) + ".png")));
        r = (float) (fire.r - 1);
        h = fire.h;
        for (int i = 0; i <= 31; i += 2) {
            consumer.vertex(matrix, 0, h, 0).color(1f, 1f, 1f, 1f).uv((float) i / 32, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();

            float angle = i * (float) (2 * Math.PI / 32);
            float angle1 = (i + 1) * (float) (2 * Math.PI / 32);
            float angle2 = (i + 2) * (float) (2 * Math.PI / 32);

            consumer.vertex(matrix, r * (float) Math.cos(angle), 0, r * (float) Math.sin(angle)).color(1f, 1f, 1f, 1f).uv((float) i / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle1), 0, r * (float) Math.sin(angle1)).color(1f, 1f, 1f, 1f).uv((float) (i + 1) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle2), 0, r * (float) Math.sin(angle2)).color(1f, 1f, 1f, 1f).uv((float) (i + 2) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
        }

        consumer = buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation("vs_orbit:textures/object/planet_engine_fire/fire_out.png")));
        r = (float) fire.r;
        h = fire.h;
        for (int i = 0; i <= 31; i += 2) {
            consumer.vertex(matrix, 0, h, 0).color(1f, 1f, 1f, 1f).uv((float) i / 32, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();

            float angle = i * (float) (2 * Math.PI / 32);
            float angle1 = (i + 1) * (float) (2 * Math.PI / 32);
            float angle2 = (i + 2) * (float) (2 * Math.PI / 32);

            consumer.vertex(matrix, r * (float) Math.cos(angle), 0, r * (float) Math.sin(angle)).color(1f, 1f, 1f, 1f).uv((float) i / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle1), 0, r * (float) Math.sin(angle1)).color(1f, 1f, 1f, 1f).uv((float) (i + 1) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle2), 0, r * (float) Math.sin(angle2)).color(1f, 1f, 1f, 1f).uv((float) (i + 2) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
        }

        consumer = buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation("vs_orbit:textures/object/planet_engine_fire/fire_core_out.png")));
        r = (float) (fire.r * 0.75);
        h = (int) (fire.h * 0.75);
        for (int i = 0; i <= 31; i += 2) {
            consumer.vertex(matrix, 0, h, 0).color(1f, 1f, 1f, 1f).uv((float) i / 32, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();

            float angle = i * (float) (2 * Math.PI / 32);
            float angle1 = (i + 1) * (float) (2 * Math.PI / 32);
            float angle2 = (i + 2) * (float) (2 * Math.PI / 32);

            consumer.vertex(matrix, r * (float) Math.cos(angle), 0, r * (float) Math.sin(angle)).color(1f, 1f, 1f, 1f).uv((float) i / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle1), 0, r * (float) Math.sin(angle1)).color(1f, 1f, 1f, 1f).uv((float) (i + 1) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle2), 0, r * (float) Math.sin(angle2)).color(1f, 1f, 1f, 1f).uv((float) (i + 2) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
        }

        consumer = buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation("vs_orbit:textures/object/planet_engine_fire/fire_core.png")));
        r = (float) (fire.r * 0.6);
        h = (int) (fire.h * 0.75);
        for (int i = 0; i <= 31; i += 2) {
            consumer.vertex(matrix, 0, h, 0).color(1f, 1f, 1f, 1f).uv((float) i / 32, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();

            float angle = i * (float) (2 * Math.PI / 32);
            float angle1 = (i + 1) * (float) (2 * Math.PI / 32);
            float angle2 = (i + 2) * (float) (2 * Math.PI / 32);

            consumer.vertex(matrix, r * (float) Math.cos(angle), 0, r * (float) Math.sin(angle)).color(1f, 1f, 1f, 1f).uv((float) i / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle1), 0, r * (float) Math.sin(angle1)).color(1f, 1f, 1f, 1f).uv((float) (i + 1) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
            consumer.vertex(matrix, r * (float) Math.cos(angle2), 0, r * (float) Math.sin(angle2)).color(1f, 1f, 1f, 1f).uv((float) (i + 2) / 32, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normal.x(), normal.y(), normal.z()).endVertex();
        }
    }
}
