package net.cn_good_grass.vs_orbit.procedures.create.display_source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;
import org.valkyrienskies.core.impl.config.VSCoreConfig;

import java.util.List;

public class CelestialTachymeterDisplaySource extends DisplaySource {
    @Override public List<MutableComponent> provideText(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) { return List.of(); }

    @Override public int getPassiveRefreshTicks() { return 1; }
}
