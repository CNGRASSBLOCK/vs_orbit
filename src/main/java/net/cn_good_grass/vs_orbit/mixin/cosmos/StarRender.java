package net.cn_good_grass.vs_orbit.mixin.cosmos;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.RenderMINTProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin({RenderMINTProcedure.class})
public class StarRender {
    //天体渲染
    @WrapOperation(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"}, at = @At(value = "INVOKE", target = "net/minecraft/nbt/ListTag.get(I)Lnet/minecraft/nbt/Tag;"))
    private static Tag RotatePlanet(ListTag instance, int pKey, Operation<Tag> original, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        if (instance.get(pKey) instanceof CompoundTag cTag) {
            CompoundTag compoundTag = cTag.copy();

            CosmosAstronomical astronomical = StarAPI.getAstronomical(entity.level().dimension().location().toString(), partialTick, cTag, false);
            if (astronomical == null) return instance.get(pKey);

            compoundTag.putDouble("x", astronomical.x);
            compoundTag.putDouble("y", astronomical.y);
            compoundTag.putDouble("z", astronomical.z);

            Vector3d Rotate = new Quaterniond(astronomical.rotate).getEulerAnglesYXZ(new Vector3d());
            Rotate.x = Math.toDegrees(Rotate.x);
            Rotate.y = Math.toDegrees(Rotate.y);
            Rotate.z = Math.toDegrees(Rotate.z);

            compoundTag.putDouble("pitch", Rotate.x());
            compoundTag.putDouble("yaw", Rotate.y());
            compoundTag.putDouble("roll", Rotate.z());

            return compoundTag;
        }
        return instance.get(pKey);
    }
    //渲染层覆盖
    @Redirect(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"}, at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> ChangeDistanceOrder(CompoundTag map, double order, String dimension, Vec3 position, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        return StarAPI.changeOrder(entity, partialTick, (ListTag) CosmosModVariables.WorldVariables.get(world).render_data_map.get(dimension), -1.0F, dimension, position);
    }
    //光照绘制
    @WrapOperation(method = {"execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;DD)V"}, at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.get(Ljava/lang/String;)Lnet/minecraft/nbt/Tag;"))
    private static Tag ChangeLightingData(CompoundTag instance, String pKey, Operation<Tag> original, Event event, LevelAccessor world, Entity entity, double partialTick, double ticks) {
        return StarAPI.recalculateLight(instance, pKey, original, world, entity, partialTick);
    }
}