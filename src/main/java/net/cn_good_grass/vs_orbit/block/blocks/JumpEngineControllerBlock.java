package net.cn_good_grass.vs_orbit.block.blocks;

import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.cilent.render.PlanetEngine.PlanetEngineFire;
import net.cn_good_grass.vs_orbit.entity.ThrusterCore.ThrusterCoreEntity;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.cn_good_grass.vs_orbit.gui.JumpEngineControllerGUI.JumpEngineControllerGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.ShipAction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Force.decomposeForce;

public class JumpEngineControllerBlock extends Block implements EntityBlock {
    public JumpEngineControllerBlock() {
        super(Properties.of()
                .strength(5f, 90f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 12)
                .noOcclusion()
        );
    }
    //方块方向
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()); }
    public BlockState rotate(BlockState state, Rotation rot) { return state.setValue(FACING, rot.rotate(state.getValue(FACING))); }
    public BlockState mirror(BlockState state, Mirror mirrorIn) { return state.rotate(mirrorIn.getRotation(state.getValue(FACING))); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new JumpEngineControllerBlockEntity(pos, state); }

    public float getThrottle(BlockState state, int signal) { return 10000f; }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        if (!(world instanceof ServerLevel)) return;
        world.scheduleTick(pos, this, 1);
    }

    public static Integer computequeues = 0;

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        if (computequeues <= 32) { computequeues++; structural_inspection(blockstate, world, pos); computequeues--; } //结构检测

        display(blockstate, world, pos);

        event_dispose(blockstate, world, pos);

        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) world.getBlockEntity(pos); //自动保存 不知道有没有用
        if (blockEntity != null) blockEntity.setChanged();

        world.scheduleTick(pos, this, 1);
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if (!(world instanceof ServerLevel)) return InteractionResult.CONSUME;

        super.use(blockstate, world, pos, entity, hand, hit);

        if (entity instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override public Component getDisplayName() {
                    return Component.literal("JumpEngineControllerGUI");
                }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new JumpEngineControllerGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)); }
            }, pos);
        }

        return InteractionResult.CONSUME;
    }

    @Override public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {return true; }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!(world instanceof ServerLevel)) return;// 确保只在服务端执行
        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;
        blockEntity.red_stone_power = world.getBestNeighborSignal(pos);
    }

    public static void structural_inspection(BlockState blockstate, ServerLevel world, BlockPos pos) {
        //初始判断
        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) { return; }
        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);

        Direction facing_0 = blockstate.getValue(BlockStateProperties.FACING);
        if (facing_0.equals(Direction.UP)) { blockEntity.structure_state = "direction"; return; }
        if (facing_0.equals(Direction.DOWN) && ship == null) { blockEntity.structure_state = "right"; blockEntity.mode = "planet_engine"; return; }

        if (ship == null) { blockEntity.structure_state = "out"; return; }

        Direction.Axis currentAxis = facing_0.getAxis();
        List<Direction> perpendicularDirs = Arrays.stream(Direction.values()).filter(dir -> dir.getAxis() != currentAxis).toList();
        //检测周围4个方向
        List<Integer> radius = new ArrayList<>(List.of(0, 0, 0, 0));
        List<Boolean> face_end = new ArrayList<>(List.of(false, false, false, false));
        List<Vector3d> face_pos_offset = new ArrayList<>(List.of(new Vector3d(perpendicularDirs.get(0).getStepX(), perpendicularDirs.get(0).getStepY(), perpendicularDirs.get(0).getStepZ()), new Vector3d(perpendicularDirs.get(1).getStepX(), perpendicularDirs.get(1).getStepY(), perpendicularDirs.get(1).getStepZ()), new Vector3d(perpendicularDirs.get(2).getStepX(), perpendicularDirs.get(2).getStepY(), perpendicularDirs.get(2).getStepZ()), new Vector3d(perpendicularDirs.get(3).getStepX(), perpendicularDirs.get(3).getStepY(), perpendicularDirs.get(3).getStepZ())));
        for (int i = 1;i < 25;i++) {
            Vector3d vector3d = (new Vector3d(face_pos_offset.get(0))).mul(i);
            BlockPos blockPos = new BlockPos(pos.getX() + (int) vector3d.x, pos.getY() + (int) vector3d.y, pos.getZ() + (int) vector3d.z);
            if (!face_end.get(0)) if (ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") && world.getBlockState(blockPos).getValue(BlockStateProperties.FACING).equals(facing_0.getOpposite()) && world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) radius.set(0, radius.get(0) + 1); else face_end.set(0, true);

            vector3d = (new Vector3d(face_pos_offset.get(1))).mul(i);
            blockPos = new BlockPos(pos.getX() + (int) vector3d.x, pos.getY() + (int) vector3d.y, pos.getZ() + (int) vector3d.z);
            if (!face_end.get(1)) if (ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") && world.getBlockState(blockPos).getValue(BlockStateProperties.FACING).equals(facing_0.getOpposite()) && world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) radius.set(1, radius.get(1) + 1); else face_end.set(1, true);

            vector3d = (new Vector3d(face_pos_offset.get(2))).mul(i);
            blockPos = new BlockPos(pos.getX() + (int) vector3d.x, pos.getY() + (int) vector3d.y, pos.getZ() + (int) vector3d.z);
            if (!face_end.get(2)) if (ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") && world.getBlockState(blockPos).getValue(BlockStateProperties.FACING).equals(facing_0.getOpposite()) && !world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) radius.set(2, radius.get(2) + 1); else face_end.set(2, true);

            vector3d = (new Vector3d(face_pos_offset.get(3))).mul(i);
            blockPos = new BlockPos(pos.getX() + (int) vector3d.x, pos.getY() + (int) vector3d.y, pos.getZ() + (int) vector3d.z);
            if (!face_end.get(3)) if (ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") && world.getBlockState(blockPos).getValue(BlockStateProperties.FACING).equals(facing_0.getOpposite()) && !world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) radius.set(3, radius.get(3) + 1); else face_end.set(3, true);
        }
        blockEntity.structure_radius = Collections.min(radius);
        if (blockEntity.structure_radius < 3) { blockEntity.structure_state = "small"; return; }
        //检测周围方向向前
        List<Vector3d> vector3d = new ArrayList<>(List.of(new Vector3d(face_pos_offset.get(0)).mul(blockEntity.structure_radius + 1), new Vector3d(face_pos_offset.get(1)).mul(blockEntity.structure_radius + 1), new Vector3d(face_pos_offset.get(2)).mul(blockEntity.structure_radius + 1), new Vector3d(face_pos_offset.get(3)).mul(blockEntity.structure_radius + 1)));
        for (int i = 1;i < blockEntity.structure_radius + 2;i++) {
            Vector3d vector3d_main = new Vector3d(facing_0.getOpposite().getStepX(), facing_0.getOpposite().getStepY(), facing_0.getOpposite().getStepZ()).mul(i);
            BlockPos blockPos = new BlockPos((int) (pos.getX() + vector3d.get(0).x + vector3d_main.x), (int) (pos.getY() + vector3d.get(0).y + vector3d_main.y), (int) (pos.getZ() + vector3d.get(0).z + vector3d_main.z));
            if (!ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") || !world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) { blockEntity.structure_state = "incomplete"; return; }
            blockPos = new BlockPos((int) (pos.getX() + vector3d.get(1).x + vector3d_main.x), (int) (pos.getY() + vector3d.get(1).y + vector3d_main.y), (int) (pos.getZ() + vector3d.get(1).z + vector3d_main.z));
            if (!ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") || !world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) { blockEntity.structure_state = "incomplete"; return; }
            blockPos = new BlockPos((int) (pos.getX() + vector3d.get(2).x + vector3d_main.x), (int) (pos.getY() + vector3d.get(2).y + vector3d_main.y), (int) (pos.getZ() + vector3d.get(2).z + vector3d_main.z));
            if (!ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") || world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) { blockEntity.structure_state = "incomplete"; return; }
            blockPos = new BlockPos((int) (pos.getX() + vector3d.get(3).x + vector3d_main.x), (int) (pos.getY() + vector3d.get(3).y + vector3d_main.y), (int) (pos.getZ() + vector3d.get(3).z + vector3d_main.z));
            if (!ForgeRegistries.BLOCKS.getKey(world.getBlockState(blockPos).getBlock()).toString().equals("vs_orbit:electromagnetic_tractor") || world.getBlockState(blockPos).getValue(ElectromagneticTractorBlock.ROTATE)) { blockEntity.structure_state = "incomplete"; return; }
        }

        Vector3d center_pos = new Vector3d(new Vector3d(pos.getX(), pos.getY(), pos.getZ())).add(new Vector3d(facing_0.getOpposite().getStepX(), facing_0.getOpposite().getStepY(), facing_0.getOpposite().getStepZ()).mul(radius.get(0)));
        blockEntity.structure_state = "right";
        blockEntity.structure_center_pos = new BlockPos((int) center_pos.x, (int) center_pos.y, (int) center_pos.z);
    }

    public static void display(BlockState blockstate, ServerLevel world, BlockPos pos) {
        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;
        if (!blockEntity.structure_state.equals("right")) { return; }
        if (blockEntity.mode.equals("planet_engine")) {
            //行星发动机模式
            PlanetEngineFire planetEngineFire = new PlanetEngineFire(pos.relative(Direction.UP, 5), world.dimension().location().toString(), 23, 0);
            int index = PlanetEngineFire.indexOf(planetEngineFire);
            if (blockEntity.red_stone_power > 0) {
                if (PlanetEngineFire.has(planetEngineFire)) {
                    if (blockEntity.animation_tick >= 20) {
                        if (blockEntity.state.equals("charged")) blockEntity.state = "work";
                        planetEngineFire.h = 600;
                        if (index != -1) PlanetEngineFire.fires_server.set(index, planetEngineFire);
                    } else {
                        planetEngineFire.h = blockEntity.animation_tick * blockEntity.animation_tick / 2 * 3;
                        if (index != -1) PlanetEngineFire.fires_server.set(index, planetEngineFire);
                        blockEntity.state = "charged";
                        blockEntity.animation_tick++;
                    }
                } else {
                    PlanetEngineFire.fires_server.add(planetEngineFire);
                    blockEntity.animation_tick = 0;
                }
            } else {
                if (index != -1) PlanetEngineFire.fires_server.remove(index);
                blockEntity.state = "wait";
            }
        } else {
            //船只推进模式
            Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
            if (ship == null) { return; }
            ThrusterCoreEntity entity = (ThrusterCoreEntity) world.getEntity(blockEntity.display_entity_uuid);

            Vector3d block_pos = new Vector3d(blockEntity.structure_center_pos.getX(), blockEntity.structure_center_pos.getY(), blockEntity.structure_center_pos.getZ());
            if (entity == null) {
                entity = VSOrbitModEntities.THRUSTER_CORE.get().create(world);
                if (entity == null) return;
                entity.setPos(block_pos.x + 0.5, block_pos.y + 0.5, block_pos.z + 0.5);
                Direction facing = blockstate.getValue(BlockStateProperties.FACING).getOpposite();
                float YRot = 180f;
                if (facing.equals(Direction.SOUTH)) YRot = 0f;
                if (facing.equals(Direction.EAST)) YRot = -90f;
                if (facing.equals(Direction.WEST)) YRot = 90f;
                entity.setYBodyRot(YRot);
                entity.setYHeadRot(YRot);
                entity.setYRot(YRot);
                entity.getEntityData().set(ThrusterCoreEntity.engine_pos, pos);
                entity.getEntityData().set(ThrusterCoreEntity.scare, (float) (blockEntity.structure_radius / 2.5));
                world.addFreshEntity(entity);
                blockEntity.display_entity_uuid = entity.getUUID();
            }
        }
    }

    public static void event_dispose(BlockState blockstate, ServerLevel world, BlockPos pos) {
        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;

        if (!blockEntity.structure_state.equals("right")) blockEntity.state = "structure_error"; else if (blockEntity.state.equals("none") || blockEntity.state.equals("structure_error")) blockEntity.state = "wait";

        ServerShip ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship == null) return;

        Entity entity = world.getEntity(blockEntity.display_entity_uuid);
        if (!(entity instanceof ThrusterCoreEntity thrusterCoreEntity)) return;

        if (blockEntity.structure_state.equals("right") && blockEntity.red_stone_power > 0) {
            if (blockEntity.mode.equals("power")) {
                Quaterniondc ShipQuaternion = ship.getTransform().getShipToWorldRotation();
                Quaterniondc ForceQuaternion = ShipQuaternion.add((Quaterniondc) blockstate.getValue(BlockStateProperties.FACING).getRotation(), new Quaterniond());

                if (blockEntity.animation_tick <= 100) {
                    thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "charged");
                    blockEntity.animation_tick ++;
                } else if (blockEntity.animation_tick <= 120) {
                    thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "work");
                    blockEntity.animation_tick ++;
                } else {
                    double force = blockEntity.setting.getDouble("force");
                    Vector3d forceVector = decomposeForce(force, ForceQuaternion);

                    LoadedServerShip loadedServerShip = VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().getById(ship.getId());
                    if (loadedServerShip == null) return;
                    GameTickForceApplier applier = loadedServerShip.getAttachment(GameTickForceApplier.class);
                    if (applier != null) applier.applyInvariantForce(forceVector);
                }
            } else if (blockEntity.mode.equals("jump") && !blockEntity.red_stone_power_do) {
                if (blockEntity.animation_tick <= 200) {
                    thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "charged_high");
                    blockEntity.animation_tick ++;
                } else if (blockEntity.animation_tick <= 220) {
                    thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "work");
                    blockEntity.animation_tick ++;
                } else {
                    ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(world);
                    ShipAction.teleportShip(shipWorld, ship, new ShipTeleportDataImpl(new Vector3d(blockEntity.setting.getDouble("pos_x"), blockEntity.setting.getDouble("pos_y"), blockEntity.setting.getDouble("pos_z")), ship.getTransform().getShipToWorldRotation(), new Vector3d(), new Vector3d(), ship.getChunkClaimDimension(), null));
                    blockEntity.red_stone_power_do = true;
                    blockEntity.animation_tick = 0;
                    thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "spend");
                }
            } else if (blockEntity.mode.equals("planet_engine")) {}
        } else {
            blockEntity.red_stone_power_do = false;
            blockEntity.animation_tick = 0;
            thrusterCoreEntity.getEntityData().set(ThrusterCoreEntity.ANIMATION, "spend");
        }
    }
}