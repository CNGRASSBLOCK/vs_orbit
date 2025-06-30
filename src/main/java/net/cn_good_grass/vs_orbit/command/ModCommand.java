package net.cn_good_grass.vs_orbit.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.AstronomicalThread;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

@Mod.EventBusSubscriber
public class ModCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> main_command = Commands.literal("vs_orbit");
        //核心-信息
        main_command.then(Commands.literal("core").then(Commands.literal("info").executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) {
                int size = 0;
                for (String WorldId : Config.Gravitation_WORK_WORLD.get()) size += AstronomicalPool.getFromWorldID(WorldId).getAllAstronomical().size();
                int world_size = AstronomicalPool.getFromWorldID(entity.level().dimension().location().toString()).getAllAstronomical().size();
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.state").getString()), false);
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.target_tick").getString() + Config.Core_TICK_SPEED.get()), false);
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.actual_tick").getString() + AstronomicalThread.tick), false);
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.size").getString() + size), false);
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.world_size").getString() + world_size), false);
                player.displayClientMessage(Component.literal(Component.translatable("message.vs_orbit.core.info.end").getString()), false);
            } }
            return 0;
        })));
        //核心-暂停
        main_command.then(Commands.literal("core").then(Commands.literal("pause").executes(arguments -> {
            AstronomicalThread.pause = !AstronomicalThread.pause;

            Entity entity = arguments.getSource().getEntity();

            String state;
            if (AstronomicalThread.pause) state = Component.translatable("message.vs_orbit.core.state.pause").getString(); else state = Component.translatable("message.vs_orbit.core.state.run").getString();

            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.state").getString() + state ), false); } }

            return 0;
        })));
        //核心-速度
        main_command.then(Commands.literal("core").then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("zoom", DoubleArgumentType.doubleArg()).executes(arguments -> {
            AstronomicalThread.core_tick_time = Config.Core_TICK_TIME.get() * DoubleArgumentType.getDouble(arguments, "zoom");

            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.speed_set").getString()), false); } }

            return 0;
        })))));
        //质点-传送
        main_command.then(Commands.literal("astronomical").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("tp").then(Commands.argument("pos", Vec3Argument.vec3()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Astronomical astronomical = AstronomicalPool.getFromWorldID(entity.level().dimension().location().toString()).getAstronomical(StringArgumentType.getString(arguments, "name"));
                if (astronomical == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_astronomical").getString()), false);
                    return 0;
                }
                Vec3 pos = Vec3Argument.getVec3(arguments, "pos");

                astronomical.x = pos.x;
                astronomical.y = pos.y;
                astronomical.z = pos.z;

                if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.astronomical_tp").getString()), false); }
            }

            return 0;
        })))));
        //质点-速度
        main_command.then(Commands.literal("astronomical").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("x_speed", DoubleArgumentType.doubleArg()).then(Commands.argument("y_speed", DoubleArgumentType.doubleArg()).then(Commands.argument("z_speed", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Astronomical astronomical = AstronomicalPool.getFromWorldID(entity.level().dimension().location().toString()).getAstronomical(StringArgumentType.getString(arguments, "name"));
                if (astronomical == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_astronomical").getString()), false);
                    return 0;
                }

                astronomical.x_speed = DoubleArgumentType.getDouble(arguments, "x_speed");
                astronomical.y_speed = DoubleArgumentType.getDouble(arguments, "y_speed");
                astronomical.z_speed = DoubleArgumentType.getDouble(arguments, "z_speed");

                if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.astronomical_setspeed").getString()), false); }
            }

            return 0;
        }))))))));
        //质点-力-添加
        main_command.then(Commands.literal("astronomical").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("force").then(Commands.literal("add").then(Commands.argument("x_force", DoubleArgumentType.doubleArg()).then(Commands.argument("y_force", DoubleArgumentType.doubleArg()).then(Commands.argument("z_force", DoubleArgumentType.doubleArg()).then(Commands.argument("time", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Astronomical astronomical = AstronomicalPool.getFromWorldID(entity.level().dimension().location().toString()).getAstronomical(StringArgumentType.getString(arguments, "name"));
                if (astronomical == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_astronomical").getString()), false);
                    return 0;
                }

                Force force = new Force("CommandAdd-" + entity.getDisplayName() + "-At-" + System.currentTimeMillis(), DoubleArgumentType.getDouble(arguments, "x_force"), DoubleArgumentType.getDouble(arguments, "y_force"), DoubleArgumentType.getDouble(arguments, "z_force"), DoubleArgumentType.getDouble(arguments, "time"));
                astronomical.addForce(force);

                if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.astronomical_addforce").getString()), false);
            }

            return 0;
        })))))))));
        //质点-力-删除全部
        main_command.then(Commands.literal("astronomical").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("force").then(Commands.literal("removeall").then(Commands.argument("x_force", DoubleArgumentType.doubleArg()).then(Commands.argument("y_force", DoubleArgumentType.doubleArg()).then(Commands.argument("z_force", DoubleArgumentType.doubleArg()).then(Commands.argument("time", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Astronomical astronomical = AstronomicalPool.getFromWorldID(entity.level().dimension().location().toString()).getAstronomical(StringArgumentType.getString(arguments, "name"));
                if (astronomical == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_astronomical").getString()), false);
                    return 0;
                }

                astronomical.removeAllForce();

                if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.astronomical_addforce").getString()), false);
            }

            return 0;
        })))))))));
        //注册命令
        event.getDispatcher().register(main_command);
    }
}
