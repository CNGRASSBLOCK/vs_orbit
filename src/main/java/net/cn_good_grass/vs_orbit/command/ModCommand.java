package net.cn_good_grass.vs_orbit.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Force;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.ParticleThread;
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

import java.math.BigDecimal;

@Mod.EventBusSubscriber
public class ModCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> main_command = Commands.literal("vs_orbit");
        //核心-暂停
        main_command.then(Commands.literal("core").then(Commands.literal("pause").then(Commands.argument("state", BoolArgumentType.bool()).executes(arguments -> {
            ParticleThread.pause = BoolArgumentType.getBool(arguments, "state");

            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.pause").getString()), false); } }

            return 0;
        }))));
        //核心-速度
        main_command.then(Commands.literal("core").then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("zoom", DoubleArgumentType.doubleArg()).executes(arguments -> {
            ParticleThread.core_tick_time = Config.Core_TICK_TIME.get() * DoubleArgumentType.getDouble(arguments, "zoom");

            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) { if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.speed_set").getString()), false); } }

            return 0;
        })))));
        //质点-传送
        main_command.then(Commands.literal("particle").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("tp").then(Commands.argument("pos", Vec3Argument.vec3()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Particle particle = ParticlePool.getFromWorldID(entity.level().dimension().location().toString()).getParticle(StringArgumentType.getString(arguments, "name"));
                if (particle == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_particle").getString()), false);
                    return 0;
                }
                Vec3 pos = Vec3Argument.getVec3(arguments, "pos");

                particle.x = pos.x;
                particle.y = pos.y;
                particle.z = pos.z;

                if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.particle_tp").getString()), false); }
            }

            return 0;
        })))));
        //质点-速度
        main_command.then(Commands.literal("particle").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("x_speed", DoubleArgumentType.doubleArg()).then(Commands.argument("y_speed", DoubleArgumentType.doubleArg()).then(Commands.argument("z_speed", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Particle particle = ParticlePool.getFromWorldID(entity.level().dimension().location().toString()).getParticle(StringArgumentType.getString(arguments, "name"));
                if (particle == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_particle").getString()), false);
                    return 0;
                }

                particle.x_speed = DoubleArgumentType.getDouble(arguments, "x_speed");
                particle.y_speed = DoubleArgumentType.getDouble(arguments, "y_speed");
                particle.z_speed = DoubleArgumentType.getDouble(arguments, "z_speed");

                if (entity instanceof Player player && !player.level().isClientSide()) { player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.particle_setspeed").getString()), false); }
            }

            return 0;
        }))))))));
        //质点-力-添加
        main_command.then(Commands.literal("particle").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("force").then(Commands.literal("add").then(Commands.argument("x_force", DoubleArgumentType.doubleArg()).then(Commands.argument("y_force", DoubleArgumentType.doubleArg()).then(Commands.argument("z_force", DoubleArgumentType.doubleArg()).then(Commands.argument("time", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Particle particle = ParticlePool.getFromWorldID(entity.level().dimension().location().toString()).getParticle(StringArgumentType.getString(arguments, "name"));
                if (particle == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_particle").getString()), false);
                    return 0;
                }

                Force force = new Force("CommandAdd-" + entity.getDisplayName() + "-At-" + System.currentTimeMillis(), BigDecimal.valueOf(DoubleArgumentType.getDouble(arguments, "x_force")), BigDecimal.valueOf(DoubleArgumentType.getDouble(arguments, "y_force")), BigDecimal.valueOf(DoubleArgumentType.getDouble(arguments, "z_force")), DoubleArgumentType.getDouble(arguments, "time"));
                particle.addForce(force);

                if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.particle_addforce").getString()), false);
            }

            return 0;
        })))))))));
        //质点-力-删除全部
        main_command.then(Commands.literal("particle").then(Commands.argument("name", StringArgumentType.string()).then(Commands.literal("force").then(Commands.literal("removeall").then(Commands.argument("x_force", DoubleArgumentType.doubleArg()).then(Commands.argument("y_force", DoubleArgumentType.doubleArg()).then(Commands.argument("z_force", DoubleArgumentType.doubleArg()).then(Commands.argument("time", DoubleArgumentType.doubleArg()).executes(arguments -> {
            Entity entity = arguments.getSource().getEntity();
            if ((entity != null)) {
                String name = StringArgumentType.getString(arguments, "name");
                if (name == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.empty_name").getString()), false);
                    return 0;
                }
                Particle particle = ParticlePool.getFromWorldID(entity.level().dimension().location().toString()).getParticle(StringArgumentType.getString(arguments, "name"));
                if (particle == null) {
                    if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.no_particle").getString()), false);
                    return 0;
                }

                particle.removeAllForce();

                if (entity instanceof Player player && !player.level().isClientSide()) player.displayClientMessage(Component.literal("[VS_Orbit] [Command] " + Component.translatable("message.vs_orbit.core.particle_addforce").getString()), false);
            }

            return 0;
        })))))))));
        //注册命令
        event.getDispatcher().register(main_command);
    }
}
