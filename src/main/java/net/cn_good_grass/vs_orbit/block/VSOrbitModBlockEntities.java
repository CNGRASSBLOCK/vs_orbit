package net.cn_good_grass.vs_orbit.block;

import net.cn_good_grass.vs_orbit.block.block_entities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "vs_orbit");
    // 注册方块实体
    public static final RegistryObject<BlockEntityType<JumpEngineControllerBlockEntity>> jump_engine_controller_block_entity = BLOCK_ENTITIES.register(
            "jump_engine_controller_block_entity",
            () -> BlockEntityType.Builder.of(JumpEngineControllerBlockEntity::new, VSOrbitModBlocks.jump_engine_controller.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<MassGeneratorBlockEntity>> mass_generator_block_entity = BLOCK_ENTITIES.register(
            "mass_generator_block_entity",
            () -> BlockEntityType.Builder.of(MassGeneratorBlockEntity::new, VSOrbitModBlocks.mass_generator.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<ElectricalTrusterBlockEntity>> electrical_truster_block_entity = BLOCK_ENTITIES.register(
            "electrical_truster_block_entity",
            () -> BlockEntityType.Builder.of(ElectricalTrusterBlockEntity::new, VSOrbitModBlocks.electrical_truster.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<CelestialTachymeterBlockEntity>> celestial_tachymeter_block_entity = BLOCK_ENTITIES.register(
            "celestial_tachymeter_block_entity",
            () -> BlockEntityType.Builder.of(CelestialTachymeterBlockEntity::new, VSOrbitModBlocks.celestial_tachymeter.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<OrbitalProjectorBlockEntity>> orbital_projector_block_entity = BLOCK_ENTITIES.register(
            "orbital_projector_block_entity",
            () -> BlockEntityType.Builder.of(OrbitalProjectorBlockEntity::new, VSOrbitModBlocks.orbital_projector_block.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<OrbitalControlConsoleBlockEntity>> orbital_control_console_block_entity = BLOCK_ENTITIES.register(
            "orbital_control_console_block_entity",
            () -> BlockEntityType.Builder.of(OrbitalControlConsoleBlockEntity::new, VSOrbitModBlocks.orbital_control_console_block.get()).build(null)
    );

    // 在构造函数中注册到主模组总线
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}