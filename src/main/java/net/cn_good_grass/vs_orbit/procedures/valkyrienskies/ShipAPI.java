package net.cn_good_grass.vs_orbit.procedures.valkyrienskies;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.lointain.cosmos.CosmosMod;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.IPlayer;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.*;

public abstract class ShipAPI {
    public static Vector3d getWorldPosFromShipPos(ServerLevel level, Vector3d pos) {
        Matrix4d matrix4d;
        VSGameUtilsKt.getShipManagingPos(level, pos);
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return pos; else matrix4d = (Matrix4d) ship.getShipToWorld();

        return matrix4d.transformPosition(pos);
    }

    public static void teleportShipNoLag(ServerShipWorldCore serverShipWorldCore, ServerShip serverShip, ShipTeleportDataImpl shipTeleportData) {
        if (Double.isNaN(shipTeleportData.getNewPos().length()) || Double.isInfinite(shipTeleportData.getNewPos().length())) return;
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

        VSOrbitMod.queueServerWork(1, () -> serverShipWorldCore.teleportShip(serverShip, shipTeleportData));

        VSOrbitMod.queueServerWork(2, () -> {
            for (Entity entity : Entities) {
                entity.teleportTo(NewWorld, EntitiesToPos.get(entity.getUUID()).x, EntitiesToPos.get(entity.getUUID()).y, EntitiesToPos.get(entity.getUUID()).z, Set.of(RelativeMovement.Y_ROT, RelativeMovement.X_ROT), entity.getYRot(), entity.getXRot());
                if (entity instanceof ServerPlayer player) player.setGameMode(PlayerGameMode.get(entity.getUUID()));
            }
        });

        VSOrbitMod.queueServerWork(3, () -> {
            serverShip.setStatic(false);
            OldWorld.setChunkForced((int) serverShip.getTransform().getPositionInWorld().x(), (int) serverShip.getTransform().getPositionInWorld().z(), false);
            NewWorld.setChunkForced((int) shipTeleportData.getNewPos().x(), (int) shipTeleportData.getNewPos().z(), false);
        });
    }

    public static void teleportShipMultibody(ServerShipWorldCore serverShipWorldCore, ServerShip serverShip, ShipTeleportDataImpl shipTeleportData) {
        List<LoadedServerShip> world_ship = new ArrayList<>(serverShipWorldCore.getLoadedShips().stream().toList());

        List<LoadedServerShip> ships = new ArrayList<>();
        Map<LoadedServerShip, Vector3d> ship_pos = new HashMap<>();
        if (!(serverShip instanceof LoadedServerShip main_ship)) return;
        ships.add(main_ship);

        boolean add = false;
        for (LoadedServerShip this_ship : new ArrayList<>(ships)) {
            AABB aabb = VectorConversionsMCKt.toMinecraft(this_ship.getWorldAABB());
            AABB this_aabb = new AABB(aabb.minX - 1, aabb.minY - 1, aabb.minZ - 1, aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1);
            for (LoadedServerShip other_ship : new ArrayList<>(world_ship)) {
                if (this_ship.getId() == other_ship.getId()) continue;
                AABB other_aabb = VectorConversionsMCKt.toMinecraft(other_ship.getWorldAABB());
                if (this_aabb.intersects(other_aabb)) {
                    ships.add(other_ship);
                    Vector3d pos = new Vector3d(other_ship.getTransform().getPositionInWorld()).sub(main_ship.getWorldAABB().center(new Vector3d()));
                    ship_pos.put(other_ship, new Vector3d(shipTeleportData.getNewRot().transform(pos)));
                    world_ship.remove(other_ship);
                    add = true;
                }
            }
            if (!add) break;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        String OldWorldID = serverShip.getChunkClaimDimension().split(":")[2] + ":" + serverShip.getChunkClaimDimension().split(":")[3];
        String NewWorldID = null;
        if (shipTeleportData.getNewDimension() != null) NewWorldID = shipTeleportData.getNewDimension().split(":")[2] + ":" + shipTeleportData.getNewDimension().split(":")[3];
        if (NewWorldID == null) return;
        ServerLevel OldWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(OldWorldID)));
        ServerLevel NewWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(NewWorldID)));
        if (OldWorld == null || NewWorld == null) return;

        for (LoadedServerShip this_ship : ships) {
            AABB aabb = VectorConversionsMCKt.toMinecraft(this_ship.getWorldAABB());
            for (Entity entity : OldWorld.getEntities(null, aabb)) {
                Vector3d Pos = new Vector3d(entity.position().x(), entity.position().y(), entity.position().z()).sub(main_ship.getWorldAABB().center(new Vector3d())).add(shipTeleportData.getNewPos());
                entity.teleportTo(NewWorld, Pos.x(), Pos.y(), Pos.z(), Set.of(RelativeMovement.Y_ROT, RelativeMovement.X_ROT), entity.getYRot(), entity.getXRot());
            }
        }

        for (LoadedServerShip this_ship : ships) {
            if (this_ship.getId() == main_ship.getId()) {
                serverShipWorldCore.teleportShip(this_ship, new ShipTeleportDataImpl(shipTeleportData.getNewPos(), shipTeleportData.getNewRot(), shipTeleportData.getNewVel(), this_ship.getOmega(), shipTeleportData.getNewDimension(), shipTeleportData.getNewScale()));
                continue;
            }
            serverShipWorldCore.teleportShip(this_ship, new ShipTeleportDataImpl(ship_pos.get(this_ship).add(shipTeleportData.getNewPos()), shipTeleportData.getNewRot(), shipTeleportData.getNewVel(), this_ship.getOmega(), shipTeleportData.getNewDimension(), shipTeleportData.getNewScale()));
        }
    }
}
