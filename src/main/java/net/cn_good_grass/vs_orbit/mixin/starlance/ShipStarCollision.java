package net.cn_good_grass.vs_orbit.mixin.starlance;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.jcm.vsch.util.VSCHUtils;
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
    private static ListTag DistanceOrderProvider(ListTag cdm, LevelAccessor world, Vec3 position, String dimensionId) {
        ListTag Ropaque_object_map = cdm.copy();

        for (int i = 0; i < cdm.size(); i++) {
            CompoundTag StarData = cdm.getCompound(i);
            CosmosAstronomical astronomical = StarAPI.getAstronomical(dimensionId, 1, StarData, false);
            if (astronomical == null) return Ropaque_object_map;
            StarData.putDouble("x", astronomical.x);
            StarData.putDouble("y", astronomical.y);
            StarData.putDouble("z", astronomical.z);
            Ropaque_object_map.set(i, StarData);
        }

        return Ropaque_object_map;
    }

    @Redirect(method = "getNearestPlanet", at = @At(value = "INVOKE", target = "Lnet/lointain/cosmos/procedures/DistanceOrderProviderProcedure;execute(Lnet/minecraft/nbt/CompoundTag;DLjava/lang/String;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"), remap = false)
    private static List<Object> execute(CompoundTag map, double order, String dimension, Vec3 position, @Local(argsOnly = true) LevelAccessor world, @Local Vec3 Lposition, @Local String dimensionId) {
        ListTag listtag = StarAPI.getAllStarData((Level) world, false);
        ListTag newtag = new ListTag();
        for (Tag tag : listtag) {
            double x = 0, y = 0, z = 0;
            CosmosAstronomical astronomical = StarAPI.getAstronomical(dimension, 1, (CompoundTag) tag, false);
            if (astronomical != null) {
                x = astronomical.x;
                y = astronomical.y;
                z = astronomical.z;
            }
            newtag.add(newtag.size(), StringTag.valueOf("`" + x + "~" + y + "|" + z + "\\"));
        }
        map.put(dimension, newtag);

        return DistanceOrderProviderProcedure.execute(map, order, dimension, position);
    }
}

