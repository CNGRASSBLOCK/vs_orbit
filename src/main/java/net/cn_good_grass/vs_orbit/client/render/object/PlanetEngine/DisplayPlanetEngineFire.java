package net.cn_good_grass.vs_orbit.client.render.object.PlanetEngine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DisplayPlanetEngineFire {
    @SubscribeEvent
    public static void WorldRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        for (PlanetEngineFire fire : PlanetEngineFire.fires_cilent) {
            if (player.position().distanceTo(fire.blockPos.getCenter()) >= Minecraft.getInstance().options.renderDistance().get() * 480) continue;
            fire.renderFire(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
        }
    }
}