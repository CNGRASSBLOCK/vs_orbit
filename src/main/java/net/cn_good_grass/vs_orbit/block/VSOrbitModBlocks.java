package net.cn_good_grass.vs_orbit.block;

import net.cn_good_grass.vs_orbit.block.blocks.ElectromagneticTractorBlock;
import net.cn_good_grass.vs_orbit.block.blocks.JumpEngineControllerBlock;
import net.cn_good_grass.vs_orbit.block.blocks.MassGeneratorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "vs_orbit");
    // 注册方块
    public static final RegistryObject<Block> electromagnetic_tractor = BLOCKS.register("electromagnetic_tractor", ElectromagneticTractorBlock::new);
    public static final RegistryObject<Block> jump_engine_controller = BLOCKS.register("jump_engine_controller", JumpEngineControllerBlock::new);
    public static final RegistryObject<Block> mass_generator = BLOCKS.register("mass_generator", MassGeneratorBlock::new);
    // 在构造函数中注册到主模组总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}