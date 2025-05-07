package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick;
import net.lointain.cosmos.network.CosmosModVariables;
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

@Mixin({RenderMINTProcedure.class})
public class StarRender {
    //设位置的 返回Vec3 xyz是坐标 Target_object应该是星球
    @Inject(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"},
            at = @At(value = "INVOKE", ordinal = 22, shift = At.Shift.AFTER, target = "net/minecraft/world/phys/Vec3.<init>(DDD)V"), remap = false)
    private static void RotatePlanet(Event event, LevelAccessor world, Entity entity, double partialTick, double ticks, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Vec3> pos, @Local(ordinal = 0) CompoundTag Target_object) {
        if (!Target_object.getString("object_name").equals("")) {
            pos.set(StarTick.getPos(entity.level().dimension().location().toString(), partialTick, Target_object));
        }
    }
    //防止渲染层出错的
    @Redirect(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"},
            at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> ChangeDistanceOrder(CompoundTag map, double order, String dimension, Vec3 position, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        return StarTick.changeOrder(world, entity, partialTick, (ListTag) CosmosModVariables.WorldVariables.get(world).render_data_map.get(dimension), -1.0F, dimension, position);
    }

    @WrapOperation(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"}, at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.get(Ljava/lang/String;)Lnet/minecraft/nbt/Tag;"), remap = false)
    private static Tag ChangeLightingData(CompoundTag instance, String pKey, Operation<Tag> original, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        return StarTick.recalculateLight(instance, pKey, original, world, entity, partialTick);
    }
}
