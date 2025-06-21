package net.cn_good_grass.vs_orbit.network.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncParticlePoolPacket {
    private final long timestamp;
    private static long lastReceivedTimestamp = 0;
    private final List<ParticlePool> Gravitation_Core_World_Bus_Data;
    public static List<ParticlePool> Gravitation_Core_World_Bus_Data_Save = new ArrayList<>();

    public SyncParticlePoolPacket(List<ParticlePool> data, Long timestamp) {
        this.Gravitation_Core_World_Bus_Data = data;
        this.timestamp = timestamp;
    }
    public static void encode(SyncParticlePoolPacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.timestamp);
        buffer.writeInt(packet.Gravitation_Core_World_Bus_Data.size()); // 先写入列表大小
        for (ParticlePool item : packet.Gravitation_Core_World_Bus_Data) {
            buffer.writeUtf(item.toJsonObject().toString()); // 逐个写入列表元素
        };
    }
    public static SyncParticlePoolPacket decode(FriendlyByteBuf buffer) {
        long timestamp = buffer.readLong(); // 读取时间戳
        int size = buffer.readInt(); // 读取列表大小
        List<ParticlePool> dataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dataList.add(ParticlePool.getFromJsonObject(JsonParser.parseString(buffer.readUtf()).getAsJsonObject())); // 读取列表元素
        }
        return new SyncParticlePoolPacket(dataList, timestamp);
    }
    public static void handle(SyncParticlePoolPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().execute(() -> {
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            if (packet.timestamp > lastReceivedTimestamp) {
                lastReceivedTimestamp = packet.timestamp;
                Gravitation_Core_World_Bus_Data_Save = packet.Gravitation_Core_World_Bus_Data;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
