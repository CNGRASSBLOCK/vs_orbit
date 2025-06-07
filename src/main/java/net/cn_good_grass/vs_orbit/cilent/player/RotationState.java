//package net.cn_good_grass.vs_orbit.cilent.player;
//
//import net.minecraft.nbt.CompoundTag;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//
//public class RotationState {
//    public final Quaternionf currentRotation;
//    public final Vector3f angularVelocity;
//    public final Vector3f angularMomentum;
//    public float inertia;
//
//    public RotationState(Quaternionf currentRotation1, Vector3f angularVelocity1, Vector3f angularMomentum1, float inertia1) {
//        currentRotation = currentRotation1;
//        angularVelocity = angularVelocity1;
//        angularMomentum = angularMomentum1;
//        inertia = inertia1;
//    }
//
//    public CompoundTag toCompoundTag() {
//        CompoundTag compoundTag = new CompoundTag();
//        CompoundTag QcompoundTag = new CompoundTag();
//        QcompoundTag.putFloat("x", currentRotation.x);
//        QcompoundTag.putFloat("y", currentRotation.y);
//        QcompoundTag.putFloat("z", currentRotation.z);
//        QcompoundTag.putFloat("w", currentRotation.w);
//        CompoundTag V1compoundTag = new CompoundTag();
//        V1compoundTag.putFloat("x", angularVelocity.x);
//        V1compoundTag.putFloat("y", angularVelocity.y);
//        V1compoundTag.putFloat("z", angularVelocity.z);
//        CompoundTag V2compoundTag = new CompoundTag();
//        V2compoundTag.putFloat("x", angularMomentum.x);
//        V2compoundTag.putFloat("y", angularMomentum.y);
//        V1compoundTag.putFloat("z", angularMomentum.z);
//        compoundTag.put("currentRotation", QcompoundTag);
//        compoundTag.put("angularVelocity", V1compoundTag);
//        compoundTag.put("angularMomentum", V2compoundTag);
//        compoundTag.putFloat("inertia", inertia);
//        return compoundTag;
//    }
//
//    public static RotationState getFormCompoundTag(CompoundTag compoundTag) {
//        if (compoundTag == null) return new RotationState(new Quaternionf(0, 0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);
//        return new RotationState(
//                new Quaternionf(
//                        compoundTag.getCompound("currentRotation").getFloat("x"),
//                        compoundTag.getCompound("currentRotation").getFloat("y"),
//                        compoundTag.getCompound("currentRotation").getFloat("z"),
//                        compoundTag.getCompound("currentRotation").getFloat("w")),
//                new Vector3f(
//                        compoundTag.getCompound("angularVelocity").getFloat("x"),
//                        compoundTag.getCompound("angularVelocity").getFloat("y"),
//                        compoundTag.getCompound("angularVelocity").getFloat("z")),
//                new Vector3f(
//                        compoundTag.getCompound("angularMomentum").getFloat("x"),
//                        compoundTag.getCompound("angularMomentum").getFloat("y"),
//                        compoundTag.getCompound("angularMomentum").getFloat("z")),
//                compoundTag.getFloat("inertia")
//        );
//    }
//
//    public Vector3f toEuler() { return this.currentRotation.getEulerAnglesYXZ(new Vector3f()); }
//
//    public void applyAngularImpulse(Vector3f impulse) { this.angularMomentum.add(impulse); }
//}