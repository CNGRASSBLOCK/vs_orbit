package net.cn_good_grass.vs_orbit.network.data;

import net.cn_good_grass.vs_orbit.client.render.object.PlanetEngine.PlanetEngineFire;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncPlanetEngineDataPacket {
    private final String PlanetEngint_Data;

    public SyncPlanetEngineDataPacket(String data) {
        this.PlanetEngint_Data = data;
    }

    public static void encode(SyncPlanetEngineDataPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.PlanetEngint_Data);
    }

    public static SyncPlanetEngineDataPacket decode(FriendlyByteBuf buffer) {
        return new SyncPlanetEngineDataPacket(buffer.readUtf());
    }


    public static void handle(SyncPlanetEngineDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            List<String> FireDataPackList = new ArrayList<>(List.of(packet.PlanetEngint_Data.split("断")));
            PlanetEngineFire.fires_cilent.clear();
            for (String string : FireDataPackList) {
                if (string.isEmpty()) continue;
                String pos = string.split("，")[0].replace("{","").replace("}","");
                BlockPos blockPos = new BlockPos(Integer.parseInt(pos.split(",")[0]), Integer.parseInt(pos.split(",")[1]), Integer.parseInt(pos.split(",")[2]));
                PlanetEngineFire planetEngineFire = new PlanetEngineFire(blockPos, string.split("，")[1], Integer.parseInt(string.split("，")[2]), Integer.parseInt(string.split("，")[3]));
                PlanetEngineFire.fires_cilent.add(planetEngineFire);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
