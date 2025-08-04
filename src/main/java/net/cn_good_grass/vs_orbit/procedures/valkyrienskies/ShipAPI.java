package net.cn_good_grass.vs_orbit.procedures.valkyrienskies;

import net.lointain.cosmos.CosmosMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.*;

public class ShipAPI {
    public static Vector3d getWorldPosFromShipPos(ServerLevel level, Vector3d pos) {
        Matrix4d matrix4d;
        VSGameUtilsKt.getShipManagingPos(level, pos);
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return pos; else matrix4d = (Matrix4d) ship.getShipToWorld();

        return matrix4d.transformPosition(pos);
    }
}
