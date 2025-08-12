package net.cn_good_grass.vs_orbit.block.block_entities_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.OrbitalProjectorBlockEntity;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.api.resource.ModelTextures;
import net.jcm.vsch.api.resource.TextureLocation;
import net.jcm.vsch.client.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MassGeneratorBlockEntityRenderer implements BlockEntityRenderer<MassGeneratorBlockEntity> {
    private static final Vector4f Color = new Vector4f(1, 1, 1, 1);
    private static final Vector3i CoreBlockSize = new Vector3i(8, 8, 8);
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


    public MassGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(final MassGeneratorBlockEntity blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay) {
        RenderUtil.drawBoxWithTexture(poseStack, bufferSource.getBuffer(RenderType.translucent()), new RenderUtil.BoxLightMap().setAll(packedLight), CoreBlockModel, Color, new Vector3f(), blockEntity.cube, CoreBlockSize, 1f);
    }
}