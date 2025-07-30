package net.cn_good_grass.vs_orbit.procedures.create.DisplaySource;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.Components;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class CelestialTachymeterDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity)) return EMPTY;

        int value = 1;
        return List.of(Components.literal("进度: " + value + "%"));
    }

    @Override public Component getName() { return Components.literal("我的显示来源"); }

    @Override public int getPassiveRefreshTicks() { return 1; }
}
