package net.cn_good_grass.vs_orbit.cilent.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpaceController {
    public static Quaternionf 玩家旋转 = new Quaternionf();
    public static Quaternionf 摄像机旋转 = new Quaternionf();

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        Player 玩家 = Minecraft.getInstance().player;
        if (玩家 == null) return;


        Vector3f 玩家原来的旋转 = 玩家旋转.getEulerAnglesXYZ(new Vector3f());
        玩家旋转 = 玩家旋转.identity();
        玩家旋转 = 玩家旋转.rotateLocalY((float) -Math.toRadians(玩家.yBodyRot));
        玩家旋转 = 玩家旋转.rotateLocalX(玩家原来的旋转.x);
        玩家旋转 = 玩家旋转.rotateLocalZ(玩家原来的旋转.z);

        摄像机旋转 = 玩家旋转;
        摄像机旋转 = 摄像机旋转.rotateLocalY((float) -Math.toRadians(玩家.yHeadRot));

        if ((玩家.xRotO * -1) > 45) {
            玩家.setXRot(-45);
            摄像机旋转 = 摄像机旋转.rotateLocalX(45);
        } else if ((玩家.xRotO * -1) < -45) {
            玩家.setXRot(45);
            摄像机旋转 = 摄像机旋转.rotateLocalX(-45);
        }

        double step = ((double) 1 / Minecraft.getInstance().getFps()) * 0.5;
        if (step != 0) 玩家旋转 = 玩家旋转.slerp(摄像机旋转, (float) step);
    }
}
