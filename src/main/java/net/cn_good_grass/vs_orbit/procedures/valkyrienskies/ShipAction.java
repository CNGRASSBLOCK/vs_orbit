package net.cn_good_grass.vs_orbit.procedures.valkyrienskies;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.jcm.vsch.util.VSCHUtils;
import net.lointain.cosmos.CosmosMod;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.VSEntityHandler;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.*;

public class ShipAction {
    public static void AddForce(ServerLevel level, Ship ship, Vector3d force) {
        if (!(ship instanceof ServerShip serverShip)) return;

        LoadedServerShip loadedServerShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(ship.getId());
        if (loadedServerShip == null) return;

        GameTickForceApplier applier = loadedServerShip.getAttachment(GameTickForceApplier.class);
        if (applier == null) return;
        applier.applyInvariantForce(new Vector3d(force.x / serverShip.getInertiaData().getMass(), force.y / serverShip.getInertiaData().getMass(), force.z / serverShip.getInertiaData().getMass())); //施加力
    }

    public static void teleportShip(ServerShipWorldCore serverShipWorldCore, ServerShip serverShip, ShipTeleportDataImpl shipTeleportData) {
        if (serverShip == null) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        String OldWorldID = serverShip.getChunkClaimDimension().split(":")[2] + ":" + serverShip.getChunkClaimDimension().split(":")[3];
        String NewWorldID = null;
        if (shipTeleportData.getNewDimension() != null) NewWorldID = shipTeleportData.getNewDimension().split(":")[2] + ":" + shipTeleportData.getNewDimension().split(":")[3];
        if (NewWorldID == null) return;

        ServerLevel OldWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(OldWorldID)));
        ServerLevel NewWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(NewWorldID)));
        if (OldWorld == null || NewWorld == null) return;

        List<Entity> Entities = new ArrayList<>();
        HashMap<UUID, Vec3> EntitiesToPos = new HashMap<>();
        HashMap<UUID, GameType> PlayerGameMode = new HashMap<>();
        if (serverShip.getShipAABB() == null) return;
        Vec3 OldPos = new Vec3(serverShip.getTransform().getPositionInWorld().x(), serverShip.getTransform().getPositionInWorld().y(), serverShip.getTransform().getPositionInWorld().z());
        Vec3 NewPos = new Vec3(shipTeleportData.getNewPos().x(), shipTeleportData.getNewPos().y(), shipTeleportData.getNewPos().z());
        for (Entity entity : OldWorld.getEntities(null, VectorConversionsMCKt.toMinecraft(serverShip.getWorldAABB()).inflate(10))) {
            if (VSEntityManager.INSTANCE.getHandler(entity).getClass() == WorldEntityHandler.class) {
                Entities.add(entity);
                EntitiesToPos.put(entity.getUUID(), entity.position().add(NewPos.subtract(OldPos)));
                if (entity instanceof ServerPlayer player) {
                    PlayerGameMode.put(entity.getUUID(), player.gameMode.getGameModeForPlayer());
                    player.setGameMode(GameType.SPECTATOR);
                }
            }
        }

        OldWorld.setChunkForced((int) serverShip.getTransform().getPositionInWorld().x(), (int) serverShip.getTransform().getPositionInWorld().z(), true);
        NewWorld.setChunkForced((int) shipTeleportData.getNewPos().x(), (int) shipTeleportData.getNewPos().z(), true);
        serverShip.setStatic(true);

        CosmosMod.queueServerWork(1, () -> serverShipWorldCore.teleportShip(serverShip, shipTeleportData));

        CosmosMod.queueServerWork(2, () -> {
            for (Entity entity : Entities) {
                entity.teleportTo(NewWorld, EntitiesToPos.get(entity.getUUID()).x, EntitiesToPos.get(entity.getUUID()).y, EntitiesToPos.get(entity.getUUID()).z, Set.of(RelativeMovement.Y_ROT, RelativeMovement.X_ROT), entity.getYRot(), entity.getXRot());
                if (entity instanceof ServerPlayer player) player.setGameMode(PlayerGameMode.get(entity.getUUID()));
            }
        });

        CosmosMod.queueServerWork(3, () -> {
            serverShip.setStatic(false);
            OldWorld.setChunkForced((int) serverShip.getTransform().getPositionInWorld().x(), (int) serverShip.getTransform().getPositionInWorld().z(), false);
            NewWorld.setChunkForced((int) shipTeleportData.getNewPos().x(), (int) shipTeleportData.getNewPos().z(), false);
        });
    }
}
