//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3;
//import org.joml.Quaterniond;
//import org.joml.Vector3d;
//
//public class PhysicalPlayer {
//    private final Player player;
//
//    public PhysicalPlayer(Player player) { this.player = player; }
//
//    public void SetRotate(Quaterniond quaterniond) {
//        CompoundTag newData = new CompoundTag();
//        {
//            newData.putDouble("x", quaterniond.x);
//            newData.putDouble("y", quaterniond.y);
//            newData.putDouble("z", quaterniond.z);
//            newData.putDouble("w", quaterniond.w);
//        }
//        player.getPersistentData().put("PlayerQuaterniond", newData);
//    }
//
//    public Quaterniond getRotate() {
//        Quaterniond PlayerQuaterniond = new Quaterniond(0, 1, 0, 0);
//        if (player.getPersistentData().contains("PlayerQuaterniond")) {
//            CompoundTag data = player.getPersistentData().getCompound("PlayerQuaterniond");
//            PlayerQuaterniond = new Quaterniond(data.getDouble("x"), data.getDouble("y"), data.getDouble("z"), data.getDouble("w"));
//        } else {
//            CompoundTag newData = new CompoundTag();
//            {
//                newData.putDouble("x", 0);
//                newData.putDouble("y", 1);
//                newData.putDouble("z", 0);
//                newData.putDouble("w", 0);
//            }
//            player.getPersistentData().put("PlayerQuaterniond", newData);
//        }
//        return PlayerQuaterniond;
//    }
//
//    public Quaterniond getCameraRotate() {
//        Quaterniond PlayerQuaterniond = this.getRotate();
//
//        Vector3d euler = new Vector3d();
//        PlayerQuaterniond.getEulerAnglesXYZ(euler);
//
//        double cameraAngleDeg = Math.toDegrees(euler.y);
//
//        double cameraAngleRad = Math.toRadians(cameraAngleDeg);
//        double halfAngle = cameraAngleRad / 2.0;
//        double sinHalf = Math.sin(halfAngle);
//        double cosHalf = Math.cos(halfAngle);
//
//        return new Quaterniond(sinHalf, 0, 0, cosHalf);
//    }
//
//    public void RotateTo(Quaterniond quaterniond) {
//        Quaterniond PlayerQuaterniond = this.getRotate();
//        PlayerQuaterniond.slerp(quaterniond, 0.1);
//
//        CompoundTag newData = new CompoundTag();
//        {
//            newData.putDouble("x", PlayerQuaterniond.x);
//            newData.putDouble("y", PlayerQuaterniond.y);
//            newData.putDouble("z", PlayerQuaterniond.z);
//            newData.putDouble("w", PlayerQuaterniond.w);
//        }
//        player.getPersistentData().put("PlayerQuaterniond", newData);
//    }
//
//    public Vec3 getMoveDirection() {
//        Quaterniond PlayerQuaterniond = this.getRotate();
//        return new Vec3(PlayerQuaterniond.x * player.getSpeed(), PlayerQuaterniond.y * player.getSpeed(), PlayerQuaterniond.z * player.getSpeed());
//    }
//}
