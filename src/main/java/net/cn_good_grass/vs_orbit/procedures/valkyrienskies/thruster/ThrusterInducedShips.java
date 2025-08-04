package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class ThrusterInducedShips implements ShipForcesInducer {
	public Map<BlockPos, ThrusterApplier> appliers = new ConcurrentHashMap<>();

	@Override
	public void applyForces(@NotNull PhysShip physicShip) {
		appliers.forEach((pos,applier) -> applier.applyForces(pos, (PhysShipImpl) physicShip));
	}

	// ----- Force Appliers ----- //

	public void addApplier(BlockPos pos, ThrusterApplier applier) { appliers.put(pos, applier); }

	public void removeApplier(BlockPos pos){ appliers.remove(pos); }

	@Nullable public ThrusterApplier getApplierAtPos(BlockPos pos){ return appliers.get(pos); }

	// ----- Thrusters ----- //

	public void addThruster(BlockPos pos, ThrusterData data) { addApplier(pos, new ThrusterApplier(data)); }

	public void removeThruster(BlockPos pos) { if (getThrusterAtPos(pos) != null) removeApplier(pos); }

	@Nullable
	public ThrusterData getThrusterAtPos(BlockPos pos) {
		ThrusterApplier applier = getApplierAtPos(pos);
		if (applier != null) return applier.getData(); else return null;
	}
	// ----- Force induced ships ----- //

	public static ThrusterInducedShips getOrCreate(ServerShip ship) {
		ThrusterInducedShips attachment = ship.getAttachment(ThrusterInducedShips.class);
		if (attachment == null) {
			attachment = new ThrusterInducedShips();
			ship.saveAttachment(ThrusterInducedShips.class, attachment);
		}
		return attachment;
	}

	public static ThrusterInducedShips get(Level level, BlockPos pos) {
		ServerLevel serverLevel = (ServerLevel) level;
		ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
		if (ship == null) ship = VSGameUtilsKt.getShipManagingPos(serverLevel, pos);
		return ship != null ? getOrCreate(ship) : null;
	}
}
