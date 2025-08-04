package net.cn_good_grass.vs_orbit;

import com.mojang.logging.LogUtils;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntitiesRenderer;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.cn_good_grass.vs_orbit.gui.VSOrbitModMenus;
import net.cn_good_grass.vs_orbit.item.VSOrbitModCreativeTab;
import net.cn_good_grass.vs_orbit.item.VSOrbitModItems;
import net.cn_good_grass.vs_orbit.network.NetworkHandler;
import net.cn_good_grass.vs_orbit.procedures.create.CreateRegistrar;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core.ServerAction;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

    private static final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    public VSOrbitMod()
    {
        NetworkHandler.register();
        //注册事件
        VSOrbitModBlocks.register(eventBus);
        VSOrbitModBlockEntities.register(eventBus);
        VSOrbitModItems.register(eventBus);
        VSOrbitModCreativeTab.register(eventBus);
        VSOrbitModMenus.REGISTRY.register(eventBus);
        VSOrbitModEntities.REGISTRY.register(eventBus);

        MinecraftForge.EVENT_BUS.register(new ServerAction()); //模拟线程启动

        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        CreateRegistrar.register();
    }

    private void onClientSetup(FMLClientSetupEvent event) {

    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;
    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

}

