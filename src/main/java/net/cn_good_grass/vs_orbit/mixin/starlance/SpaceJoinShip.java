package net.cn_good_grass.vs_orbit.mixin.starlance;

import net.cn_good_grass.vs_orbit.config.VSOrbitModConfig;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.valkyrienskies.ShipAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.core.AstronomicalThread;
import net.jcm.vsch.event.AtmosphericCollision;
import net.jcm.vsch.ship.ShipLandingAttachment;
import net.jcm.vsch.util.VSCHUtils;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(AtmosphericCollision.class)
public class SpaceJoinShip {
    /**
     * @author 草方块
     * @reason null
     */
    @Overwrite(remap = false)
    public static void atmosphericCollisionTick(ServerLevel level) {
        final CosmosModVariables.WorldVariables worldVariables = CosmosModVariables.WorldVariables.get(level);
        final CompoundTag atmoDatas = worldVariables.atmospheric_collision_data_map;
        final CompoundTag atmoData = atmoDatas.getCompound(level.dimension().location().toString());
        if (atmoData.isEmpty()) return;

        Tag opaque_object_map = worldVariables.opaque_object_map.get(atmoData.getString("travel_to")); // 星球数据
        if (opaque_object_map == null) return;
        ListTag listtag = (ListTag) opaque_object_map;
        String WorldId = level.dimension().location().toString();
        CompoundTag obj = null;
        for (Tag tag : listtag) if (tag instanceof CompoundTag compoundTag && compoundTag.contains("travel_to")) if (compoundTag.getString("travel_to").equals(WorldId)) obj = compoundTag;
        if (obj == null) return;

        final double atmoHeight = atmoData.getDouble("atmosphere_y");
        final String targetDim = atmoData.getString("travel_to");
        final ServerLevel targetLevel = VSCHUtils.dimToLevel(targetDim);
        if (targetLevel == null) return;

        for (final LoadedServerShip ship : VSCHUtils.getLoadedShipsInLevel(level)) {
            final ShipLandingAttachment landingAttachment = ship.getAttachment(ShipLandingAttachment.class);
            if (ship.getTransform().getPositionInWorld().y() < atmoHeight) {
                if (landingAttachment != null) ship.saveAttachment(ShipLandingAttachment.class, null);
            } else if (landingAttachment == null || !landingAttachment.landing || ship.getTransform().getPositionInWorld().y() >= atmoHeight) {
                CosmosAstronomical astronomical = StarAPI.getAstronomical(atmoData.getString("travel_to"), 1, obj, true);
                if (astronomical == null) return;
                Vector3d newPos = new Vector3d(astronomical.x, astronomical.y + (obj.getDouble("scale") / 2 + ((obj.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128), astronomical.z);
                Quaterniond newRotate = new Quaterniond(astronomical.rotate);

                double speed = Math.sqrt(VSOrbitModConfig.Gravitation_GRAVITATIONAL_CONSTANT.get() * astronomical.mass / (obj.getDouble("scale") / 2 + ((obj.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128));

                Vector3d speed_in_xyz = new Vector3d(Math.random() * 2 - 1, 0, Math.random() * 2 - 1).normalize().mul(speed);
                speed_in_xyz.x += astronomical.x_speed;
                speed_in_xyz.y += astronomical.y_speed;
                speed_in_xyz.z += astronomical.z_speed;

                ShipAPI.teleportShipMultibody(VSGameUtilsKt.getShipObjectWorld(level), ship, new ShipTeleportDataImpl(newPos, newRotate, speed_in_xyz, new Vector3d(), "minecraft:dimension:" + targetDim, 1d));
                return;
            }
        }
    }

//    @Inject(method = "atmosphericCollisionTick", at = @At(value = "INVOKE", target = "Lnet/jcm/vsch/util/TeleportationHandler;finalizeTeleport()V", shift = At.Shift.AFTER), remap = false)
//    private static void afterHandleTeleport(ServerLevel level, CallbackInfo ci, @Local(ordinal = 1) CompoundTag atmoData, @Local TeleportationHandler teleportationHandler) {
//        VSOrbitMod.queueServerWork(80, () -> {
//            for (LoadedServerShip ship : teleportationHandler.getPendingShips()) {
//                ServerLevel serverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(ship.getChunkClaimDimension().replace("minecraft:dimension:", ""))));
//                if (serverLevel == null) return;
//                AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(serverLevel.dimension().location().toString());
//                if (astronomicalPool == null) return;
//
//                Astronomical astronomical = StarAPI.getAstronomicalFormLevel(level);
//                if (astronomical == null) return;
//
//                CompoundTag starData = StarAPI.getStarDataFormLevel(level);
//                if (starData == null) return;
//
//                double speed = Math.sqrt(VSOrbitModConfig.Gravitation_GRAVITATIONAL_CONSTANT.get() * astronomical.mass / (starData.getDouble("scale") / 2 + ((starData.getDouble("scale") / 2) * (Math.sqrt(2) - 1)) + 128));
//
//                Vector3d speed_in_xyz = new Vector3d(Math.random() * 2 - 1, 0, Math.random() * 2 - 1).normalize().mul(speed);
//                speed_in_xyz.x += astronomical.x_speed;
//                speed_in_xyz.y += astronomical.y_speed;
//                speed_in_xyz.z += astronomical.z_speed;
//
//                ship.setStatic(false);
//
//                ForcerInducedShips forcerInducedShips = ForcerInducedShips.getFromShip(ship);
//                if (forcerInducedShips == null) return;
//
//                Vector3d force = speed_in_xyz.mul(ship.getInertiaData().getMass()).mul(VSOrbitModConfig.ValkyrienSkies_ACCELERATION_SCALING.get() * AstronomicalThread.core_tick_time / VSOrbitModConfig.Core_TICK_TIME.get());
//
//                forcerInducedShips.addForce(new Force("get_into_orbit", force.x, force.y, force.z, 1));
//
//                for (Entity entity : level.getEntities(null, VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10))) if (entity instanceof ServerPlayer serverPlayer) serverPlayer.displayClientMessage(Component.literal("[VS_Orbit] [Game] " + Component.translatable("message.vs_orbit.game.orbit_synchronous").getString()), false);
//            }
//        });
//    }
}