//package net.cn_good_grass.vs_orbit.client.render.object.Astronomical;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.renderer.LightTexture;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RenderGuiEvent;
//import net.minecraftforge.client.event.ScreenEvent;
//import net.minecraftforge.eventbus.api.Event;
//import net.minecraftforge.eventbus.api.EventPriority;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.joml.Matrix4f;
//import org.joml.Vector3d;
//
//import javax.annotation.Nullable;
//
//@Mod.EventBusSubscriber(value = Dist.CLIENT)
//public class DisplayData {
//    private static void render(@Nullable Event event) {
//        if (target(2)) {
//            for (String astronomical_name : DisplayLine.ScreenPos.keySet()) {
//                Vector3d pos = DisplayLine.ScreenPos.get(astronomical_name);
//                if (pos == null) continue;
//                pos.mul(0.25);
//
//                renderTexts(astronomical_name, (float) pos.x(), (float) pos.y(), 0, 0, 1, 255 << 24 | 255 << 16 | 255 << 8 | 255, 4);
//            }
//            release();
//        }
//    }
//
//
//
//    private static GuiGraphics guiGraphics = null;
//    private static int currentStage = 0;
//    private static int targetStage = 0; // NONE: 0, ALWAYS: 1, GAME: 2, GUI: 3
//    private static boolean target(int targetStage) {
//        if (targetStage == currentStage) {
//            DisplayData.targetStage = targetStage;
//            return true;
//        } else if (targetStage == 1) {
//            if (currentStage != 0) {
//                DisplayData.targetStage = currentStage;
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static void release() { targetStage = 0; }
//
//    public static void renderTexts(String texts, float x, float y, float depth, float angle, float scale, int color, int alignment) {
//        if (currentStage == 0 || currentStage != targetStage) return;
//        Font font = Minecraft.getInstance().font;
//        float offsetX = 0.0F, offsetY = 0.0F;
//        switch (alignment) {
//            case 0 :
//                offsetX = (font.width(texts) - 1) * 0.5F;
//                offsetY = (font.lineHeight - 1) * 0.5F;
//                break;
//            case 1 :
//                offsetY = (font.lineHeight - 1) * 0.5F;
//                break;
//            case 2 :
//                offsetX = (font.width(texts) - 1) * -0.5F;
//                offsetY = (font.lineHeight - 1) * 0.5F;
//                break;
//            case 3 :
//                offsetX = (font.width(texts) - 1) * 0.5F;
//                break;
//            case 4 :
//                break;
//            case 5 :
//                offsetX = (font.width(texts) - 1) * -0.5F;
//                break;
//            case 6 :
//                offsetX = (font.width(texts) - 1) * 0.5F;
//                offsetY = (font.lineHeight - 1) * -0.5F;
//                break;
//            case 7 :
//                offsetY = (font.lineHeight - 1) * -0.5F;
//                break;
//            case 8 :
//                offsetX = (font.width(texts) - 1) * -0.5F;
//                offsetY = (font.lineHeight - 1) * -0.5F;
//                break;
//        }
//        PoseStack poseStack = guiGraphics.pose();
//        poseStack.pushPose();
//        poseStack.translate(x + offsetX * scale, y + offsetY * scale, -depth);
//        poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(angle));
//        poseStack.scale(scale, scale, 1.0F);
//        poseStack.translate((font.width(texts) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, 0.0F);
//        Matrix4f matrix4f = poseStack.last().pose();
//        font.drawInBatch(texts, 0.0F, 0.0F, color, false, matrix4f, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
//        poseStack.popPose();
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void renderGUI(RenderGuiEvent.Pre event) {
//        currentStage = 2;
//        guiGraphics = event.getGuiGraphics();
//        renderOverlays(event);
//        currentStage = 0;
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void renderScreen(ScreenEvent.Render.Post event) {
//        currentStage = 3;
//        guiGraphics = event.getGuiGraphics();
//        renderOverlays(event);
//        currentStage = 0;
//    }
//
//    private static void renderOverlays(Event event) {
//        Minecraft minecraft = Minecraft.getInstance();
//        double scale = minecraft.getWindow().getGuiScale();
//        if (scale > 0.0D) {
//            RenderSystem.depthMask(true);
//            RenderSystem.enableDepthTest();
//            RenderSystem.disableCull();
//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            render(event);
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.disableBlend();
//            RenderSystem.enableCull();
//            RenderSystem.enableDepthTest();
//            RenderSystem.depthMask(true);
//        }
//    }
//
//}
