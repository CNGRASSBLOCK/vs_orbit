package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Force;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ForcerInducedShips implements ShipForcesInducer {
	private final List<ForceApplier> appliers = new ArrayList<>();
	private final List<ForceApplier> remove = new ArrayList<>();

	@Override
	public void applyForces(@NotNull PhysShip physicShip) {
		remove.clear();
		appliers.forEach((applier) -> {
			applier.applyForces((PhysShipImpl) physicShip);
			applier.time -= 0.05 / VSCoreConfig.SERVER.getPt().getPhysicsTicksPerGameTick();
			if (applier.time < 0) remove.add(applier);
		});
		appliers.removeAll(remove);
	}

	public void addForce(Force force) {
		ForceApplier forceApplier = new ForceApplier(force.name, force.x, force.y, force.z, force.time);
		if (!appliers.contains(forceApplier)) appliers.add(forceApplier);
	}

	public void removeForce(Force force) { appliers.remove(new ForceApplier(force.name, force.x, force.y, force.z, force.time)); }

	public List<ForceApplier> getForceList() { return new ArrayList<>(appliers); }

	// ----- Force induced ships ----- //

	@Nullable
	public static ForcerInducedShips getFromShip(Ship ship) {
		Vector3dc pos = ship.getTransform().getPositionInShip();
		MinecraftServer server = ValkyrienSkiesMod.getCurrentServer();
		if (server == null) return null;
		ServerLevel serverLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(ship.getChunkClaimDimension().replace("minecraft:dimension:", ""))));
		LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, BlockPos.containing(pos.x(), pos.y(), pos.z()));
		if (serverShip == null) return null;

		ForcerInducedShips attachment = serverShip.getAttachment(ForcerInducedShips.class);
		if (attachment == null) {
			attachment = new ForcerInducedShips();
			serverShip.saveAttachment(ForcerInducedShips.class, attachment);
		}
		return attachment;
	}

    public List<ForceApplier> getRemove() {
        return remove;
    }
}
