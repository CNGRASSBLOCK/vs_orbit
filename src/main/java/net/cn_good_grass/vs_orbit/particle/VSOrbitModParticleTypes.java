package net.cn_good_grass.vs_orbit.particle;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSOrbitModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VSOrbitMod.MODID);

    public static final RegistryObject<SimpleParticleType> ElectricalTrusterFireParticle = REGISTRY.register("electrical_truster_fire_particle", () -> new SimpleParticleType(false));
}