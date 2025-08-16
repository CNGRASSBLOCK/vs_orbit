package net.cn_good_grass.vs_orbit.block.block_entities;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlockEntities;
import net.cn_good_grass.vs_orbit.block.blocks.OrbitalProjectorBlock;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public class OrbitalProjectorBlockEntity extends BlockEntity {
    public OrbitalProjectorBlockEntity(BlockPos pos, BlockState state) { super(VSOrbitModBlockEntities.orbital_projector_block_entity.get(), pos, state); }

    public Vector3d display_center_pos = new Vector3d();
    public double display_size = 0;

    public String data_world = "";
    public OrbitalProjectorBlock.DataMode data_mode = OrbitalProjectorBlock.DataMode.LOCK;
    public Object data_center = new Vector3d();
    public double data_radius = Double.MAX_VALUE;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("display_center_pos_x", this.display_center_pos.x);
        tag.putDouble("display_center_pos_y", this.display_center_pos.y);
        tag.putDouble("display_center_pos_z", this.display_center_pos.z);
        tag.putDouble("display_size", this.display_size);

        tag.putString("data_world", data_world);
        tag.putString("data_mode", data_mode.toString());
        if (this.data_center instanceof Vector3d vector3d) {
            tag.putDouble("data_center_vector3d_x", vector3d.x);
            tag.putDouble("data_center_vector3d_y", vector3d.y);
            tag.putDouble("data_center_vector3d_z", vector3d.z);
        } else if (this.data_center instanceof Astronomical astronomical) {
            tag.putString("data_center_astronomical_name", astronomical.name);
        }
        tag.putDouble("data_radius", this.data_radius);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("display_center_pos_x")) this.display_center_pos.x = tag.getDouble("display_center_pos_x");
        if (tag.contains("display_center_pos_y")) this.display_center_pos.y = tag.getDouble("display_center_pos_y");
        if (tag.contains("display_center_pos_z")) this.display_center_pos.z = tag.getDouble("display_center_pos_z");
        if (tag.contains("display_size")) this.display_size = tag.getDouble("display_size");

        if (tag.contains("data_world")) this.data_world = tag.getString("data_world");
        if (tag.contains("data_mode")) this.data_mode = OrbitalProjectorBlock.DataMode.valueOf(tag.getString("data_mode"));
        if (tag.contains("data_center_vector3d_x") && tag.contains("data_center_vector3d_y") && tag.contains("data_center_vector3d_z")) {
            this.data_center = new Vector3d(tag.getDouble("data_center_vector3d_x"), tag.getDouble("data_center_vector3d_y"), tag.getDouble("data_center_vector3d_z"));
        } else if (tag.contains("data_center_astronomical_name")) {
            Astronomical astronomical = null;
            AstronomicalPool astronomicalPool;
            if (this.level != null && !this.level.isClientSide()) astronomicalPool = AstronomicalPool.getFromWorldID(this.data_world); else astronomicalPool = AstronomicalPool.getFromWorldIDCilent(this.data_world, false);
            if (astronomicalPool != null) astronomical = astronomicalPool.getAstronomical(tag.getString("data_center_astronomical_name"));
            if (astronomical != null) this.data_center = astronomical;
        }
        if (tag.contains("data_radius")) this.data_radius = tag.getDouble("data_radius");
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