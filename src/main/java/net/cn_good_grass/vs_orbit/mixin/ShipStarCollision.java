package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.jcm.vsch.util.VSCHUtils;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.DistanceOrderProviderProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(VSCHUtils.class)
public class ShipStarCollision {
    @ModifyVariable(method = "getNearestPlanet", at = @At("STORE"), ordinal = 0, remap = false)
    private static Tag DistanceOrderProvider(Tag cdm, LevelAccessor world, Vec3 position, String dimensionId) {
        if (!(cdm instanceof ListTag collision_data_map)) return cdm;

        ListTag Rcollision_data_map = collision_data_map.copy();

        for (int i = 0; i < collision_data_map.size(); i++) {
            CompoundTag StarData = collision_data_map.getCompound(i);
            Vec3 StarPos = StarAPI.getPos(dimensionId, 1, StarData, true);
            StarData.putDouble("x", StarPos.x);
            StarData.putDouble("y", StarPos.y);
            StarData.putDouble("z", StarPos.z);
            Rcollision_data_map.set(i, StarData);
        }

        return Rcollision_data_map;
    }

    @Redirect(method = "getNearestPlanet", at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> execute(CompoundTag map, double order, String dimension, Vec3 position, @Local(argsOnly = true) LevelAccessor world, @Local Vec3 Lposition, @Local String dimensionId) {
        ListTag newtag = StarAPI.getAllStarData((Level) world);
        for (Tag tag : newtag) {
            Vec3 pos = StarAPI.getPos(dimension, 1, (CompoundTag) tag, true);
            newtag.add(newtag.size(), StringTag.valueOf("`" + pos.x + "~" + pos.y + "|" + pos.z + "\\"));
        }
        map.put(dimension, newtag);

        return DistanceOrderProviderProcedure.execute(map, order, dimension, position);
    }
}

