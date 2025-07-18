
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.cn_good_grass.vs_orbit.gui;

import net.cn_good_grass.vs_orbit.cilent.render.gui.electrical_truster.ElectromagneticTractorGUIScreen;
import net.cn_good_grass.vs_orbit.cilent.render.gui.jump_engine_controller.JumpEngineControllerGUIScreen;
import net.cn_good_grass.vs_orbit.cilent.render.gui.mass_generator.MassGeneratorGUIScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VSOrbitModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(VSOrbitModMenus.JumpEngineControllerGUI.get(), JumpEngineControllerGUIScreen::new);
			MenuScreens.register(VSOrbitModMenus.MassGeneratorGUI.get(), MassGeneratorGUIScreen::new);
			MenuScreens.register(VSOrbitModMenus.ElectromagneticTractorGUI.get(), ElectromagneticTractorGUIScreen::new);
		});
	}
}
