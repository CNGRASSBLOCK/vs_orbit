package net.cn_good_grass.vs_orbit.cilent.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class OnPlayerRender {
    private static Quaterniond quaterniond;

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.getPersistentData().contains("Quaterniond")) {
            CompoundTag compoundTag = player.getPersistentData().getCompound("Quaterniond");
            quaterniond = new Quaterniond(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"), compoundTag.getDouble("w"));
        } else return;

        PoseStack poseStack = event.getPoseStack();;
        // 在模型渲染前应用旋转
        poseStack.pushPose();
        poseStack.translate(0, 1.5, 0);
        Vector3d vector3d = quaterniond.getEulerAnglesXYZ(new Vector3d(0, 0, 0));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) vector3d.x));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) vector3d.y));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) vector3d.z));
        poseStack.translate(0, -1.5, 0); // 移回原位
    }

    @SubscribeEvent
    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        event.getPoseStack().popPose(); // 恢复矩阵状态
    }
}