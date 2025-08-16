package net.cn_good_grass.vs_orbit.block.block_entities_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.OrbitalProjectorBlockEntity;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.jcm.vsch.api.resource.ModelTextures;
import net.jcm.vsch.api.resource.TextureLocation;
import net.jcm.vsch.client.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class OrbitalProjectorBlockEntityRenderer implements BlockEntityRenderer<OrbitalProjectorBlockEntity> {
    public OrbitalProjectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public final Map<Vector3d, Float> StarSizeList = new HashMap<>();
    public final Map<Vector3d, Quaternionf> StarRotateList = new HashMap<>();
    public final Map<Vector3d, Float> PlanetSizeList = new HashMap<>();
    public final Map<Vector3d, Quaternionf> PlanetRotateList = new HashMap<>();

    @Override
    public void render(final OrbitalProjectorBlockEntity blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay) {
        StarSizeList.clear();
        StarRotateList.clear();
        PlanetSizeList.clear();
        PlanetRotateList.clear();

        Vector3d center = new Vector3d();
        if (blockEntity.data_center instanceof Vector3d vector3d) center = vector3d; else if (blockEntity.data_center instanceof Astronomical astronomical) center = new Vector3d(astronomical.x, astronomical.y, astronomical.z);

        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldIDCilent(blockEntity.data_world, false);
        if (astronomicalPool != null) for (Astronomical astronomical : astronomicalPool.getAllAstronomical()) {
            Vector3d pos = new Vector3d(astronomical.x, astronomical.y, astronomical.z);
            if (center.distance(pos) <= blockEntity.data_radius) {
                Vector3d pos_data = pos.mul(0.0000025);
                if (astronomical.type.equals("cosmos:star")) {
                    StarSizeList.put(pos_data, 0.1f);
                    StarRotateList.put(pos_data, new Quaternionf(astronomical.rotate));
                } else if (astronomical.type.equals("cosmos:planet")) {
                    PlanetSizeList.put(pos_data, 0.1f);
                    PlanetRotateList.put(pos_data, new Quaternionf(astronomical.rotate));
                }
            }
        }


        for (Vector3d vector3d : StarSizeList.keySet()) drawStar(vector3d, StarRotateList.get(vector3d), StarSizeList.get(vector3d), poseStack, bufferSource, packedLight);
        for (Vector3d vector3d : PlanetSizeList.keySet()) drawPlanet(vector3d, PlanetRotateList.get(vector3d), PlanetSizeList.get(vector3d), poseStack, bufferSource, packedLight);
    }



    private static final Vector4f Color = new Vector4f(1, 1, 1, 1);

    private static final Map<Vector3i, ModelTextures> StarBlock = new HashMap<>();

    private static final Vector3i PlanetBlockSize;
    private static final ModelTextures PlanetBlockMode;

    private void drawStar(Vector3d pos, Quaternionf rotate, float size, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!rotate.isFinite() || Math.abs(rotate.lengthSquared() - 1.0f) > 1e-6f) rotate = new Quaternionf();

        poseStack.pushPose();
        poseStack.scale(size, size, size);
        poseStack.translate((0.5f + pos.x - 0.5f * size) / size, (0.5f + pos.y + 1.5f - 0.5f * size) / size, (0.5f + pos.z - 0.5f * size) / size);
        for (Vector3i vector3i : StarBlock.keySet()) RenderUtil.drawBoxWithTexture(poseStack, bufferSource.getBuffer(RenderType.translucent()), new RenderUtil.BoxLightMap().setAll(packedLight), StarBlock.get(vector3i), Color, new Vector3f(), rotate, vector3i, 1f);
        poseStack.popPose();
    }

    private void drawPlanet(Vector3d pos, Quaternionf rotate, float size, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!rotate.isFinite() || Math.abs(rotate.lengthSquared() - 1.0f) > 1e-6f) rotate = new Quaternionf();

        poseStack.pushPose();
        poseStack.scale(size, size, size);
        poseStack.translate((0.5f + pos.x - 0.5f * size) / size, (0.5f + pos.y + 1.5f - 0.5f * size) / size, (0.5f + pos.z - 0.5f * size) / size);
        RenderUtil.drawBoxWithTexture(poseStack, bufferSource.getBuffer(RenderType.translucent()), new RenderUtil.BoxLightMap().setAll(packedLight), PlanetBlockMode, Color, new Vector3f(), rotate, PlanetBlockSize, 1f);
        poseStack.popPose();
    }

    static {
        final ResourceLocation resource = new ResourceLocation(VSOrbitMod.MODID, "block/orbital_projector/cube");
        for (int i = 0; i < 5; i++) {
            ModelTextures First_Block = new ModelTextures(
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40),
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40),
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40),
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40),
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40),
                    TextureLocation.fromNonStandardSize(resource, i * 8, 0, 40)
            );
            StarBlock.put(new Vector3i(3 + i, 3 + i, 3 + i), First_Block);
        }

        PlanetBlockMode = new ModelTextures(
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40),
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40),
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40),
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40),
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40),
                TextureLocation.fromNonStandardSize(resource, 0, 8, 40)
        );
        PlanetBlockSize = new Vector3i(4, 4, 4);
    }
}