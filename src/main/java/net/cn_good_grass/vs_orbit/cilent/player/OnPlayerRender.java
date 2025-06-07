//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.math.Axis;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderPlayerEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//
//@OnlyIn(Dist.CLIENT)
//public class OnPlayerRender {
//    @SubscribeEvent
//    public void onPlayerRender(RenderPlayerEvent.Pre event) {
//        Player player = event.getEntity();
//        RotationState rotationState = RotationState.getFormCompoundTag((CompoundTag) player.getPersistentData().get("RotationState"));
//        PoseStack poseStack = event.getPoseStack();;
//        // 在模型渲染前应用旋转
//        poseStack.pushPose();
//        poseStack.translate(0, 1.5, 0); // 移动到模型中心（玩家高度约1.8格）
//        poseStack.mulPose(Axis.XP.rotationDegrees(rotationState.toEuler().x));
//        poseStack.mulPose(Axis.YP.rotationDegrees(rotationState.toEuler().y));
//        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationState.toEuler().z));
//        poseStack.translate(0, -1.5, 0); // 移回原位
//    }
//
//    @SubscribeEvent
//    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
//        event.getPoseStack().popPose(); // 恢复矩阵状态
//    }
//}