package net.cn_good_grass.vs_orbit.block.blocks;

import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.OrbitalControlConsoleBlockEntity;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class OrbitalControlConsoleBlock extends Block implements EntityBlock{
    public OrbitalControlConsoleBlock() {
        super(Properties.of()
                .strength(0.5f, 5f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
                .noOcclusion()
        );
    }

    @Override public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem) return tieredItem.getTier().getLevel() >= 2;else return super.canHarvestBlock(state, world, pos, player);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new OrbitalControlConsoleBlockEntity(pos, state); }

    private static final VoxelShape NORTH_SHAPE = Block.box(-16, 0, 0, 32, 16, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.box(-16, 0, 0, 32, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(0, 0, -16, 16, 16, 32);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, -16, 16, 16, 32);
    @Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(FACING).equals(Direction.NORTH)) return NORTH_SHAPE;
        else if (state.getValue(FACING).equals(Direction.SOUTH)) return SOUTH_SHAPE;
        else if (state.getValue(FACING).equals(Direction.EAST)) return EAST_SHAPE;
        else return WEST_SHAPE;
    }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        if (!(world instanceof ServerLevel)) return;
        world.scheduleTick(pos, this, 1);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel)) return;

        if (level.getBlockEntity(pos) instanceof OrbitalControlConsoleBlockEntity orbitalControlConsoleBlockEntity) orbitalControlConsoleBlockEntity.setRemoved();

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        world.scheduleTick(pos, this, 1);

        if (!(world.getBlockEntity(pos) instanceof OrbitalControlConsoleBlockEntity orbitalControlConsoleBlockEntity)) return;

        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }
}