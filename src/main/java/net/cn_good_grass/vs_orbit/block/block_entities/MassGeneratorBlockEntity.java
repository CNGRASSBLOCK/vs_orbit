package net.cn_good_grass.vs_orbit.block.block_entities;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MassGeneratorBlockEntity extends BlockEntity {
    public MassGeneratorBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.mass_generator_block_entity.get(), pos, state); }

    public BigDecimal mass = new BigDecimal(0);

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("mass", this.mass.toString()); // 保存到NBT
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mass")) this.mass = new BigDecimal(tag.getString("mass")); // 从NBT读取
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag()); // 客户端接收到数据包后更新
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag); // 保存需要同步的数据
        return tag;
    }
}