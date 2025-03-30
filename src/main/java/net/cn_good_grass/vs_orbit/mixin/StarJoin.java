package net.cn_good_grass.vs_orbit.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate.StarTick;
import net.lointain.cosmos.CosmosMod;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.TravelCProcedure;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.text.DecimalFormat;
import java.util.Objects;

@Mixin(TravelCProcedure.class)
public class StarJoin {
    /**
     * @author Alenigma
     * @reason My exams are coming up and I don't want to think. If there are any conflicts, write me
     */
    @Overwrite(remap = false)
    private static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) return;
        boolean dimention_check = false;
        boolean check = false;
        JsonObject read_json = (new Gson()).fromJson(CosmosModVariables.WorldVariables.get(world).planet_sel_data.get((int) entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables()).page_sel_index).getAsString(), JsonObject.class);
        JsonObject obj_json = read_json.get("object_data").getAsJsonObject();
        JsonObject planet_json = obj_json.get(obj_json.keySet().stream().toList().get((int) entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables()).planet_page_sel_index)).getAsJsonObject();
        for (Tag dataelementiterator : entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables()).traveled_dimentions)
            if (planet_json.get("unlocking_dimension").getAsString().equals(dataelementiterator.getAsString())) {
                check = true;
                break;
            }
        if (CosmosModVariables.WorldVariables.get(world).dimension_type.contains(entity.level().dimension().location().toString()))
            dimention_check = Objects.requireNonNullElse(CosmosModVariables.WorldVariables.get(world).dimension_type.get(entity.level().dimension().location().toString()), new CompoundTag()).getAsString().equals("space");
        if (!check) return;
        if (!dimention_check) {
            if (entity instanceof Player _player && !_player.level().isClientSide())
                _player.displayClientMessage(Component.literal("You are not in space yet!!"), true);
            return;
        }
        if (entity.getVehicle() instanceof RocketSeatEntity) {
            double posX;
            double posY;
            double posZ;
            if (planet_json.has("object_name")) {
                CompoundTag obj = (CompoundTag) ((ListTag) Objects.requireNonNull(CosmosModVariables.WorldVariables.get(world).render_data_map.get(entity.level().dimension().location().toString()))).stream().filter(e -> Objects.requireNonNullElse(((CompoundTag) e).get("object_name"), new CompoundTag()).getAsString().equals(planet_json.get("object_name").getAsString())).findFirst().orElse(new CompoundTag());
                Vec3 pos = StarTick.GetPos(world, entity.level().dimension().location().toString(), StarTick.getPartialTick(world), new Vec3(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z")), obj);
                posX = pos.x;
                posY = pos.y + obj.getDouble("scale");
                posZ = pos.z;
            } else {
                posX = planet_json.get("travel_x").getAsDouble() + (double) Mth.nextInt(RandomSource.create(), -20, 20);
                posY = planet_json.get("travel_y").getAsDouble() + (double) Mth.nextInt(RandomSource.create(), -8, 8);
                posZ = planet_json.get("travel_z").getAsDouble() + (double) Mth.nextInt(RandomSource.create(), -20, 20);
            }
            String dimension = read_json.get("travel_dimension").getAsString();
            if (!entity.level().dimension().location().toString().equals(read_json.get("travel_dimension").getAsString())) {
                if (entity instanceof Player _player) _player.closeContainer();
                String _setval = entity.getVehicle().getStringUUID();
                entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent((capability) -> {
                    capability.vehicle = _setval;
                    capability.syncPlayerVariables(entity);
                });
                if (world instanceof ServerLevel _level) {
                    _level.getServer().getCommands().performPrefixedCommand((new CommandSourceStack(CommandSource.NULL, new Vec3(entity.getX(), entity.getY(), entity.getZ()), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null)).withSuppressedOutput(), "execute in " + dimension + " run tp " + entity.getVehicle().getStringUUID() + " " + (new DecimalFormat("##")).format(posX) + " " + (new DecimalFormat("##")).format(posY) + " " + (new DecimalFormat("##")).format(posZ));
                    _level.getServer().getCommands().performPrefixedCommand((new CommandSourceStack(CommandSource.NULL, new Vec3(entity.getX(), entity.getY(), entity.getZ()), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null)).withSuppressedOutput(), "execute in " + dimension + " run tp " + entity.getStringUUID() + " " + (new DecimalFormat("##")).format(posX) + " " + (new DecimalFormat("##")).format(posY) + " " + (new DecimalFormat("##")).format(posZ));
                }
                CosmosMod.queueServerWork(20, () -> {
                    for (int index0 = 0; index0 < 70; ++index0) {
                        if (!entity.isPassenger() && !(entity.getVehicle() instanceof RocketSeatEntity) && !entity.level().isClientSide() && entity.getServer() != null) {
                            Commands var1000 = entity.getServer().getCommands();
                            CommandSourceStack var10001 = new CommandSourceStack(CommandSource.NULL, entity.position(), entity.getRotationVector(), (ServerLevel) entity.level(), 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity);
                            String var10002 = entity.getStringUUID();
                            var1000.performPrefixedCommand(var10001, "ride " + var10002 + " mount " + entity.getCapability(CosmosModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CosmosModVariables.PlayerVariables()).vehicle);
                        }
                    }

                });
            } else if (entity.level().dimension().location().toString().equals(read_json.get("travel_dimension").getAsString())) {
                if (entity instanceof Player _player) _player.closeContainer();
                Entity _ent = entity.getVehicle();
                _ent.teleportTo(posX, posY, posZ);
                if (_ent instanceof ServerPlayer _serverPlayer1)
                    _serverPlayer1.connection.teleport(posX, posY, posZ, _ent.getYRot(), _ent.getXRot());
            }
        }
    }
}