package net.cn_good_grass.vs_orbit.procedures.computercraft.BlockPeripheral;

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

    @LuaFunction public String getSpeedTarget() { return blockEntity.target; }
    @LuaFunction public double getSpeed() { return blockEntity.speed.length(); }
    @LuaFunction public double getXSpeed() { return blockEntity.speed.x(); }
    @LuaFunction public double getYSpeed() { return blockEntity.speed.y(); }
    @LuaFunction public double getZSpeed() { return blockEntity.speed.z(); }
}

