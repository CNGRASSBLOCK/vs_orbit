package net.cn_good_grass.vs_orbit.client.render.object.PlanetOverride;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.procedures.cosmos.WorldAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.AstronomicalPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlanetImpact {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldIDCilent(Minecraft.getInstance().level.dimension().location().toString(), false);
        if (astronomicalPool == null) return;

        Vector3d img_pos = WorldAPI.getTexturePosFormSpacePos(astronomicalPool.getAstronomical("CosmosStar-Earth"), Minecraft.getInstance().player.position());
        if (img_pos.y > 2 || img_pos.x > 4) return;
        img_pos.absolute();

        try {
            NativeImage image = NativeImage.read(Minecraft.getInstance().getResourceManager().getResource(resourceLocation).get().open());

            image.fillRect(0, 0, (int) (img_pos.x() * image.getWidth() / 8.0), (int) (img_pos.y() * image.getHeight() / 4.0), 0xFFFF0000);
            image.setPixelRGBA((int) (img_pos.x() * image.getWidth() / 8.0), (int) (img_pos.y() * image.getHeight() / 4.0), 0xFF00FF00);

            TextureReplace(image);
        } catch (IOException ignored) {}
    }

    private static ResourceLocation resourceLocation = new ResourceLocation("cosmos", "textures/earth.png");

    public static void TextureReplace(NativeImage img) { try {Minecraft.getInstance().getResourceManager().getResource(resourceLocation).ifPresent(resource -> Minecraft.getInstance().getTextureManager().register(resourceLocation, new DynamicTexture(img)));} catch (IllegalStateException | NoSuchElementException ignored) {} }
}
