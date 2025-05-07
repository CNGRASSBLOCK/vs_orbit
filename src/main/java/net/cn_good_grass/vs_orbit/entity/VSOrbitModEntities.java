
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.cn_good_grass.vs_orbit.entity;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.entity.ThrusterCore.ThrusterCoreEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VSOrbitModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VSOrbitMod.MODID);
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) { return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname)); }

	@SubscribeEvent public static void init(FMLCommonSetupEvent event) { event.enqueueWork(() -> {
		ThrusterCoreEntity.init();
	}); }

	@SubscribeEvent public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(THRUSTER_CORE.get(), ThrusterCoreEntity.createAttributes().build());
	}
	//注册实体
	public static final RegistryObject<EntityType<ThrusterCoreEntity>> THRUSTER_CORE = register("thruster_core", EntityType.Builder.<ThrusterCoreEntity>of(ThrusterCoreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(2147463647).setUpdateInterval(3).setCustomClientFactory(ThrusterCoreEntity::new).fireImmune().sized(1f, 1f));
}
