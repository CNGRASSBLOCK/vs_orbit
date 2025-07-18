package net.cn_good_grass.vs_orbit.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class  ElectromagneticTractorBlock extends Block{
    public ElectromagneticTractorBlock() {
        super(Properties.of()
                .strength(5f, 75f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
        );
    }

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty ROTATE = BooleanProperty.create("rotate");
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); builder.add(ROTATE); }
    public BlockState rotate(BlockState state, Rotation rot) { return state.setValue(FACING, rot.rotate(state.getValue(FACING))); }
    public BlockState mirror(BlockState state, Mirror mirrorIn) { return state.rotate(mirrorIn.getRotation(state.getValue(FACING))); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() == null) { return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()); }
        if (context.getPlayer().isShiftKeyDown()) {
            BlockPos placementPos = context.getClickedPos();
            Direction clickedFace = context.getClickedFace();
            BlockState ChickBlockState = context.getPlayer().level().getBlockState(placementPos.relative(clickedFace.getOpposite()));
            if (ForgeRegistries.BLOCKS.getKey(ChickBlockState.getBlock()).toString().equals("vs_orbit:jump_engine_controller")) {
                return this.defaultBlockState().setValue(FACING, ChickBlockState.getValue(BlockStateProperties.FACING).getOpposite());
            } else if (ForgeRegistries.BLOCKS.getKey(ChickBlockState.getBlock()).toString().equals("vs_orbit:electromagnetic_tractor")) {
                return this.defaultBlockState().setValue(FACING, ChickBlockState.getValue(BlockStateProperties.FACING));
            }
        }
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);

        if (entity.isShiftKeyDown()) world.setBlock(pos, blockstate.setValue(ROTATE, !blockstate.getValue(ROTATE)), Block.UPDATE_ALL);

        return InteractionResult.CONSUME;
    }
}