package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.jcm.vsch.util.VSCHUtils;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.CollisionDetectorProcedure;
import net.lointain.cosmos.procedures.DistanceOrderProviderProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CollisionDetectorProcedure.class)
public class PlayerStarCollision {
    @Inject(method = {"execute"}, at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/world/phys/Vec3.distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private static void RotateHitbox(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Vec3> pos, @Local(ordinal = 0) CompoundTag Target_object) {
        pos.set(StarAPI.getPos(entity.level().dimension().location().toString(), 1, Target_object, true));
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "net/minecraft/world/phys/Vec3.distanceTo(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0))
    private static double DirtyFix(Vec3 instance, Vec3 pVec, LevelAccessor world, double x, double y, double z, Entity entity, @Local(ordinal = 0) CompoundTag Target_object) {
        return StarAPI.getPos(entity.level().dimension().location().toString(), 1, Target_object, true).distanceTo(pVec);
    }

    @Redirect(method = {"execute"}, at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> ChangeDistanceOrder(CompoundTag map, double order, String dimension, Vec3 position, LevelAccessor world, double x, double y, double z, Entity entity) {
        return StarAPI.changeOrder(entity, 1, (ListTag) CosmosModVariables.WorldVariables.get(world).collision_data_map.get(dimension), 1.0F, dimension, position);
    }
}

