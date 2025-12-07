package net.cn_good_grass.vs_orbit.procedures.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.procedures.create.DisplaySource.CelestialTachymeterDisplaySource;

public class CreateRegistrar {
    public static void register() {
        // Register display source for celestial tachymeter block
        DisplaySource.BY_BLOCK.add(VSOrbitModBlocks.celestial_tachymeter.get(), new CelestialTachymeterDisplaySource());
    }
}
