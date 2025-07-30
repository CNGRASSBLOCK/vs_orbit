package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.AtmosphericCollisionDetectorProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AtmosphericCollisionDetectorProcedure.class) //妈的这玩意得重写整个方法 不然太石山了
public class SpaceJoinPlayer {
    @ModifyArg(method={"execute"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"), index = 1)
    private static String SpaceJoinPlayer(String pCommand, @Local(argsOnly = true) LevelAccessor world, @Local(argsOnly = true) Entity entity, @Local CompoundTag atmospheric_data) {
        String ReturnCommand = "execute in {travel_to} run tp {uuid} {x} {y} {z}";
        if (pCommand.contains("tp")) {
            ReturnCommand = ReturnCommand.replace("{uuid}", pCommand.substring((pCommand.indexOf("tp ") + 3)).substring(0, pCommand.substring((pCommand.indexOf("tp ") + 3)).indexOf(" ")));
            if (atmospheric_data.contains("travel_to")) ReturnCommand = ReturnCommand.replace("{travel_to}", atmospheric_data.getString("travel_to"));

            CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(world);
            Tag opaque_object_map = worldVars.opaque_object_map.get(atmospheric_data.getString("travel_to"));//星球数据
            if (opaque_object_map == null) { return pCommand; }
            ListTag listtag = (ListTag) opaque_object_map;
            String WorldId = ((Level) world).dimension().location().toString();
            CompoundTag obj = null;
            for (Tag tag : listtag) if (tag instanceof CompoundTag compoundTag && compoundTag.contains("travel_to")) if (compoundTag.getString("travel_to").equals(WorldId)) obj = compoundTag;
            if (obj == null) { return pCommand;}
            Vec3 newPos = StarAPI.getPos(atmospheric_data.getString("travel_to"), 1, obj, true);

            ReturnCommand = ReturnCommand.replace("{x}", "" + newPos.x);
            ReturnCommand = ReturnCommand.replace("{y}", "" + (newPos.y + (obj.getDouble("scale") / 2)));
            ReturnCommand = ReturnCommand.replace("{z}", "" + newPos.z);

            return ReturnCommand;
        }
        return pCommand;
    }
}