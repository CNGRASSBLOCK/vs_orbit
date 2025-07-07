//package net.cn_good_grass.vs_orbit.block;
//
//import dan200.computercraft.api.ComputerCraftAPI;
//import dan200.computercraft.api.peripheral.IPeripheral;
//import dan200.computercraft.api.peripheral.IPeripheralProvider;
//import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
//import net.cn_good_grass.vs_orbit.block.block_peripheral.MassGeneratorPeripheral;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.common.util.LazyOptional;
//import dan200.computercraft.api.ForgeComputerCraftAPIs.ComputerCraftAPIAttachEvent;
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//
//import javax.annotation.Nullable;
//import java.lang.reflect.Method;
//
//public class VSOrbitModPeripherals implements IPeripheralProvider {
//    @Nullable
//    @Override
//    public LazyOptional<IPeripheral> getPeripheral(
//            Level world,
//            BlockPos pos,
//            Direction side
//    ) {
//        BlockEntity be = world.getBlockEntity(pos);
//        if (be instanceof MassGeneratorBlockEntity generator) {
//            return generator.getCapability(
//                    MassGeneratorBlockEntity.CAPABILITY_PERIPHERAL,
//                    side
//            );
//        }
//        return LazyOptional.empty();
//    }
//
//    public static void register(ComputerCraftAPIAttachEvent event) {
//        ComputerCraftAPI.registerPeripheralProvider()
//    }
//}
