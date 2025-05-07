package net.cn_good_grass.vs_orbit.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.gui.JumpEngineControllerGUI.JumpEngineControllerGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.GravitationThread;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber
public class ModCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> main_command = Commands.literal("vs_orbit");
        //下面是core的
        main_command.then(Commands.literal("core").then(Commands.literal("pause").then(Commands.argument("state", BoolArgumentType.bool()).executes(arguments -> {
            GravitationThread.pause = BoolArgumentType.getBool(arguments, "state");

            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.pause").getString()), false); } }

            return 0;
        }))));

        main_command.then(Commands.literal("core").then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("zoom", DoubleArgumentType.doubleArg()).executes(arguments -> {
            GravitationThread.core_tick_time = Config.Core_TICK_TIME.get() * DoubleArgumentType.getDouble(arguments, "zoom");

            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.speed_set").getString()), false); } }

            return 0;
        })))));
        //注册命令
        event.getDispatcher().register(main_command);
    }
}
