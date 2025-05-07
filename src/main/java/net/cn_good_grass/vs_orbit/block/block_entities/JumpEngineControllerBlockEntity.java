package net.cn_good_grass.vs_orbit.block.block_entities;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JumpEngineControllerBlockEntity extends BlockEntity {
    public JumpEngineControllerBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.jump_engine_controller_block_entity.get(), pos, state); }

    public String state = "none";
    public String mode = "power";
    public Integer red_stone_power = 0;

    public String structure_state = "none";
    public Integer structure_radius = 0;
    public BlockPos structure_center_pos = new BlockPos(0, 0, 0);

    public UUID display_entity_uuid = new UUID(0,0);
    public Integer animation_tick = 0;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("state", this.state); // 保存到NBT
        tag.putString("mode", this.mode);
        tag.putInt("red_stone_power", this.red_stone_power);
        tag.putString("structure_state", this.structure_state);
        tag.putInt("structure_radius", this.structure_radius);
        tag.putIntArray("structure_center_pos", new ArrayList<>(List.of(structure_center_pos.getX(), structure_center_pos.getY(), structure_center_pos.getZ())));
        tag.putInt("animation_tick", this.animation_tick);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.state = tag.getString("state"); // 从NBT读取
        this.mode = tag.getString("mode");
        this.red_stone_power = tag.getInt("red_stone_power");
        this.structure_state = tag.getString("structure_state");
        this.structure_radius = tag.getInt("structure_radius");
        this.structure_center_pos = new BlockPos(tag.getIntArray("structure_center_pos")[0], tag.getIntArray("structure_center_pos")[1], tag.getIntArray("structure_center_pos")[2]);
        this.animation_tick = tag.getInt("animation_tick");
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