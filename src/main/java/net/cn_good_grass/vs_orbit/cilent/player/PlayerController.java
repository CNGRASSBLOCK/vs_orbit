package net.cn_good_grass.vs_orbit.cilent.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PlayerController {
//    @SubscribeEvent
//    public void onPlayerRender(RenderPlayerEvent.Pre event) {
//        PoseStack poseStack = event.getPoseStack();
//
//        poseStack.pushPose();
//        poseStack.translate(0, 1.5, 0);
//        Vector3f rotate = SpaceController.玩家旋转.getEulerAnglesXYZ(new Vector3f(0, 0, 0));
//
//        poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(rotate.x)));
//        poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(rotate.y)));
//        poseStack.mulPose(Axis.ZP.rotationDegrees((float) Math.toDegrees(rotate.z)));
//        poseStack.translate(0, -1.5, 0);
//    }
//
//    @SubscribeEvent
//    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
//        event.getPoseStack().popPose(); // 恢复矩阵状态
//    }
}
