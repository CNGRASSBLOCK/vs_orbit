package net.cn_good_grass.vs_orbit.cilent.render.object.Orbit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cn_good_grass.vs_orbit.config.ClientConfig;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.*;

@Mod.EventBusSubscriber(modid = "vs_orbit", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OrbitRender {
    @SubscribeEvent
    public static void renderLine(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES && Minecraft.getInstance().options.renderDebug) {;
            RenderSystem.disableDepthTest();
            RenderSystem.setShaderFogStart(2147463647);
            RenderSystem.setShaderFogEnd(2147463647);

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) return;

            Map<Astronomical, List<Vec3>> pos = new HashMap<>();
            for (AstronomicalPool one_pool: OrbitPrediction.getAllDataFromWorldID(minecraft.level.dimension().location().toString())) for (Astronomical astronomical : one_pool.getAllAstronomical()) if (pos.containsKey(astronomical)) pos.get(astronomical).add(new Vec3(astronomical.x, astronomical.y, astronomical.z));else pos.put(astronomical, new ArrayList<>(Arrays.stream(new Vec3[]{new Vec3(astronomical.x, astronomical.y, astronomical.z)}).toList()));

            for (Astronomical astronomical : pos.keySet()) {
                List<Vec3> pos_list = pos.get(astronomical);
                for (int i = 0; i < pos_list.size() - 1; i++)  renderLine(event, pos_list.get(i), pos_list.get(i + 1), ((int) (255 * (1 - i / 1.0 / pos_list.size())) << 24 | 0x00FFFF));
            }

            RenderSystem.enableDepthTest();
        }
    }

    public static void renderLine(RenderLevelStageEvent event, Vec3 start_pos, Vec3 end_pos, int color) {
        Vec3 pos = event.getCamera().getPosition();
        Vector3f normal = new Vec3(end_pos.x - start_pos.x, end_pos.y - start_pos.y, end_pos.z - start_pos.z).normalize().toVector3f();
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        vertexConsumer.vertex(event.getPoseStack().last().pose(), (float) (start_pos.x - pos.x()), (float) (start_pos.y - pos.y()), (float) (start_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
        vertexConsumer.vertex(event.getPoseStack().last().pose(), (float) (end_pos.x - pos.x()), (float) (end_pos.y - pos.y()), (float) (end_pos.z - pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }
}
