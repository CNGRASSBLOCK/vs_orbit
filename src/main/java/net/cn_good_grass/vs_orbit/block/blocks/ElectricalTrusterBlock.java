package net.cn_good_grass.vs_orbit.block.blocks;

import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.block.block_entities.ElectricalTrusterBlockEntity;
import net.cn_good_grass.vs_orbit.gui.ElectromagneticTractorGUI.ElectromagneticTractorGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.common.VSOrbitForceInducedShips;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster.ThrusterData;
import net.lointain.cosmos.CosmosMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class ElectricalTrusterBlock extends Block implements EntityBlock {
    public ElectricalTrusterBlock() {
        super(Properties.of()
                .strength(5f, 90f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
                .noOcclusion()
        );
    }
    //方块方向
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ElectricalTrusterBlockEntity(pos, state); }

    private static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    private static final VoxelShape DOWN_SHAPE = Block.box(0, 8, 0, 16, 16, 16);
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 0, 16, 16, 8);
    private static final VoxelShape EAST_SHAPE = Block.box(0, 0, 0, 8, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(8, 0, 0, 16, 16, 16);
    @Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(FACING).equals(Direction.UP)) return UP_SHAPE;
        else if (state.getValue(FACING).equals(Direction.DOWN)) return DOWN_SHAPE;
        else if (state.getValue(FACING).equals(Direction.NORTH)) return NORTH_SHAPE;
        else if (state.getValue(FACING).equals(Direction.SOUTH)) return SOUTH_SHAPE;
        else if (state.getValue(FACING).equals(Direction.EAST)) return EAST_SHAPE;
        else return WEST_SHAPE;
    }


    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        if (!(world instanceof ServerLevel)) return;
        world.scheduleTick(pos, this, 1);

        VSOrbitForceInducedShips ships = VSOrbitForceInducedShips.get(world, pos);
        if (ships == null) return;

        ships.addThruster(pos, new ThrusterData(VectorConversionsMCKt.toJOMLD(blockstate.getValue(FACING).getOpposite().getNormal()), 0, ThrusterData.ThrusterMode.POSITION));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel)) return;

        VSOrbitForceInducedShips ships = VSOrbitForceInducedShips.get(level, pos);
        if (ships == null) return;
        ships.removeThruster(pos);

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);

        event_dispose(blockstate, world, pos);

        if (!(world.getBlockEntity(pos) instanceof ElectricalTrusterBlockEntity blockEntity)) return;
        blockEntity.sendToClient();

        world.scheduleTick(pos, this, 1);
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if (!(world instanceof ServerLevel)) return InteractionResult.CONSUME;

        super.use(blockstate, world, pos, entity, hand, hit);

        if (entity instanceof ServerPlayer serverPlayer) { NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override public Component getDisplayName() { return Component.literal("ElectromagneticTractorGUI"); }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ElectromagneticTractorGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)); }
            }, pos); }

        return InteractionResult.CONSUME;
    }

    @Override public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction direction) { return true; }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!(world instanceof ServerLevel)) return;// 确保只在服务端执行
        ElectricalTrusterBlockEntity blockEntity = (ElectricalTrusterBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;
        blockEntity.red_stone_power = world.getBestNeighborSignal(pos);
    }

    private static void event_dispose(BlockState blockstate, ServerLevel world, BlockPos pos) {
        ElectricalTrusterBlockEntity blockEntity = (ElectricalTrusterBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;

        VSOrbitForceInducedShips ships = VSOrbitForceInducedShips.get(world, pos);
        if (ships == null) return;

        double force = blockEntity.force;

        ThrusterData thruster = ships.getThrusterAtPos(pos);
        if (thruster == null) ships.addThruster(pos, new ThrusterData(VectorConversionsMCKt.toJOMLD(blockstate.getValue(FACING).getOpposite().getNormal()), 0, ThrusterData.ThrusterMode.POSITION));
        else {
            thruster.mode = ThrusterData.ThrusterMode.valueOf(blockEntity.mode.toString());
            thruster.throttle = (float) force * blockEntity.red_stone_power / 15;
        }
    }

    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos pos, RandomSource p_220830_) {
        super.animateTick(p_220827_, p_220828_, pos, p_220830_);

        Direction facing = p_220827_.getValue(FACING);

        int particleCount = 16;

        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2 / particleCount) * i;
            Vec3 particlePos = new Vec3(Math.cos(angle) * 0.2, 0.5, Math.sin(angle) * 0.2);

            if (facing.equals(Direction.NORTH)) particlePos = new Vec3(particlePos.x, particlePos.z, particlePos.y);
            else if (facing.equals(Direction.SOUTH))
                particlePos = new Vec3(particlePos.x, particlePos.z, particlePos.y);
            if (facing.equals(Direction.EAST)) particlePos = new Vec3(particlePos.y, particlePos.x, particlePos.z);
            else if (facing.equals(Direction.WEST))
                particlePos = new Vec3(particlePos.y - 3, particlePos.x, particlePos.z);
            if (facing.equals(Direction.DOWN)) particlePos = new Vec3(-particlePos.z, particlePos.y, particlePos.x);

            p_220828_.addParticle(new DustParticleOptions(new Vector3f(0, 1, 1), 0.3f),
                    pos.getX() + 0.5 + particlePos.x,
                    pos.getY() + 0.5 + particlePos.y,
                    pos.getZ() + 0.5 + particlePos.z,
                    facing.getStepX(),
                    facing.getStepY(),
                    facing.getStepZ()
            );
        }
    }

    public enum Mode {
        POSITION,
        GLOBAL;
    }
}