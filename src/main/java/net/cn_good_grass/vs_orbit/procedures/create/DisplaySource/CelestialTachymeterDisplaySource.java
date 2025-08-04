package net.cn_good_grass.vs_orbit.procedures.create.DisplaySource;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Components;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.simibubi.create.content.redstone.displayLink.source.BoilerDisplaySource.notEnoughSpaceDouble;

public class CelestialTachymeterDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity)) return EMPTY;
        if (stats.maxRows() < 3) return notEnoughSpaceDouble;
        if (stats.maxColumns() < 25) return notEnoughSpaceDouble;

        List<MutableComponent> data = new ArrayList<>();
        data.add(Components.translatable("create.vs_orbit.display_source.celestial_tachymeter.target").append(celestialTachymeterBlockEntity.target));
        data.add(Components.translatable("create.vs_orbit.display_source.celestial_tachymeter.speed").append(String.valueOf(Math.round(celestialTachymeterBlockEntity.speed.length() * 1000.0) / 1000.0)));
        data.add(Components.translatable("create.vs_orbit.display_source.celestial_tachymeter.speed_xyz").append(Math.round(celestialTachymeterBlockEntity.speed.x * 10.0) / 10.0 + " " + Math.round(celestialTachymeterBlockEntity.speed.y * 10.0) / 10.0 + " " + Math.round(celestialTachymeterBlockEntity.speed.z * 10.0) / 10.0));

        return data;
    }

    @Override public Component getName() { return Components.translatable("block.vs_orbit.celestial_tachymeter"); }

    @Override public int getPassiveRefreshTicks() { return 40; }
}
