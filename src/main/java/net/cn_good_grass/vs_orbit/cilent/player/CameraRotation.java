package net.cn_good_grass.vs_orbit.cilent.player;

import net.minecraft.client.Camera;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@OnlyIn(Dist.CLIENT)
//public class CameraRotation {
//    @SubscribeEvent
//    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
//        float roll = (float) Math.toRadians((System.currentTimeMillis() % 7200 / 10.0F) % 360 * 15); // 滚动角
//        event.setRoll(roll); // 1.20.1+ 支持Roll旋转
//    }
//}
