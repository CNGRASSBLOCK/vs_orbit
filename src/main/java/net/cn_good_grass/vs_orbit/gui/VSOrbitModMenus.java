
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.cn_good_grass.vs_orbit.gui;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.gui.menu.ElectricalTrusterGUIMenu;
import net.cn_good_grass.vs_orbit.gui.menu.JumpEngineControllerGUIMenu;
import net.cn_good_grass.vs_orbit.gui.menu.MassGeneratorGUIMenu;
import net.cn_good_grass.vs_orbit.gui.menu.OrbitalProjectorGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VSOrbitMod.MODID);
	public static final RegistryObject<MenuType<JumpEngineControllerGUIMenu>> JumpEngineControllerGUI = REGISTRY.register("jump_engine_controller_gui", () -> IForgeMenuType.create(JumpEngineControllerGUIMenu::new));
	public static final RegistryObject<MenuType<MassGeneratorGUIMenu>> MassGeneratorGUI = REGISTRY.register("mass_generator_gui", () -> IForgeMenuType.create(MassGeneratorGUIMenu::new));
	public static final RegistryObject<MenuType<ElectricalTrusterGUIMenu>> ElectricalTrusterGUI = REGISTRY.register("electrical_truster_gui", () -> IForgeMenuType.create(ElectricalTrusterGUIMenu::new));
	public static final RegistryObject<MenuType<OrbitalProjectorGUIMenu>> OrbitalProjectorGUI = REGISTRY.register("orbital_projector_gui", () -> IForgeMenuType.create(OrbitalProjectorGUIMenu::new));
}
