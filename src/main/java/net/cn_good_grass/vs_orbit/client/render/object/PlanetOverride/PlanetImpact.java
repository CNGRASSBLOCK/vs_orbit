package net.cn_good_grass.vs_orbit.client.render.object.PlanetOverride;

import com.mojang.blaze3d.platform.NativeImage;
import net.cn_good_grass.vs_orbit.procedures.cosmos.WorldAPI;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;

import java.io.IOException;
import java.util.NoSuchElementException;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlanetImpact {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
//        if (event.phase != TickEvent.Phase.START || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;
//
//        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldIDCilent(Minecraft.getInstance().level.dimension().location().toString(), false);
//        if (astronomicalPool == null) return;
//
//        CosmosAstronomical astronomical = astronomicalPool.getAstronomical("CosmosStar-Earth");
//        if (astronomicalPool == null) return;
//        Vector3d img_pos = WorldAPI.getTexturePosFormSpacePos(, Minecraft.getInstance().player.position());
//        if (img_pos.y > 2 || img_pos.x > 4) return;
//        img_pos.absolute();
//
//        ResourceLocation textureLoc = new ResourceLocation("cosmos:textures/earth.png");
//        NativeImage image = new NativeImage(1024, 512, false);
//
//        image.fillRect(0, 0, (int) (img_pos.x() * image.getWidth() / 8.0), (int) (img_pos.y() * image.getHeight() / 4.0), 0xFFFF0000);
//        image.setPixelRGBA((int) (img_pos.x() * image.getWidth() / 8.0), (int) (img_pos.y() * image.getHeight() / 4.0), 0xFF00FF00);
//
//        DynamicTexture dynamicTexture = new DynamicTexture(image);
//        Minecraft.getInstance().getTextureManager().register(textureLoc, dynamicTexture);    }
    }
}
