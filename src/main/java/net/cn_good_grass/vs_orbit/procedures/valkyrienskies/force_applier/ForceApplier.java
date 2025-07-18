package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier;

import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.common.IVSOrbitForceApplier;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

public class ForceApplier implements IVSOrbitForceApplier {
    private final Vector3dc force;

    public ForceApplier(Vector3d vector3d) { this.force = vector3d; }

    @Override public void applyForces(BlockPos pos, PhysShipImpl physShip) {
        physShip.applyInvariantForce(force);
    }
}
