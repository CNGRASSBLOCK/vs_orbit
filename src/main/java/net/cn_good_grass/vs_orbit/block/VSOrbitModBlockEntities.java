package net.cn_good_grass.vs_orbit.block;

import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "vs_orbit");
    // 注册方块实体
    public static final RegistryObject<BlockEntityType<JumpEngineControllerBlockEntity>> jump_engine_controller_block_entity = BLOCK_ENTITIES.register("jump_engine_controller_block_entity", () -> BlockEntityType.Builder.of(JumpEngineControllerBlockEntity::new).build(null));
    // 在构造函数中注册到主模组总线
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}