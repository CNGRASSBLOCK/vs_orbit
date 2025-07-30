package net.cn_good_grass.vs_orbit.network.gui;

import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncMassGeneratorGUI(BlockPos blockPos, double mass) {
    public static void encode(SyncMassGeneratorGUI msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.blockPos);
        buf.writeDouble(msg.mass);

    }

    public static SyncMassGeneratorGUI decode(FriendlyByteBuf buf) {
        return new SyncMassGeneratorGUI(buf.readBlockPos(), buf.readDouble());
    }

    public static void handle(SyncMassGeneratorGUI msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer == null) return;
            ServerLevel level = serverPlayer.serverLevel();
            if (level.isLoaded(msg.blockPos())) {
                BlockEntity blockEntity = level.getBlockEntity(msg.blockPos());
                if (blockEntity instanceof MassGeneratorBlockEntity massGeneratorBlockEntity) {
                    massGeneratorBlockEntity.mass = msg.mass();
                    blockEntity.setChanged();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
