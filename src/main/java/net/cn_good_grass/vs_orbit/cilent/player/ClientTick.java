//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
//import net.cn_good_grass.vs_orbit.config.Config;
//import net.minecraft.client.Minecraft;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.joml.Quaterniond;
//import org.joml.Vector3d;
//
//@Mod.EventBusSubscriber(modid = "vs_orbit", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//public class ClientTick {
//    @SubscribeEvent
//    public static void ClientTick(TickEvent.ClientTickEvent event) {
//        if (event.phase != TickEvent.Phase.START) return;
//        Minecraft mc = Minecraft.getInstance();
//        Player player = mc.player;
//        if (player == null) return;
//        if (!Config.Gravitation_WORK_WORLD.get().contains(player.level().dimension().location().toString())) return;
//
//        PhysicalPlayer physicalPlayer = new PhysicalPlayer(player);
//        physicalPlayer.SetRotate(new Quaterniond(0,0,1,0));
//
//        System.out.println(physicalPlayer.getCameraRotate().getEulerAnglesXYZ(new Vector3d()));
//        if (player.getXRot() == 90f) {
//            physicalPlayer.RotateTo(physicalPlayer.getRotate().rotateY(Math.toRadians(player.getYHeadRot())).rotateX(90));
//        } else if (player.getXRot() == -90f) {
//            physicalPlayer.RotateTo(physicalPlayer.getRotate().rotateY(Math.toRadians(player.getYHeadRot())).rotateX(90).invert());
//        }
//    }
//}
