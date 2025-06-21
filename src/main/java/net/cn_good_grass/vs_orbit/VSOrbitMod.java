package net.cn_good_grass.vs_orbit;

import com.mojang.logging.LogUtils;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.cn_good_grass.vs_orbit.gui.VSOrbitModMenus;
import net.cn_good_grass.vs_orbit.item.VSOrbitModItems;
import net.cn_good_grass.vs_orbit.network.NetworkHandler;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.ParticleWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(VSOrbitMod.MODID)
public class VSOrbitMod
{
    //模组名字
    public static final String MODID = "vs_orbit";
    //日志输出
    public static final Logger LOGGER = LogUtils.getLogger();

    public VSOrbitMod()
    {
        NetworkHandler.register();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::onClientSetup);
        VSOrbitModBlocks.register(eventBus);
        VSOrbitModBlockEntities.register(eventBus);
        VSOrbitModItems.register(eventBus);
        VSOrbitModEntities.REGISTRY.register(eventBus);
        VSOrbitModMenus.REGISTRY.register(eventBus);
        //注册事件
        MinecraftForge.EVENT_BUS.register(new ParticleWorld());
        //MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(new PlayerRender());
    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;
    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
}

