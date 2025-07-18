package net.cn_good_grass.vs_orbit.block.blocks;

import io.netty.buffer.Unpooled;
import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.config.Config;
import net.cn_good_grass.vs_orbit.gui.MassGeneratorGUI.MassGeneratorGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class CelestialTachymeterBlock extends Block implements EntityBlock{
    public CelestialTachymeterBlock() {
        super(Properties.of()
                .strength(5f, 75f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
                .noOcclusion()
        );
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CelestialTachymeterBlockEntity(pos, state); }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        if (!(world instanceof ServerLevel)) return;
        world.scheduleTick(pos, this, 1);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel)) return;
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);

        if (!(world.getBlockEntity(pos) instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity)) return;

        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship != null)
            celestialTachymeterBlockEntity.speed = SpeedMeasurementForShip(ship, world, pos);
        else
            celestialTachymeterBlockEntity.speed = SpeedMeasurementForStar(world);

        world.scheduleTick(pos, this, 1);
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }

    private static Vector3d SpeedMeasurementForShip(Ship ship, ServerLevel world, BlockPos pos) {
        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(ship.getChunkClaimDimension().replace("minecraft:dimension:", ""));
        if (astronomicalPool == null) return new Vector3d(0, 0, 0);
        Astronomical astronomical = astronomicalPool.getAstronomical("VSShip-" + ship.getId());

        if (astronomical != null) { //如果有质点优先使用质点速度
            return new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed);
        } else {
            if (!(world.getBlockEntity(pos) instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity)) return new Vector3d(0, 0, 0);
        }

        return new Vector3d(0, 0, 0);
    }

    private static Vector3d SpeedMeasurementForStar(ServerLevel world) {
        for (String WorldId : Config.Gravitation_WORK_WORLD.get()) {
            ServerLevel level = world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldId)));
            if (level == null) continue;
            ListTag data = StarAPI.getAllStarData(level, false);
            if (data == null) continue;
            for (int i = 0; i < data.size(); i++) {
                CompoundTag StarTag = data.getCompound(i);
                if (StarTag.getString("travel_to").equals(world.dimension().location().toString())) {
                    AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldId);
                    if (astronomicalPool == null) continue;
                    Astronomical astronomical = astronomicalPool.getAstronomical("CosmosStar-" + StarTag.getString("object_name"));
                    if (astronomical != null) return new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed);
                }
            }
        }


        return new Vector3d(0, 0, 0);
    }
}