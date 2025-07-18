package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.common;

import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier.ForceApplier;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster.ThrusterData;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster.ThrusterForceApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class VSOrbitForceInducedShips implements ShipForcesInducer {
	public Map<BlockPos, IVSOrbitForceApplier> appliers = new ConcurrentHashMap<>();

	@Override
	public void applyForces(@NotNull PhysShip physicShip) {
		appliers.forEach((pos,applier) -> applier.applyForces(pos, (PhysShipImpl) physicShip));
	}

	// ----- Force Appliers ----- //

	public void addApplier(BlockPos pos, IVSOrbitForceApplier applier) { 
		appliers.put(pos, applier);
	}

	public void removeApplier(BlockPos pos){ appliers.remove(pos); }

	@Nullable public IVSOrbitForceApplier getApplierAtPos(BlockPos pos){ return appliers.get(pos); }

	// ----- Thrusters ----- //

	public void addThruster(BlockPos pos, ThrusterData data) { addApplier(pos, new ThrusterForceApplier(data)); }

	public void removeThruster(BlockPos pos) { if (getThrusterAtPos(pos) != null) removeApplier(pos); }

	@Nullable
	public ThrusterData getThrusterAtPos(BlockPos pos) {
		IVSOrbitForceApplier applier = getApplierAtPos(pos);
		if (applier instanceof ThrusterForceApplier thruster) return thruster.getData(); else return null;
	}

	// ----- ForceApplier ----- //

	public void addForce(BlockPos pos, Vector3d vector3d) {
		if ((Double.isInfinite(vector3d.x) || Double.isNaN(vector3d.x)) || (Double.isInfinite(vector3d.y) || Double.isNaN(vector3d.y)) || (Double.isInfinite(vector3d.z) || Double.isNaN(vector3d.z))) return;
		addApplier(pos, new ForceApplier(vector3d));
	}

	public void removeForce(BlockPos pos) {
		removeApplier(pos);
	}

	// ----- Force induced ships ----- //

	public static VSOrbitForceInducedShips getOrCreate(ServerShip ship) {
		VSOrbitForceInducedShips attachment = ship.getAttachment(VSOrbitForceInducedShips.class);
		if (attachment == null) {
			attachment = new VSOrbitForceInducedShips();
			ship.saveAttachment(VSOrbitForceInducedShips.class, attachment);
		}
		return attachment;
	}

	public static VSOrbitForceInducedShips get(Level level, BlockPos pos) {
		ServerLevel serverLevel = (ServerLevel) level;
		ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
		if (ship == null) ship = VSGameUtilsKt.getShipManagingPos(serverLevel, pos);
		return ship != null ? getOrCreate(ship) : null;
	}
}
