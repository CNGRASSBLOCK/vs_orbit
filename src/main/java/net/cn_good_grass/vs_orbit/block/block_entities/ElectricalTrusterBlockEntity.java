package net.cn_good_grass.vs_orbit.block.block_entities;

import dan200.computercraft.shared.Capabilities;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.procedures.computercraft.BlockPeripheral.ElectromagneticTractorPeripheral;
import net.cn_good_grass.vs_orbit.block.blocks.ElectricalTrusterBlock.Mode;
import net.cn_good_grass.vs_orbit.procedures.CompatMods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class ElectricalTrusterBlockEntity extends BlockEntity {
    public ElectricalTrusterBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.electrical_truster_block_entity.get(), pos, state); }

    public Mode mode = Mode.POSITION;
    public double force = 0;
    public int red_stone_power = 0;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("mode", this.mode.toString());
        tag.putDouble("force", this.force);
        tag.putInt("red_stone_power", this.red_stone_power);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mode")) this.mode = Mode.valueOf(tag.getString("mode"));
        if (tag.contains("force")) this.force = tag.getDouble("force");
        if (tag.contains("red_stone_power")) this.red_stone_power = tag.getInt("red_stone_power");
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

    public void sendToClient() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockPos pos = this.getBlockPos();
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }



    private final LazyOptional<Object> peripheralCap = LazyOptional.of(() -> new ElectromagneticTractorPeripheral(this));
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) return peripheralCap.cast();
        return super.getCapability(cap, side);
    }
}