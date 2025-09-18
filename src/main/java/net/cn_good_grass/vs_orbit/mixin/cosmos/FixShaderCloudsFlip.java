package net.cn_good_grass.vs_orbit.mixin.cosmos;

import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class, priority = 900)
public class FixShaderCloudsFlip {
    @Shadow private float renderDistance;

    /**
     * @author 草方块
     * @reason null
     * 防止光影云层反转
     */
    @Overwrite
    public float getDepthFar() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return renderDistance * 4.0F;
        if (VSOrbitDataPack.OrbitWorld.contains(level.dimension().location().toString())) return Float.POSITIVE_INFINITY;
        return renderDistance * 4.0F;
    }
}
