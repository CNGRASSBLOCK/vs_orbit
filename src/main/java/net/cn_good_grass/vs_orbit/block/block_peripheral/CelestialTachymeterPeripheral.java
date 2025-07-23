package net.cn_good_grass.vs_orbit.block.block_peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CelestialTachymeterPeripheral implements IPeripheral {
    private final CelestialTachymeterBlockEntity blockEntity;
    private final long peripheralId;
    private final AttachedComputerSet computers = new AttachedComputerSet();

    public CelestialTachymeterPeripheral(CelestialTachymeterBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.peripheralId = blockEntity.getBlockPos().asLong();
    }

    @Override public String getType() { return "celestial_tachymeter"; }

    @Override public boolean equals(@Nullable IPeripheral iPeripheral) { return false; }

    @LuaFunction public String getSpeedType() { return ""; }
    @LuaFunction public double getSpeed() { return 0; }
    @LuaFunction public double getXSpeed() { return 0; }
    @LuaFunction public double getYSpeed() { return 0; }
    @LuaFunction public double getZSpeed() { return 0; }
}

