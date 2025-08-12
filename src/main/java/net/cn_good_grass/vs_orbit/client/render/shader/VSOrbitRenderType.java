//package net.cn_good_grass.vs_orbit.client.render.shader;
//
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormat;
//import net.minecraft.Util;
//import net.minecraft.client.renderer.RenderStateShard;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.function.Function;
//
//public class VSOrbitRenderType extends RenderType {
//    private VSOrbitRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
//        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
//    }
//
//    public static final Function<ResourceLocation, RenderType> PlanetEngineFire = Util.memoize(resourceLocation -> {
//        RenderType.CompositeState state = RenderType.CompositeState.builder()
//                .setShaderState(VSOrbitShaders.PlanetEngineFire.shard)
//                .setCullState(NO_CULL)
//                .setTextureState(new RenderStateShard.BooleanStateShard.TextureStateShard(resourceLocation, false, false))
//                .setTransparencyState(LIGHTNING_TRANSPARENCY)
//                .setOutputState(RenderType.TRANSLUCENT_TARGET)
//                .createCompositeState(true);
//        return create("vs_orbit_planet_engine_fire", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, true, true, state);
//    });
//}
