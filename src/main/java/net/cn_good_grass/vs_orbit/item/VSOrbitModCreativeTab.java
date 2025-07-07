package net.cn_good_grass.vs_orbit.item;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VSOrbitMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + VSOrbitMod.MODID + ".main_tab"))
                    .icon(() -> new ItemStack(VSOrbitModItems.jump_engine_controller_item.get()))
                    .displayItems((params, output) -> {
                        output.accept(VSOrbitModItems.gravitational_core.get());
                        output.accept(VSOrbitModItems.control_circuit_board.get());
                        output.accept(VSOrbitModItems.jump_engine_controller_item.get());
                        output.accept(VSOrbitModItems.electromagnetic_tractor_item.get());
                        output.accept(VSOrbitModItems.mass_generator.get());
                    }).build());

    public static void register(IEventBus eventBus) { CREATIVE_MODE_TABS.register(eventBus); }
}
