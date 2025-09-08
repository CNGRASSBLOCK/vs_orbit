package net.cn_good_grass.vs_orbit.client.render.object.Astronomical;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class WorldPosToScreen {
    public static Vector3d worldToScreen(Vector3d worldPos, PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return new Vector3d();
        Matrix4d projectionMatrix = new Matrix4d(RenderSystem.getProjectionMatrix());
        Matrix4d viewMatrix = new Matrix4d(poseStack.last().pose());

        Vector4d clipSpacePos = new Vector4d(worldPos, 1.0f);
        viewMatrix.transform(clipSpacePos);
        projectionMatrix.transform(clipSpacePos);
        if (clipSpacePos.w <= 0) return null;
        clipSpacePos.div(clipSpacePos.w);

        return new Vector3d((clipSpacePos.x * 0.5f + 0.5f) * screen.width, (1.0f - (clipSpacePos.y * 0.5f + 0.5f)) * screen.height, clipSpacePos.z);
        }

}
