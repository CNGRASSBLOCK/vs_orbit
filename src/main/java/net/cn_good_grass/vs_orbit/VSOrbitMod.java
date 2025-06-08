package net.cn_good_grass.vs_orbit;

import com.mojang.logging.LogUtils;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.cn_good_grass.vs_orbit.gui.VSOrbitModMenus;
import net.cn_good_grass.vs_orbit.item.VSOrbitModItems;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.GravitationWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
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
        //注册事件
        MinecraftForge.EVENT_BUS.register(new GravitationWorld()); //注册引力更新事件
//        MinecraftForge.EVENT_BUS.register(new OnPlayerRender());
//        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        //注册各种杂七杂八的
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        VSOrbitModBlocks.register(eventBus);
        VSOrbitModBlockEntities.register(eventBus);
        VSOrbitModItems.register(eventBus);
        VSOrbitModEntities.REGISTRY.register(eventBus);
        VSOrbitModMenus.REGISTRY.register(eventBus);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // 注册客户端事件
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;
    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
    public static void queueServerWork(int tick, Runnable action) { if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) workQueue.add(new AbstractMap.SimpleEntry<>(action, tick)); }
    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach(work -> { work.setValue(work.getValue() - 1);if (work.getValue() == 0) actions.add(work); });
            actions.forEach(e -> e.getKey().run());
            workQueue.removeAll(actions);
        }
    }
}

