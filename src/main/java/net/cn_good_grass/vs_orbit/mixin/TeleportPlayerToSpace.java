package net.cn_good_grass.vs_orbit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
//import net.cn_good_grass.vs_orbit.procedures.other.ReturnTPCommand;
import net.lointain.cosmos.procedures.AtmosphericCollisionDetectorProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AtmosphericCollisionDetectorProcedure.class) //妈的这玩意得重写整个方法 不然太石山了
public class TeleportPlayerToSpace {
    @ModifyArg(method={"execute"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"), index = 1, remap = false)
//    @ModifyArg(method={"execute"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"), index = 1, remap = false)
    private static String changeTravelDestination(String pCommand, @Local(argsOnly = true) LevelAccessor world, @Local CompoundTag atmospheric_data) {
        //System.out.println("aaaaaaaaaaaaaaaaaaa" + ReturnTPCommand.ReturnTPCommand(pCommand, world, atmospheric_data));
        //return ReturnTPCommand.ReturnTPCommand(pCommand, world, atmospheric_data);
        return "";
    }
}