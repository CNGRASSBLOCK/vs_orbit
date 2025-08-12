package net.cn_good_grass.vs_orbit.block.block_entities_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
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

public class CelestialTachymeterBlockEntityRenderer implements BlockEntityRenderer<CelestialTachymeterBlockEntity> {
    private static final Vector4f Color = new Vector4f(1, 1, 1, 1);
    private static final Vector3i CoreBlockSize = new Vector3i(4, 4, 4);
    private static final ModelTextures CoreBlockModel;

    static {
        final ResourceLocation resource = new ResourceLocation(VSOrbitMod.MODID, "block/mass_generator/cube");
        CoreBlockModel = new ModelTextures(
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8),
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8),
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8),
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8),
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8),
                TextureLocation.fromNonStandardSize(resource, 0, 0, 8)
        );
    }


    public CelestialTachymeterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(final CelestialTachymeterBlockEntity blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay) {
        Vector3d pos = new Vector3d(blockEntity.speed).mul(-0.01);
        if (pos.x >= 0.1875) { pos.x = 0.1875; } else if (pos.x <= -0.1875) { pos.x = -0.1875; }
        if (pos.y >= 0.1875) { pos.y = 0.1875; } else if (pos.y <= -0.1875) { pos.y = -0.1875; }
        if (pos.z >= 0.1875) { pos.z = 0.1875; } else if (pos.z <= -0.1875) { pos.z = -0.1875; }

        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);
        RenderUtil.drawBoxWithTexture(poseStack, bufferSource.getBuffer(RenderType.translucent()), new RenderUtil.BoxLightMap().setAll(packedLight), CoreBlockModel, Color, new Vector3f(), new Quaternionf(), CoreBlockSize, 1f);
        poseStack.popPose();
    }
}