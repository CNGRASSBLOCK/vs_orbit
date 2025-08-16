package net.cn_good_grass.vs_orbit.block.block_entities;

import dan200.computercraft.shared.Capabilities;
import net.cn_good_grass.vs_orbit.procedures.CompatMods;
import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.procedures.computercraft.BlockPeripheral.MassGeneratorPeripheral;
import net.cn_good_grass.vs_orbit.procedures.mekanism.EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Quaternionf;

public class MassGeneratorBlockEntity extends BlockEntity {
    public MassGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VSOrbitModBlockEntities.mass_generator_block_entity.get(), pos, state);
        this.energyStorage = new EnergyStorage(1000000000);
    }

    public double mass = 0;

    public Quaternionf cube = new Quaternionf();

    public final EnergyStorage energyStorage;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("mass", this.mass); // 保存到NBT
        tag.putFloat("cube_x", this.cube.x);
        tag.putFloat("cube_y", this.cube.y);
        tag.putFloat("cube_z", this.cube.z);
        tag.putFloat("cube_w", this.cube.w);

        tag.putInt("Energy", this.energyStorage.storedEnergy);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mass")) this.mass = tag.getDouble("mass"); // 从NBT读取
        if (tag.contains("cube_x")) this.cube.x = tag.getFloat("cube_x"); // 从NBT读取
        if (tag.contains("cube_y")) this.cube.y = tag.getFloat("cube_y"); // 从NBT读取
        if (tag.contains("cube_z")) this.cube.z = tag.getFloat("cube_z"); // 从NBT读取
        if (tag.contains("cube_w")) this.cube.w = tag.getFloat("cube_w"); // 从NBT读取

        if (tag.contains("Energy")) this.energyStorage.storedEnergy = tag.getInt("Energy");
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



    private final LazyOptional<Object> peripheralCap = LazyOptional.of(() -> new MassGeneratorPeripheral(this));
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return LazyOptional.of(() -> this.energyStorage).cast();
        if (CompatMods.COMPUTERCRAFT.isLoaded() && cap == Capabilities.CAPABILITY_PERIPHERAL) return peripheralCap.cast();
        return super.getCapability(cap, side);
    }
}