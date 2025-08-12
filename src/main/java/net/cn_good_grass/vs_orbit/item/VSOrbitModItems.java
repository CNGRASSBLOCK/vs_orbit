package net.cn_good_grass.vs_orbit.item;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.item.items.ControlCircuitCoardItem;
import net.cn_good_grass.vs_orbit.item.items.GravitationalCoreItem;
import net.cn_good_grass.vs_orbit.item.items.MassCoreItem;
import net.cn_good_grass.vs_orbit.item.items.PowerCoreItem;
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
    public static final RegistryObject<Item> mass_generator = ITEMS.register("mass_generator", () -> new BlockItem(VSOrbitModBlocks.mass_generator.get(), new Item.Properties()));
    public static final RegistryObject<Item> electrical_truster = ITEMS.register("electrical_truster", () -> new BlockItem(VSOrbitModBlocks.electrical_truster.get(), new Item.Properties()));
    public static final RegistryObject<Item> celestial_tachymeter = ITEMS.register("celestial_tachymeter", () -> new BlockItem(VSOrbitModBlocks.celestial_tachymeter.get(), new Item.Properties()));
    public static final RegistryObject<Item> orbital_projector = ITEMS.register("orbital_projector", () -> new BlockItem(VSOrbitModBlocks.orbital_projector_block.get(), new Item.Properties()));
    //注册物品
    public static final RegistryObject<Item> gravitational_core = ITEMS.register("gravitational_core", () -> new GravitationalCoreItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> mass_core = ITEMS.register("mass_core", () -> new MassCoreItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> power_core = ITEMS.register("power_core", () -> new PowerCoreItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> control_circuit_board = ITEMS.register("control_circuit_board", () -> new ControlCircuitCoardItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}