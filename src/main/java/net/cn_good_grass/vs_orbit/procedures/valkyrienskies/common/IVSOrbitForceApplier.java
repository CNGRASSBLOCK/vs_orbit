package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.common;

import net.minecraft.core.BlockPos;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public interface IVSOrbitForceApplier {
    void applyForces(BlockPos pos, PhysShipImpl physShip);
}
