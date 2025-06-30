package net.cn_good_grass.vs_orbit.network;

import net.cn_good_grass.vs_orbit.network.packet.SyncAstronomicalPoolPacket;
import net.cn_good_grass.vs_orbit.network.packet.SyncPlanetEngineDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("vs_orbit", "main"), () -> "1.0", "1.0"::equals, "1.0"::equals);

    public static void register() {
        INSTANCE.registerMessage(0, SyncAstronomicalPoolPacket.class, SyncAstronomicalPoolPacket::encode, SyncAstronomicalPoolPacket::decode, SyncAstronomicalPoolPacket::handle);
        INSTANCE.registerMessage(1, SyncPlanetEngineDataPacket.class, SyncPlanetEngineDataPacket::encode, SyncPlanetEngineDataPacket::decode, SyncPlanetEngineDataPacket::handle);
    }
}
