package net.cn_good_grass.vs_orbit.client.render.object.Splinter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cn_good_grass.vs_orbit.network.SyncDataTick;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.SplinterAstronomical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.*;

@Mod.EventBusSubscriber(modid = "vs_orbit", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DisplaySplinter {
    private static final List<SplinterAstronomical> SplinterList = new ArrayList<>();
    private static final Map<String, BlockState> SplinterBlock = new HashMap<>();

    @SubscribeEvent
    public static void renderModels(RenderLevelStageEvent event) {
        provider = event;
        if (provider.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        RenderSystem.disableDepthTest();
        RenderSystem.setShaderFogStart(2147463647);
        RenderSystem.setShaderFogEnd(2147463647);

        for (SplinterAstronomical astronomical : SplinterList) {
            Vector3d rotate = astronomical.rotate.getEulerAnglesXYZ(new Vector3d(0, 0, 0));
            renderBlock(SplinterBlock.get(astronomical.name), astronomical.x, astronomical.y, astronomical.z, (float) Math.toDegrees(rotate.x), (float) Math.toDegrees(rotate.y), (float) Math.toDegrees(rotate.z), (float) (astronomical.mass / 750));
        }
    }

    private static final List<BlockState> blockStates = List.of(new BlockState[]{
            Blocks.MAGMA_BLOCK.defaultBlockState(),
            Blocks.MAGMA_BLOCK.defaultBlockState(),
            Blocks.GLOWSTONE.defaultBlockState(),
            Blocks.OCHRE_FROGLIGHT.defaultBlockState(),
            Blocks.SHROOMLIGHT.defaultBlockState(),
            Blocks.SHROOMLIGHT.defaultBlockState()
    });
    private static int tick = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        String WorldId = "";
        if (Minecraft.getInstance().level != null) WorldId = Minecraft.getInstance().level.dimension().location().toString();
        SplinterList.clear();
        if (tick >= 3) SplinterBlock.clear();
        for (AstronomicalPool astronomicalPool : SyncDataTick.New_Gravitation_Core_World_Bus) if (astronomicalPool.WorldId.equals(WorldId)) for (Astronomical astronomical : astronomicalPool.getAllAstronomical()) if (astronomical instanceof SplinterAstronomical splinterAstronomical) {
            SplinterList.add(splinterAstronomical);
            if (tick >= 3) SplinterBlock.put(astronomical.name, blockStates.get((int) (Math.random() * 6)));
        }

        if (tick >= 3) tick = 0;
        tick++;

    }

    private static RenderLevelStageEvent provider = null;
    private static final RandomSource SHARED_RANDOM = RandomSource.create(42L);

    public static void renderBlock(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale) {
        if (blockState == null) return;
        if (blockState.getRenderShape() != RenderShape.MODEL) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        Vec3 cameraPos = provider.getCamera().getPosition();

        PoseStack poseStack = provider.getPoseStack();
        poseStack.pushPose();
        try {
            poseStack.translate(x - cameraPos.x(), y - cameraPos.y(), z - cameraPos.z());

            poseStack.mulPose(new Quaternionf().rotateYXZ(-yaw * Mth.DEG_TO_RAD, pitch * Mth.DEG_TO_RAD, -roll * Mth.DEG_TO_RAD));

            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5F, -0.5F, -0.5F);

            BakedModel bakedModel = dispatcher.getBlockModel(blockState);

            ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();
            for (RenderType renderType : bakedModel.getRenderTypes(blockState, SHARED_RANDOM, ModelData.EMPTY)) modelRenderer.tesselateBlock(level, bakedModel, blockState, BlockPos.containing(x, y, z), poseStack, mc.renderBuffers().bufferSource().getBuffer(Sheets.translucentCullBlockSheet()), false, SHARED_RANDOM, 42L, 15728880);
        } finally {
            poseStack.popPose();
        }
    }
}
