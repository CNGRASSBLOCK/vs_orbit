//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//
//public class AngularMomentumCalculation {
//    @SubscribeEvent
//    public void onServerTick(TickEvent.ServerTickEvent event) {
//        if (event.phase == TickEvent.Phase.START) return;
//        for (Player player : event.getServer().getPlayerList().getPlayers()) {
//            RotationState rotationState = new RotationState(new Quaternionf(0, 0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);
//            boolean b = player.getPersistentData().contains("RotationState");
//            if (!player.getPersistentData().contains("RotationState"))
//                player.getPersistentData().put("RotationState", rotationState.toCompoundTag());
//            rotationState = RotationState.getFormCompoundTag(player.getPersistentData().getCompound("RotationState"));
//            rotationState.applyAngularImpulse(new Vector3f(1, 2, 1));
//            updateRotation(rotationState);
//            player.getPersistentData().put("RotationState", rotationState.toCompoundTag());
//        }
//    }
//
//
//    private static void updateRotation(RotationState rotationState) {
//        rotationState.angularVelocity.set(rotationState.angularMomentum).div(rotationState.inertia);
//        float angle = rotationState.angularVelocity.length();
//        if (angle > 0.001f) {
//            Vector3f axis = new Vector3f(rotationState.angularVelocity).normalize();
//            Quaternionf deltaRot = new Quaternionf().fromAxisAngleRad(axis.x, axis.y, axis.z, angle * 0.05f);
//            rotationState.currentRotation.mul(deltaRot);
//            rotationState.currentRotation.normalize();
//        }
//        rotationState.angularMomentum.mul(0.95f);
//    }
//}
