package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier.ForcerInducedShips;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.core.AstronomicalThread;
import net.jcm.vsch.event.AtmosphericCollision;
import net.lointain.cosmos.CosmosMod;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

@Mixin(AtmosphericCollision.class)
public class SpaceJoinShip {
    @Redirect(method = {"atmosphericCollisionTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getDouble(Ljava/lang/String;)D"))
    private static double SpaceJoinShip(CompoundTag atmospheric_data, String key, @Local(argsOnly = true) ServerLevel level) {
        if (!key.contains("origin")) return atmospheric_data.getDouble(key);

        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(level);
        Tag opaque_object_map = worldVars.opaque_object_map.get(atmospheric_data.getString("travel_to")); // 星球数据
        if (opaque_object_map == null) { return atmospheric_data.getDouble(key); }
        ListTag listtag = (ListTag) opaque_object_map;
        String WorldId = level.dimension().location().toString();
        CompoundTag obj = null;
        for (Tag tag : listtag) if (tag instanceof CompoundTag compoundTag && compoundTag.contains("travel_to")) if (compoundTag.getString("travel_to").equals(WorldId)) obj = compoundTag;
        if (obj == null) return atmospheric_data.getDouble(key);
        Vec3 newPos = StarAPI.getPos(atmospheric_data.getString("travel_to"), 1, obj, true);

        if (key.contains("x")) return newPos.x; else if (key.contains("y")) return newPos.y + (obj.getDouble("scale") / 2) + ((obj.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128; else if (key.contains("z")) return newPos.z;

        return atmospheric_data.getDouble(key);
    }

    @Inject(method = "atmosphericCollisionTick", at = @At(value = "INVOKE", target = "Lnet/jcm/vsch/util/TeleportationHandler;handleTeleport(Lorg/valkyrienskies/core/api/ships/Ship;Lorg/joml/Vector3d;)V", shift = At.Shift.AFTER), remap = false)
    private static void afterHandleTeleport(ServerLevel level, CallbackInfo ci, @Local Ship ship, @Local(ordinal = 1) CompoundTag atmoData) {
        CosmosMod.queueServerWork(80, () -> {
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

            if (VSOrbitModConfig.ValkyrienSkies_SYNC_MODE.get()) {
                Astronomical ShipAstronomical = astronomicalPool.getAstronomical("VSShip-" + ship.getId());
                if (ShipAstronomical == null) return;

                ShipAstronomical.x_speed = speed_in_xyz.x;
                ShipAstronomical.y_speed = speed_in_xyz.y;
                ShipAstronomical.z_speed = speed_in_xyz.z;
            } else {
                ForcerInducedShips forcerInducedShips = ForcerInducedShips.getFromShip(ship);
                if (forcerInducedShips == null) return;

                if (!(ship instanceof ServerShip serverShip)) return;
                Vector3d force = speed_in_xyz.mul(serverShip.getInertiaData().getMass()).mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get());

                forcerInducedShips.addForce(new Force("get_into_orbit", force.x, force.y, force.z, 1));
            }
        });
    }
}