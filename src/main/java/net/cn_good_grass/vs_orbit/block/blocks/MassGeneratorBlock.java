package net.cn_good_grass.vs_orbit.block.blocks;

import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.gui.MassGeneratorGUI.MassGeneratorGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class MassGeneratorBlock extends Block implements EntityBlock{
    public MassGeneratorBlock() {
        super(Properties.of()
                .strength(5f, 75f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
                .noOcclusion()
        );
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MassGeneratorBlockEntity(pos, state); }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if (!(world instanceof ServerLevel)) return InteractionResult.CONSUME;

        super.use(blockstate, world, pos, entity, hand, hit);

        if (entity instanceof ServerPlayer serverPlayer) { NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override public Component getDisplayName() {
                    return Component.literal("MassGeneratorGUI");
                }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new MassGeneratorGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)); }
            }, pos); }

        return InteractionResult.CONSUME;
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
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);

        MassGeneratorBlockEntity blockEntity = (MassGeneratorBlockEntity) world.getBlockEntity(pos); //自动保存 不知道有没有用
        if (blockEntity == null) return;



        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship == null) return;
        Astronomical astronomical = AstronomicalPool.getFromWorldID(world.dimension().location().toString()).getAstronomical("VSShip-" + ship.getId());
        if (astronomical == null) return;

        CompoundTag addMass = astronomical.Tag.getCompound("vs_orbit:add_mass");
        addMass.putDouble(String.valueOf(pos.asLong()), blockEntity.mass * world.getBestNeighborSignal(pos) / 16);
        astronomical.Tag.put("vs_orbit:add_mass", addMass);


        blockEntity.setChanged();
        world.scheduleTick(pos, this, 1);
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }
}