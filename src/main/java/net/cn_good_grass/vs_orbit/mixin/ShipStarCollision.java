package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick;
import net.jcm.vsch.util.VSCHUtils;
import net.jcm.vsch.event.PlanetCollision;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.DistanceOrderProviderProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.impl.shadow.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mixin(VSCHUtils.class)
public class ShipStarCollision {
    @ModifyVariable(method = "getNearestPlanet", at = @At("STORE"), ordinal = 0, remap = false)
    private static Tag DistanceOrderProvider(Tag cdm, LevelAccessor world, Vec3 position, String dimensionId) {
        if (!(cdm instanceof ListTag collision_data_map)) return cdm;

        ListTag Rcollision_data_map = collision_data_map.copy();

        for (int i = 0; i < collision_data_map.size(); i++) {
            CompoundTag StarData = collision_data_map.getCompound(i);
            Vec3 StarPos = StarTick.getPos(dimensionId, 0, StarData);
            StarData.putDouble("x", StarPos.x);
            StarData.putDouble("y", StarPos.y);
            StarData.putDouble("z", StarPos.z);
            Rcollision_data_map.set(i, StarData);
        }

        return Rcollision_data_map;
    }

    @Redirect(method = "getNearestPlanet", at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> execute(CompoundTag map, double order, String dimension, Vec3 position, @Local(argsOnly = true) LevelAccessor world, @Local Vec3 Lposition, @Local String dimensionId) {
        List<Tag> AllPlanetKey = new ArrayList<>();
        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(world);
        if (!worldVars.collision_data_map.contains(dimensionId)) return DistanceOrderProviderProcedure.execute(map, order, dimension, position);
        Tag collision_data_map = worldVars.collision_data_map.get(dimensionId); //星球数据
        ListTag listtag = new ListTag();
        if (collision_data_map instanceof ListTag listTag) { listtag.addAll(listTag.copy()); }
        if (listtag.isEmpty()) return DistanceOrderProviderProcedure.execute(map, order, dimension, position);

        ListTag newtag = new ListTag();
        for (Tag tag : listtag) {
            Vec3 pos = StarTick.getPos(dimension, 0, (CompoundTag) tag);
            newtag.add(newtag.size(), StringTag.valueOf("`" + pos.x + "~" + pos.y + "|" + pos.z + "\\"));
        }
        map.put(dimension, newtag);

        return DistanceOrderProviderProcedure.execute(map, order, dimension, position);
    }
}

