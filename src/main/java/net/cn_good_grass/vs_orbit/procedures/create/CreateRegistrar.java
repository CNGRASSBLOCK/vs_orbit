package net.cn_good_grass.vs_orbit.procedures.create;

import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.procedures.create.DisplaySource.CelestialTachymeterDisplaySource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public class CreateRegistrar {
    public static void register() {
        if (!ModList.get().isLoaded("create")) return;

        registerDisplaySource(
                VSOrbitModBlocks.celestial_tachymeter.get(),
                new CelestialTachymeterDisplaySource(),
                "celestial_tachymeter_display"
        );
    }

    public static void registerDisplaySource(Block block, DisplaySource source, String id) {
//        try {
//            Class<?> registryClass = Class.forName("com.simibubi.create.content.redstone.displayLink.DisplayLinkManager");
//            Method registerMethod = registryClass.getMethod("registerDisplaySource", Block.class, DisplaySource.class, ResourceLocation.class);
//
//            ResourceLocation rl = new ResourceLocation("vs_orbit", id);
//            registerMethod.invoke(null, block, source, rl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
