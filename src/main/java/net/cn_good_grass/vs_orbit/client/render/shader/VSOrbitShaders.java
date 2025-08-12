//package net.cn_good_grass.vs_orbit.client.render.shader;
//
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import net.cn_good_grass.vs_orbit.VSOrbitMod;
//import net.minecraft.client.renderer.RenderStateShard;
//import net.minecraft.client.renderer.ShaderInstance;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RegisterShadersEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.io.IOException;
//import java.util.function.Supplier;
//
//@Mod.EventBusSubscriber(modid = VSOrbitMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class VSOrbitShaders {
//    static final ShaderTracker PlanetEngineFire = new ShaderTracker();
//
//    @SubscribeEvent
//    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
//        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("vs_orbit:planet_engine_fire"), DefaultVertexFormat.POSITION_COLOR_TEX), PlanetEngineFire::setInstance);
//    }
//
//    static class ShaderTracker implements Supplier<ShaderInstance> {
//        private ShaderInstance instance;
//        final RenderStateShard.ShaderStateShard shard = new RenderStateShard.ShaderStateShard(this);
//
//        private ShaderTracker() {}
//
//        private void setInstance(ShaderInstance instance) {
//            this.instance = instance;
//        }
//
//        @Override public ShaderInstance get() {
//            return instance;
//        }
//    }
//}
