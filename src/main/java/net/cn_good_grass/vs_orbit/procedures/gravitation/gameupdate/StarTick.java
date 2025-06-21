//package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;
//
//import com.google.gson.JsonObject;
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import net.cn_good_grass.vs_orbit.config.Config;
//import net.cn_good_grass.vs_orbit.network.packet.SyncParticlePoolPacket;
//import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
//import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Particle;
//import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.theard.ParticlePool;
//import net.lointain.cosmos.network.CosmosModVariables;
//import net.lointain.cosmos.procedures.BrightnessProviderProcedure;
//import net.lointain.cosmos.procedures.CubeVertexOrientorProcedure;
//import net.lointain.cosmos.procedures.JsontomapconverterProcedure;
//import net.lointain.cosmos.procedures.SimpleOcclusionProviderProcedure;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.core.Direction;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.DoubleTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.Tag;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelAccessor;
//import net.minecraft.world.phys.Vec3;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.joml.Vector3d;
//
//import java.text.DecimalFormat;
//import java.util.*;
//
//
//@Mod.EventBusSubscriber
//public class StarTick {
//    @SubscribeEvent
//    public static void onWorldTick(TickEvent.ServerTickEvent event) {
//        for (String WorldIDs : Config.Gravitation_WORK_WORLD.get()) {
//            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldIDs)));
//            if (level == null) return;
//
//            ListTag StarData = StarAPI.getAllStarData(level);
//
//            for (Tag thisTag : StarData) {
//                CompoundTag thisData = (CompoundTag) thisTag;
//                if (thisData.contains("core_color")) continue;
//                Vec3 thisPos = StarAPI.getPos(level.dimension().location().toString(), 1, thisData);
//                for (Tag otherTag : StarData) {
//                    CompoundTag otherData = (CompoundTag) otherTag;
//                    if (otherData.contains("core_color")) continue;
//                    Vec3 otherPos = StarAPI.getPos(level.dimension().location().toString(), 1, otherData);
//
//                    if (thisPos.distanceTo(otherPos) < (thisData.getDouble("scale") + otherData.getDouble("scale"))) { //俩个东西撞一起了
//
//                    }
//                }
//            }
//        }
//    }
//
//    public static Vec3[] getPlaneXY(Vec3 A, Vec3 B) {
//        Vector3d pointA = new Vector3d(A.x, A.y, A.z);
//        Vector3d pointB = new Vector3d(B.x, B.y, B.z);
//        Vector3d midpoint = new Vector3d(pointA).add(pointB).mul(0.5);
//        Vector3d normal = new Vector3d(new Vector3d(pointB).sub(pointA)).normalize();
//        Vector3d basisX;
//        if (Math.abs(normal.x) > 0.0001 || Math.abs(normal.y) > 0.0001) basisX = new Vector3d(-normal.y, normal.x, 0).normalize(); else basisX = new Vector3d(1, 0, 0);
//        Vector3d basisY = new Vector3d();
//        normal.cross(basisX, basisY).normalize();
//
//        return new Vec3[]{new Vec3(basisX.x, basisX.y, basisX.z), new Vec3(basisY.x, basisY.y, basisY.z)};
//    }
//}
