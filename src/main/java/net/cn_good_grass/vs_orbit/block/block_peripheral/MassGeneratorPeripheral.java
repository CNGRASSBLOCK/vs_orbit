package net.cn_good_grass.vs_orbit.block.block_peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MassGeneratorPeripheral implements IPeripheral {
    private final MassGeneratorBlockEntity blockEntity;
    private final long peripheralId;
    private final AttachedComputerSet computers = new AttachedComputerSet();

    public MassGeneratorPeripheral(MassGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.peripheralId = blockEntity.getBlockPos().asLong();
    }

    @Override public String getType() { return "mass_generator"; }

    @Override public boolean equals(@Nullable IPeripheral iPeripheral) { return false; }

    @LuaFunction public double getMass() { return blockEntity.mass; }
    @LuaFunction public MethodResult setMass(double mass) {
        blockEntity.mass = mass ;
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }
}

