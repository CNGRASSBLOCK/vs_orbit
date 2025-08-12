package net.cn_good_grass.vs_orbit.procedures.mekanism;

import net.cn_good_grass.vs_orbit.procedures.CompatMods;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
    public final int maxEnergy;
    public int storedEnergy;

    public EnergyStorage(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int needs = this.maxEnergy - this.storedEnergy;
        if (needs < maxReceive) maxReceive = needs;
        if (!simulate) this.storedEnergy += maxReceive;
        return maxReceive;
    }

    public boolean useEnergy(int needs) {
        needs = Math.abs(needs);
        if (!CompatMods.MEKANISM.isLoaded()) return true;
        if (this.storedEnergy < needs) return false;
        this.storedEnergy -= needs;
        return true;
    }

    @Override public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }
    @Override public int getEnergyStored() {
        return this.storedEnergy;
    }
    @Override public int getMaxEnergyStored() {
        return this.maxEnergy;
    }
    @Override public boolean canExtract() {
        return false;
    }
    @Override public boolean canReceive() {
        return true;
    }
}
