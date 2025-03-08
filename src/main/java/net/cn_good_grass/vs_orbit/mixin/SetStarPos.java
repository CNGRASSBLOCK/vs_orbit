package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.cn_good_grass.vs_orbit.procedures.gravitation.star.StarTick;
import net.lointain.cosmos.procedures.RenderMINTProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderMINTProcedure.class})
public class SetStarPos {
    //设位置的 返回Vec3 xyz是坐标 Target_object应该是星球
    @Inject(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"},
            at = @At(value = "INVOKE", ordinal = 22, shift = At.Shift.AFTER, target = "net/minecraft/world/phys/Vec3.<init>(DDD)V"), remap = false)
    private static void RotatePlanet(Event event, LevelAccessor world, Entity entity, double partialTick, double ticks, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Vec3> pos, @Local(ordinal = 0) CompoundTag Target_object) {
        if (!Target_object.getString("object_name").equals("")) {
            pos.set(StarTick.OnStarRender(entity.level().dimension().location().toString(), Target_object));
        }
    }
    //设方向的 返回tag还没写好
    /*@WrapOperation(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"}, at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.get(Ljava/lang/String;)Lnet/minecraft/nbt/Tag;"), remap = false)
    private static Tag ChangeLightingData(CompoundTag instance, String pKey, Operation<Tag> original, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        return null;
    }*/
}
