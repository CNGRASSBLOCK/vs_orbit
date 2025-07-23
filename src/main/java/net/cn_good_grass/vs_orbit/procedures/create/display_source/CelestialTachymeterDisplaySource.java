package net.cn_good_grass.vs_orbit.procedures.create.display_source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.valkyrienskies.core.impl.config.VSCoreConfig;

import java.util.ArrayList;
import java.util.List;

public class CelestialTachymeterDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        List<MutableComponent> text = new ArrayList<>();

        if (context.getSourceBlockEntity() instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity) {
            text.add(Component.literal("Value: " + celestialTachymeterBlockEntity.speed.toString()));
        }

        return text;
    }

    @Override public int getPassiveRefreshTicks() { return 1; }
}
