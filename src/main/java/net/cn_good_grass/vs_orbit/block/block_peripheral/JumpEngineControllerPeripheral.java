package net.cn_good_grass.vs_orbit.block.block_peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.block.blocks.JumpEngineControllerBlock;
import org.jetbrains.annotations.Nullable;

public class JumpEngineControllerPeripheral implements IPeripheral {
    private final JumpEngineControllerBlockEntity blockEntity;
    private final long peripheralId;
    private final AttachedComputerSet computers = new AttachedComputerSet();

    public JumpEngineControllerPeripheral(JumpEngineControllerBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.peripheralId = blockEntity.getBlockPos().asLong();
    }

    @Override public String getType() { return "jump_engine_controller"; }

    @Override public boolean equals(@Nullable IPeripheral iPeripheral) { return false; }

    @LuaFunction public String getMode() { return blockEntity.mode.toString(); }
    @LuaFunction public MethodResult setMode(String mode) {
        blockEntity.mode = JumpEngineControllerBlock.Mode.valueOf(mode);
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }

    @LuaFunction public double[] getJumpPos() { return new double[]{blockEntity.setting.getDouble("pos_x"), blockEntity.setting.getDouble("pos_y"), blockEntity.setting.getDouble("pos_z")}; }
    @LuaFunction public String getJumpWorld() { return blockEntity.setting.getString("pos_world"); }
    @LuaFunction public MethodResult setJumpPos(int x, int y, int z) {
        blockEntity.setting.putDouble("pos_x", x);
        blockEntity.setting.putDouble("pos_y", y);
        blockEntity.setting.putDouble("pos_z", z);
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }
    @LuaFunction public MethodResult setJumpWorld(String WorldID) {
        blockEntity.setting.putString("pos_world", WorldID);
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }

    @LuaFunction public double getPowerForce() { return blockEntity.setting.getDouble("force"); }
    @LuaFunction public MethodResult setPowerForce(double force) {
        blockEntity.setting.putDouble("force", force);
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }

    @LuaFunction public int getRedStonePower() { return blockEntity.red_stone_power; }
    @LuaFunction public MethodResult setRedStonePower(int power) {
        if (power > 15) power = 15; else if (power < 0) power = 0;
        blockEntity.red_stone_power = power;
        blockEntity.sendToClient();
        return MethodResult.of(true);
    }
}

