package net.cn_good_grass.vs_orbit.block.block_entities;

import dan200.computercraft.shared.Capabilities;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.block.block_peripheral.CelestialTachymeterPeripheral;
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
import org.joml.Vector3d;

public class CelestialTachymeterBlockEntity extends BlockEntity {
    public CelestialTachymeterBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.celestial_tachymeter_block_entity.get(), pos, state); }

    public String target = "";
    public Vector3d speed = new Vector3d();

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("speed_x", this.speed.x);
        tag.putDouble("speed_y", this.speed.y);
        tag.putDouble("speed_z", this.speed.z);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("speed_x")) this.speed.x = tag.getDouble("speed_x");
        if (tag.contains("speed_y")) this.speed.y = tag.getDouble("speed_y");
        if (tag.contains("speed_z")) this.speed.z = tag.getDouble("speed_z");
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



    private final LazyOptional<Object> peripheralCap = LazyOptional.of(() -> new CelestialTachymeterPeripheral(this));
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) return peripheralCap.cast();
        return super.getCapability(cap, side);
    }
}