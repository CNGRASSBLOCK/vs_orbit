package net.cn_good_grass.vs_orbit.block.blocks;

import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.gui.menu.MassGeneratorGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
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
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class MassGeneratorBlock extends Block implements EntityBlock{
    public MassGeneratorBlock() {
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

        if (level.getBlockEntity(pos) instanceof MassGeneratorBlockEntity massGeneratorBlockEntity) massGeneratorBlockEntity.setRemoved();

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        world.scheduleTick(pos, this, 1);

        if (!(world.getBlockEntity(pos) instanceof MassGeneratorBlockEntity blockEntity)) return;

        blockEntity.cube.rotateX((float) Math.random() / 12.5f - 0.04f);
        blockEntity.cube.rotateY((float) Math.random() / 12.5f - 0.04f);
        blockEntity.cube.rotateZ((float) Math.random() / 12.5f - 0.04f);
        blockEntity.sendToClient();

        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship == null) return;
        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(world.dimension().location().toString());
        if (astronomicalPool == null) return;
        Astronomical astronomical = astronomicalPool.getAstronomical("VSShip-" + ship.getId());
        if (astronomical == null) return;

        CompoundTag addMass = astronomical.Tag.getCompound("vs_orbit:add_mass");
        addMass.putDouble(String.valueOf(pos.asLong()), blockEntity.mass * world.getBestNeighborSignal(pos) / 15);
        astronomical.Tag.put("vs_orbit:add_mass", addMass);

        blockEntity.setChanged();
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }
}