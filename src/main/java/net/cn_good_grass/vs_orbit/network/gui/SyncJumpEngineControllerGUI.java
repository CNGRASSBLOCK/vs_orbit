package net.cn_good_grass.vs_orbit.network.gui;

import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncJumpEngineControllerGUI(BlockPos blockPos, CompoundTag data) {
    public static void encode(SyncJumpEngineControllerGUI msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.blockPos);
        buf.writeNbt(msg.data);

    }

    public static SyncJumpEngineControllerGUI decode(FriendlyByteBuf buf) {
        return new SyncJumpEngineControllerGUI(buf.readBlockPos(), buf.readNbt());
    }

    public static void handle(SyncJumpEngineControllerGUI msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer == null) return;
            ServerLevel level = serverPlayer.serverLevel();
            if (level.isLoaded(msg.blockPos())) {
                BlockEntity blockEntity = level.getBlockEntity(msg.blockPos());
                if (blockEntity instanceof JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity) {
                    jumpEngineControllerBlockEntity.setting = new JumpEngineControllerBlockEntity.SettingCompoundTag(msg.data());
                    blockEntity.setChanged();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
