package net.cn_good_grass.vs_orbit.network.packet;

import com.google.gson.JsonParser;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class SyncAstronomicalPoolPacket {
    private final long timestamp;
    private static long lastReceivedTimestamp = 0;
    private final List<AstronomicalPool> Gravitation_Core_World_Bus_Data;
    public static List<AstronomicalPool> Gravitation_Core_World_Bus_Data_Save = new ArrayList<>();

    public SyncAstronomicalPoolPacket(List<AstronomicalPool> data, Long timestamp) {
        this.Gravitation_Core_World_Bus_Data = data;
        this.timestamp = timestamp;
    }
    public static void encode(SyncAstronomicalPoolPacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.timestamp);
        buffer.writeInt(packet.Gravitation_Core_World_Bus_Data.size()); // 先写入列表大小
        for (AstronomicalPool item : packet.Gravitation_Core_World_Bus_Data) {
            buffer.writeByteArray(compress(item.toJsonObject().toString().getBytes(StandardCharsets.UTF_8))); // 逐个写入列表元素
        };
    }
    public static SyncAstronomicalPoolPacket decode(FriendlyByteBuf buffer) {
        try {
            long timestamp = buffer.readLong(); // 读取时间戳
            int size = buffer.readInt(); // 读取列表大小
            List<AstronomicalPool> dataList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                AstronomicalPool astronomicalPool = AstronomicalPool.getFromJsonObject(JsonParser.parseString(new String(decompress(buffer.readByteArray()))).getAsJsonObject());
                if (astronomicalPool != null) dataList.add(astronomicalPool); // 读取列表元素
            }
            return new SyncAstronomicalPoolPacket(dataList, timestamp);
        } catch (Exception e) {
            return new SyncAstronomicalPoolPacket(new ArrayList<>(), 0L);
        }
    }
    public static void handle(SyncAstronomicalPoolPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().execute(() -> {
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
            if (packet.timestamp > lastReceivedTimestamp) {
                lastReceivedTimestamp = packet.timestamp;
                Gravitation_Core_World_Bus_Data_Save = packet.Gravitation_Core_World_Bus_Data;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 压缩字节数组 (最高性能设置)
    public static byte[] compress(byte[] data) {
        // 空输入处理
        if (data == null || data.length == 0) return new byte[0];

        // 使用最高速压缩级别 (牺牲压缩率换速度)
        Deflater deflater = new Deflater(Deflater.DEFLATED);
        deflater.setInput(data);
        deflater.finish();

        // 预分配缓冲区 (经验值: 原始数据长度 + 12.5% 头部空间)
        byte[] buffer = new byte[(int) (data.length * 1.125)];
        int compressedSize = deflater.deflate(buffer);
        deflater.end();

        // 精确截取实际压缩数据
        byte[] result = new byte[compressedSize];
        System.arraycopy(buffer, 0, result, 0, compressedSize);
        return result;
    }

    // 解压字节数组
    public static byte[] decompress(byte[] compressedData) {
        if (compressedData == null || compressedData.length == 0)
            return new byte[0];

        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        byte[] buffer = new byte[1024 * 1024]; // 1MB 初始缓冲区
        int decompressedSize = 0;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                bos.write(buffer, 0, count);
                decompressedSize += count;
            }
            return bos.toByteArray();
        } catch (DataFormatException | IOException e) {
            throw new RuntimeException("Invalid compressed data", e);
        } finally {
            inflater.end();
        }
    }
}
