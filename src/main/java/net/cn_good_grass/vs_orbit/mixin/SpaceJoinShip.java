package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick;
import net.jcm.vsch.event.AtmosphericCollision;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick.getPartialTick;

@Mixin(AtmosphericCollision.class) //妈的这玩意得重写整个方法 不然太石山了
public class SpaceJoinShip {
    @Redirect(method={"atmosphericCollisionTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getDouble(Ljava/lang/String;)D"), remap = false)
    private static double SpaceJoinShip(CompoundTag atmospheric_data, String key, @Local(argsOnly = true) LevelAccessor world) {
        if (!key.contains("origin")) return atmospheric_data.getDouble(key);


        CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(world);
        Tag collision_data_map = worldVars.collision_data_map.get(atmospheric_data.getString("travel_to"));//星球数据
        if (collision_data_map == null) { return atmospheric_data.getDouble(key); }
        ListTag listtag = (ListTag) collision_data_map;
        String WorldId = ((Level) world).dimension().location().toString();
        CompoundTag obj = null;
        for (Tag tag : listtag) {
            if (tag instanceof CompoundTag compoundTag && compoundTag.contains("travel_to")) {
                if (compoundTag.getString("travel_to").equals(WorldId)) { obj = compoundTag; }
            }
        }
        if (obj == null) { return atmospheric_data.getDouble(key);}
        Vec3 newPos = StarTick.getPos(atmospheric_data.getString("travel_to"), getPartialTick(world), obj);

        if (key.contains("x")) { return newPos.x; } else if (key.contains("y")) { return newPos.y + (obj.getDouble("scale") / 2); } else if (key.contains("z")) { return newPos.z; }

        return atmospheric_data.getDouble(key);
    }
}