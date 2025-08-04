package net.cn_good_grass.vs_orbit.procedures.create;

import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.block.blocks.CelestialTachymeterBlock;
import net.cn_good_grass.vs_orbit.procedures.CompatMods;
import net.cn_good_grass.vs_orbit.procedures.create.DisplaySource.CelestialTachymeterDisplaySource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public class CreateRegistrar {
    public static void register() {
        if (CompatMods.COMPUTERCRAFT.isLoaded()) {
            AllDisplayBehaviours.assignDataBehaviour(new CelestialTachymeterDisplaySource(), "celestial_tachymeter_display_source").accept(VSOrbitModBlocks.celestial_tachymeter.get());
        }
    }
}
