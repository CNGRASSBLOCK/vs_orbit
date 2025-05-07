package net.cn_good_grass.vs_orbit.item;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "vs_orbit");
    // 注册方块对应的物品
    public static final RegistryObject<Item> electromagnetic_tractor_item = ITEMS.register("electromagnetic_tractor", () -> new BlockItem(VSOrbitModBlocks.electromagnetic_tractor.get(), new Item.Properties()));
    public static final RegistryObject<Item> jump_engine_controller_item = ITEMS.register("jump_engine_controller", () -> new BlockItem(VSOrbitModBlocks.jump_engine_controller.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}