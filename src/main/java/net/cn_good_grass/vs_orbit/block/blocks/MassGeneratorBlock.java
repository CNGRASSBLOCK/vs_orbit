package net.cn_good_grass.vs_orbit.block.blocks;

import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.GravitationThread;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.registries.ForgeRegistries;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;

public class MassGeneratorBlock extends Block{
    public MassGeneratorBlock() {
        super(Properties.of()
                .strength(5f, 75f) // 硬度（挖掘时间）、爆炸抗性
                .sound(SoundType.STONE) // 音效类型
                .requiresCorrectToolForDrops() // 需要正确工具采集
                .lightLevel(state -> 8)
        );
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!(world instanceof ServerLevel)) return;// 确保只在服务端执行
        Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
        if (ship == null) return;

        List<Particle> particleList = ParticlePool.getFromWorldID(world.dimension().location().toString()).getGravitationCoreWorld();

        Particle particle = null;
        for (Particle oneparticle : particleList) if (oneparticle.name.equals("VSShip-" + ship.getId())) particle = oneparticle;
        if (particle == null) return;

        CompoundTag Cpos = particle.Tag.getCompound("vs_orbit:mass_add");
        CompoundTag ThisBlock = new CompoundTag();
        ThisBlock.putLong(pos.toString(), 1000);
    }
}