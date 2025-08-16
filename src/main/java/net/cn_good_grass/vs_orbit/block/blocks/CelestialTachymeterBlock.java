package net.cn_good_grass.vs_orbit.block.blocks;

import net.cn_good_grass.vs_orbit.block.block_entities.CelestialTachymeterBlockEntity;
import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class CelestialTachymeterBlock extends Block implements EntityBlock{
    public CelestialTachymeterBlock() {
        super(Properties.of()
                .strength(0.5f, 5f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
                .noOcclusion()
        );
    }

    @Override public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem) return tieredItem.getTier().getLevel() >= 2;else return super.canHarvestBlock(state, world, pos, player);
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

        if (level.getBlockEntity(pos) instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity) celestialTachymeterBlockEntity.setRemoved();

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        world.scheduleTick(pos, this, 1);

        if (!(world.getBlockEntity(pos) instanceof CelestialTachymeterBlockEntity celestialTachymeterBlockEntity)) return;

        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship != null) SpeedMeasurementForShip(ship, celestialTachymeterBlockEntity); else SpeedMeasurementForStar(world, celestialTachymeterBlockEntity);

        world.sendBlockUpdated(pos, blockstate, blockstate, 3);
    }

    private static void SpeedMeasurementForShip(Ship ship, CelestialTachymeterBlockEntity celestialTachymeterBlockEntity) {
        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(ship.getChunkClaimDimension().replace("minecraft:dimension:", ""));
        Astronomical astronomical = null;
        if (astronomicalPool != null) astronomical = astronomicalPool.getAstronomical("VSShip-" + ship.getId());
        celestialTachymeterBlockEntity.target = "VSShip-" + ship.getId();

        if (astronomical != null) celestialTachymeterBlockEntity.speed = new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed); else celestialTachymeterBlockEntity.speed = (Vector3d) ship.getVelocity();
    }

    private static void SpeedMeasurementForStar(ServerLevel world, CelestialTachymeterBlockEntity celestialTachymeterBlockEntity) {
        Astronomical astronomical = StarAPI.getAstronomicalFormLevel(world);
        if (astronomical != null) {
            celestialTachymeterBlockEntity.speed = new Vector3d(astronomical.x_speed, astronomical.y_speed, astronomical.z_speed);
            celestialTachymeterBlockEntity.target = astronomical.name;
            return;
        }
        celestialTachymeterBlockEntity.speed = new Vector3d(0, 0, 0);
        celestialTachymeterBlockEntity.target = "NULL";
    }
}