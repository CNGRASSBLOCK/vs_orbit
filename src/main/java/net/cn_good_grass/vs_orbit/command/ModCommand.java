package net.cn_good_grass.vs_orbit.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

@Mod.EventBusSubscriber
public class ModCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> main_command = Commands.literal("vs_orbit");
        main_command.then(Commands.literal("core"));

        event.getDispatcher().register(main_command);
    }
}
