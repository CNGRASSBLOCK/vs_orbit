package net.cn_good_grass.vs_orbit.network.gui;

import net.cn_good_grass.vs_orbit.block.block_entities.ElectricalTrusterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncElectricalTrusterGUI(BlockPos blockPos, double force) {
    public static void encode(SyncElectricalTrusterGUI msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.blockPos);
        buf.writeDouble(msg.force);
    }

    public static SyncElectricalTrusterGUI decode(FriendlyByteBuf buf) {
        return new SyncElectricalTrusterGUI(buf.readBlockPos(), buf.readDouble());
    }

    public static void handle(SyncElectricalTrusterGUI msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer == null) return;
            ServerLevel level = serverPlayer.serverLevel();
            if (level.isLoaded(msg.blockPos())) {
                BlockEntity blockEntity = level.getBlockEntity(msg.blockPos());
                if (blockEntity instanceof ElectricalTrusterBlockEntity electricalTrusterBlockEntity) {
                    electricalTrusterBlockEntity.force = msg.force();
                    blockEntity.setChanged();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
