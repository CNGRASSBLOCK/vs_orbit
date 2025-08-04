package net.cn_good_grass.vs_orbit.block.block_entities;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OrbitalProjectorBlockEntity extends BlockEntity {
    public OrbitalProjectorBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.orbital_projector_block_entity.get(), pos, state); }


    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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



//    private final LazyOptional<Object> peripheralCap = LazyOptional.of(() -> new CelestialTachymeterPeripheral(this));
//    @Override
//    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//        if(CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) return peripheralCap.cast();
//        return super.getCapability(cap, side);
//    }
}