package net.cn_good_grass.vs_orbit.block;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.*;
import net.cn_good_grass.vs_orbit.block.block_entities_renderer.CelestialTachymeterBlockEntityRenderer;
import net.cn_good_grass.vs_orbit.block.block_entities_renderer.MassGeneratorBlockEntityRenderer;
import net.cn_good_grass.vs_orbit.block.block_entities_renderer.OrbitalProjectorBlockEntityRenderer;
import net.jcm.vsch.blocks.entity.VSCHBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = VSOrbitMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VSOrbitModBlockEntitiesRenderer {
    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VSOrbitModBlockEntities.mass_generator_block_entity.get(), MassGeneratorBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(VSOrbitModBlockEntities.celestial_tachymeter_block_entity.get(), CelestialTachymeterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(VSOrbitModBlockEntities.orbital_projector_block_entity.get(), OrbitalProjectorBlockEntityRenderer::new);
    }
}