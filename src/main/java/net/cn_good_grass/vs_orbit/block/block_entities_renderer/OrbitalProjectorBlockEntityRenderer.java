package net.cn_good_grass.vs_orbit.block.block_entities_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cn_good_grass.vs_orbit.block.block_entities.OrbitalProjectorBlockEntity;
import net.cn_good_grass.vs_orbit.item.VSOrbitModItems;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.api.resource.ModelTextures;
import net.jcm.vsch.api.resource.TextureLocation;
import net.jcm.vsch.blocks.entity.GyroBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class OrbitalProjectorBlockEntityRenderer implements BlockEntityRenderer<OrbitalProjectorBlockEntity> {
    private static final Vector4f ONE4 = new Vector4f(1, 1, 1, 1);
    private static final Vector3f HALF3 = new Vector3f(0.5f, 0.5f, 0.5f);
    private static final Vector3i CORE_SIZE = new Vector3i(6, 6, 6);
    private static final ModelTextures CORE_MODEL;

    static {
        final ResourceLocation resource = new ResourceLocation(VSCHMod.MODID, "block/gyro");
        CORE_MODEL = new ModelTextures(
                TextureLocation.fromNonStandardSize(resource, 12, 44, 128),
                TextureLocation.fromNonStandardSize(resource, 6, 44, 128),
                TextureLocation.fromNonStandardSize(resource, 6, 50, 128),
                TextureLocation.fromNonStandardSize(resource, 18, 50, 128),
                TextureLocation.fromNonStandardSize(resource, 12, 50, 128),
                TextureLocation.fromNonStandardSize(resource, 0, 50, 128)
        );
    }

    private final BlockEntityRendererProvider.Context ctx;

    public OrbitalProjectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.ctx = context;
        this.itemRenderer = context.getItemRenderer();
    }

    private final ItemRenderer itemRenderer;

    @Override
    public void render(final OrbitalProjectorBlockEntity blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay) {
        poseStack.pushPose();
        // 调整位置（方块中心上方）
        poseStack.translate(0.5, 1.25 + Math.sin((blockEntity.getLevel().getGameTime() + partialTick) / 8.0) / 4.0, 0.5);
        // 旋转动画
        poseStack.mulPose(Axis.YP.rotationDegrees((blockEntity.getLevel().getGameTime() + partialTick) * 4));
        // 渲染物品（使用GROUND模式）
        itemRenderer.renderStatic(new ItemStack(VSOrbitModItems.orbital_projector.get(), 1), ItemDisplayContext.GROUND, 15, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}