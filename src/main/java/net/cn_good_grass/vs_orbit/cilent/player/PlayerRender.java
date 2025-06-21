//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.math.Axis;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderPlayerEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import org.joml.Quaterniond;
//import org.joml.Vector3d;
//
//@OnlyIn(Dist.CLIENT)
//public class PlayerRender {
//    @SubscribeEvent
//    public void onPlayerRender(RenderPlayerEvent.Pre event) {
//        PhysicalPlayer physicalPlayer = new PhysicalPlayer(event.getEntity());
//        Quaterniond quaterniond = physicalPlayer.getRotate();
//
//        PoseStack poseStack = event.getPoseStack();;
//        poseStack.pushPose();
//        poseStack.translate(0, 1.5, 0);
//        Vector3d vector3d = quaterniond.getEulerAnglesXYZ(new Vector3d(0, 0, 0));
//
//        poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(vector3d.x)));
//        poseStack.mulPose(Axis.YP .rotationDegrees((float) Math.toDegrees(vector3d.y)));
//        poseStack.mulPose(Axis.ZP.rotationDegrees((float) Math.toDegrees(vector3d.z)));
//        poseStack.translate(0, -1.5, 0);
//    }
//
//    @SubscribeEvent
//    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
//        event.getPoseStack().popPose();
//    }
//}