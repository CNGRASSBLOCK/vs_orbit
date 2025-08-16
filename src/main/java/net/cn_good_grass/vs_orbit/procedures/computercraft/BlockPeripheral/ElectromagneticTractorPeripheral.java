package net.cn_good_grass.vs_orbit.procedures.computercraft.BlockPeripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.cn_good_grass.vs_orbit.block.block_entities.ElectricalTrusterBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ElectromagneticTractorPeripheral implements IPeripheral {
    private final ElectricalTrusterBlockEntity blockEntity;
    private final long peripheralId;
    private final AttachedComputerSet computers = new AttachedComputerSet();

    public ElectromagneticTractorPeripheral(ElectricalTrusterBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.peripheralId = blockEntity.getBlockPos().asLong();
    }

    @Override public String getType() { return "electrical_truster"; }

    @Override public boolean equals(@Nullable IPeripheral iPeripheral) { return false; }

    @LuaFunction public double getForce() { return blockEntity.force; }
    @LuaFunction public MethodResult setForce(double force) {
        blockEntity.force = force ;
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }
}

