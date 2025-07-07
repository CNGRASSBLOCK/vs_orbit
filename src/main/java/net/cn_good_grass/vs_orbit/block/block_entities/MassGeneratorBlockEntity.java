package net.cn_good_grass.vs_orbit.block.block_entities;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MassGeneratorBlockEntity extends BlockEntity {
    public MassGeneratorBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.mass_generator_block_entity.get(), pos, state); }

    public double mass = 0;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("mass", this.mass); // 保存到NBT
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mass")) this.mass = tag.getDouble("mass"); // 从NBT读取
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



//    public static final Capability<IPeripheral> CAPABILITY_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {});
//    private final MassGeneratorPeripheral peripheral = new MassGeneratorPeripheral(this);
//    private final LazyOptional<IPeripheral> peripheralCap = LazyOptional.of(() -> peripheral);
//
//    @Override
//    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == CAPABILITY_PERIPHERAL) return peripheralCap.cast();
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        peripheralCap.invalidate();
//    }
}