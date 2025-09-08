package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Force;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier.ForcerInducedShips;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core.AstronomicalThread;
import net.jcm.vsch.event.AtmosphericCollision;
import net.jcm.vsch.util.TeleportationHandler;
import net.lointain.cosmos.CosmosMod;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;
import java.util.List;

@Mixin(AtmosphericCollision.class)
public class SpaceJoinShip {
    @Redirect(method = {"atmosphericCollisionTick"}, at = @At(value = "INVOKE", target = "Lnet/jcm/vsch/util/TeleportationHandler;addShip(Lorg/valkyrienskies/core/api/ships/ServerShip;Lorg/joml/Vector3dc;Lorg/joml/Quaterniondc;)V"), remap = false)
    private static void TeleportShip(TeleportationHandler handler, ServerShip ship, Vector3dc pos, Quaterniondc rot, @Local(argsOnly = true) ServerLevel level, @Local(ordinal = 1) CompoundTag atmospheric_data) {
        if (!atmospheric_data.contains("travel_to")) return;

        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(level);
        Tag opaque_object_map = worldVars.opaque_object_map.get(atmospheric_data.getString("travel_to")); // 星球数据
        if (opaque_object_map == null) return;
        ListTag listtag = (ListTag) opaque_object_map;
        String WorldId = level.dimension().location().toString();
        CompoundTag obj = null;
        for (Tag tag : listtag) if (tag instanceof CompoundTag compoundTag && compoundTag.contains("travel_to")) if (compoundTag.getString("travel_to").equals(WorldId)) obj = compoundTag;
        if (obj == null) return;

        Vec3 Pos = StarAPI.getPos(atmospheric_data.getString("travel_to"), 1, obj, true);
        Vector3d newPos = new Vector3d(Pos.x(), Pos.y() + (obj.getDouble("scale") / 2 + ((obj.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128), Pos.z());
        Vec3 Rotate = StarAPI.getRotate(atmospheric_data.getString("travel_to"), 1, obj, true);
        Quaterniond newRotate = new Quaterniond().rotateYXZ(Rotate.y, Rotate.x, Rotate.z);

        handler.addShip(ship, newPos, newRotate.mul(new Quaterniond().rotateLocalX(Math.toRadians(90))));
    }

    @Inject(method = "atmosphericCollisionTick", at = @At(value = "INVOKE", target = "Lnet/jcm/vsch/util/TeleportationHandler;finalizeTeleport()V", shift = At.Shift.AFTER), remap = false)
    private static void afterHandleTeleport(ServerLevel level, CallbackInfo ci, @Local(ordinal = 1) CompoundTag atmoData, @Local TeleportationHandler teleportationHandler) {
        for (LoadedServerShip ship : teleportationHandler.getPendingShips()) ship.setStatic(true);

        CosmosMod.queueServerWork(80, () -> {
            for (LoadedServerShip ship : teleportationHandler.getPendingShips()) {
                ship.setStatic(false);

                ServerLevel serverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(ship.getChunkClaimDimension().replace("minecraft:dimension:", ""))));
                if (serverLevel == null) return;
                AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(serverLevel.dimension().location().toString());
                if (astronomicalPool == null) return;

                Astronomical astronomical = StarAPI.getAstronomicalFormLevel(level);
                if (astronomical == null) return;

                CompoundTag starData = StarAPI.getStarDataFormLevel(level);
                if (starData == null) return;

                double speed = Math.sqrt(VSOrbitModConfig.Gravitation_GRAVITATIONAL_CONSTANT.get() * astronomical.mass / (starData.getDouble("scale") / 2 + ((starData.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128));

                Vector3d speed_in_xyz = new Vector3d(Math.random() * 2 - 1, 0, Math.random() * 2 - 1).normalize().mul(speed);
                speed_in_xyz.x += astronomical.x_speed;
                speed_in_xyz.y += astronomical.y_speed;
                speed_in_xyz.z += astronomical.z_speed;

                ForcerInducedShips forcerInducedShips = ForcerInducedShips.getFromShip(ship);
                if (forcerInducedShips == null) return;

                Vector3d force = speed_in_xyz.mul(ship.getInertiaData().getMass()).mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get());

                forcerInducedShips.addForce(new Force("get_into_orbit", force.x, force.y, force.z, 1));

                for (Entity entity : level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10))) if (entity instanceof ServerPlayer serverPlayer) serverPlayer.displayClientMessage(Component.literal("[VS_Orbit] [Game] " + Component.translatable("message.vs_orbit.game.orbit_synchronous").getString()), false);
            }
        });
    }
}