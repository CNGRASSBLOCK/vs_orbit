package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.CollisionDetectorProcedure;
import net.lointain.cosmos.procedures.RenderMINTProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CollisionDetectorProcedure.class)
public class StarRingRender {
    @Inject(method = {"execute"}, at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/world/phys/Vec3.distanceTo(Lnet/minecraft/world/phys/Vec3;)D"), remap = false)
//    @Inject(method = {"execute"}, at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/world/phys/Vec3.m_82554_(Lnet/minecraft/world/phys/Vec3;)D"), remap = false)
    private static void RotateHitbox(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Vec3> pos, @Local(ordinal = 0) CompoundTag Target_object) {
        pos.set(StarTick.getPos(entity.level().dimension().location().toString(), StarTick.getPartialTick(world), Target_object));
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "net/minecraft/world/phys/Vec3.distanceTo(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0), remap = false)
//    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "net/minecraft/world/phys/Vec3.m_82554_(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0), remap = false)
    private static double DirtyFix(Vec3 instance, Vec3 pVec, LevelAccessor world, double x, double y, double z, Entity entity, @Local(ordinal = 0) CompoundTag Target_object) {
        return StarTick.getPos(entity.level().dimension().location().toString(), StarTick.getPartialTick(world), Target_object).distanceTo(pVec);
    }

    @Redirect(method = {"execute"}, at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> ChangeDistanceOrder(CompoundTag map, double order, String dimension, Vec3 position, LevelAccessor world, double x, double y, double z, Entity entity) {
        return StarTick.changeOrder(world, entity, StarTick.getPartialTick(world), (ListTag) CosmosModVariables.WorldVariables.get(world).collision_data_map.get(dimension), 1.0F, dimension, position);
    }
}
