package net.cn_good_grass.vs_orbit.procedures.cosmos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CableConnectionHelper {
    public static void setCableConnections(LevelAccessor world, BlockPos pos, Direction[] directions, boolean value) {
        if (world.isClientSide()) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return;

        for (Direction direction : directions) setCableConnection(blockEntity, "cablesFCopper" + direction.name().charAt(0) + direction.getSerializedName().substring(1), value);


        setCableConnection(blockEntity, "Eflow", value);
        syncBlockUpdate(world, pos);
    }

    public static void setCableConnection(BlockEntity blockEntity, String key, boolean value) { blockEntity.getPersistentData().putBoolean(key, value); }

    public static void syncBlockUpdate(LevelAccessor world, BlockPos pos) {
        if (world instanceof Level level) {
            BlockState state = world.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public static boolean hasCableConnection(BlockEntity blockEntity, String direction) { return blockEntity.getPersistentData().getBoolean("cablesFCopper" + direction); }
}