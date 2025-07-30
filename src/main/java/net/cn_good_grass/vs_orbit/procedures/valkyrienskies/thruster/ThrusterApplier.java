package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster;

import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class ThrusterApplier {
    private final ThrusterData data;

    public ThrusterData getData() {
        return this.data;
    }

    public ThrusterApplier(ThrusterData data){
        this.data = data;
    }

    public void applyForces(BlockPos pos, PhysShipImpl physShip) {
        double throttle = data.throttle;
        if (throttle == 0) return;

        ShipTransform transform = physShip.getTransform();
        Vector3d tForce = transform.getShipToWorld().transformDirection(data.dir.div(transform.getShipToWorldScaling(), new Vector3d()));
        tForce.mul(throttle);

        Vector3dc linearVelocity = physShip.getPoseVel().getVel();

        if (tForce.dot(linearVelocity) > 0) {
            if (data.mode == ThrusterData.ThrusterMode.GLOBAL) {
                applyScaledForce(physShip, linearVelocity, tForce);
            } else {
                Vector3d tPos = VectorConversionsMCKt.toJOMLD(pos).add(0.5, 0.5, 0.5, new Vector3d()).sub(transform.getPositionInShip());
                Vector3d parallel = new Vector3d(tPos).mul(tForce.dot(tPos) / tForce.dot(tForce));
                Vector3d perpendicular = new Vector3d(tForce).sub(parallel);
                physShip.applyInvariantForceToPos(perpendicular, tPos);
                applyScaledForce(physShip, linearVelocity, parallel);
            }
            return;
        }

        if (data.mode == ThrusterData.ThrusterMode.POSITION) {
            Vector3d tPos = VectorConversionsMCKt.toJOMLD(pos).add(0.5, 0.5, 0.5, new Vector3d()).sub(transform.getPositionInShip());
            physShip.applyInvariantForceToPos(tForce, tPos);
        } else {
            physShip.applyInvariantForce(tForce);
        }
    }

    private static void applyScaledForce(PhysShipImpl physShip, Vector3dc linearVelocity, Vector3d tForce) {
        assert ValkyrienSkiesMod.getCurrentServer() != null;
        double deltaTime = 1.0 / (VSGameUtilsKt.getVsPipeline(ValkyrienSkiesMod.getCurrentServer()).computePhysTps());
        double mass = physShip.getInertia().getShipMass();

        Vector3d targetVelocity = (new Vector3d(linearVelocity).add(new Vector3d(tForce).mul(deltaTime / mass))).sub(linearVelocity);

        physShip.applyInvariantForce(targetVelocity.mul(mass / deltaTime));
    }
}
