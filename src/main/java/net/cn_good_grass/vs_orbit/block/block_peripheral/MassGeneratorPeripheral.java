//package net.cn_good_grass.vs_orbit.block.block_peripheral;
//
//import dan200.computercraft.api.peripheral.IPeripheral;
//import dan200.computercraft.api.lua.LuaFunction;
//import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
//import net.minecraft.world.level.Level;
//
//import javax.annotation.Nullable;
//
//public class MassGeneratorPeripheral implements IPeripheral {
//    private final MassGeneratorBlockEntity generator;
//
//    public MassGeneratorPeripheral(MassGeneratorBlockEntity generator) { this.generator = generator; }
//
//    @Override public String getType() { return "mass_generator"; }
//
//    @Override
//    public boolean equals(@Nullable IPeripheral other) {
//        if (this == other) return true;
//        if (!(other instanceof MassGeneratorPeripheral)) return false;
//        return generator == ((MassGeneratorPeripheral) other).generator;
//    }
//
//    @LuaFunction(mainThread = true) public final double getMass() { return generator.mass; }
//
//    @LuaFunction(mainThread = true)
//    public final void setMass(double newMass) {
//        generator.mass = newMass;
//        generator.setChanged();
//        if (generator.getLevel() != null) generator.getLevel().sendBlockUpdated(generator.getBlockPos(), generator.getBlockState(), generator.getBlockState(), 3);
//    }
//
//    @LuaFunction(mainThread = true) public final void addMass(double amount) { setMass(getMass() + amount); }
//}
//
