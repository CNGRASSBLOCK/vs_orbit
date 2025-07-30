//package net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.gameupdate;
//
//import net.cn_good_grass.vs_orbit.config.Config;
//import net.cn_good_grass.vs_orbit.procedures.cosmos.StarAPI;
//import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Astronomical;
//import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.physics.Force;
//import net.cn_good_grass.vs_orbit.procedures.vs_orbit.gravitation.classes.theard.AstronomicalPool;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.Tag;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.phys.Vec3;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.joml.Quaterniond;
//import org.joml.Vector3d;
//
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
//            ListTag StarData = StarAPI.getAllStarData(level, false);
//            if (StarData == null) return;
//
//            for (Tag thisTag : StarData) {
//                CompoundTag thisData = (CompoundTag) thisTag;
//                if (thisData.contains("core_color")) continue;
//                Vec3 thisPos = StarAPI.getPos(level.dimension().location().toString(), 1, thisData, true);
//                if (thisPos.equals(new Vec3(0 , 0, 0))) continue;
//                for (Tag otherTag : StarData) {
//                    CompoundTag otherData = (CompoundTag) otherTag;
//                    if (thisData.equals(otherData)) continue;
//
//                    if (otherData.contains("core_color")) continue;
//                    Vec3 otherPos = StarAPI.getPos(level.dimension().location().toString(), 1, otherData, true);
//
//                    if (thisPos.distanceTo(otherPos) < ((thisData.getDouble("scale") + otherData.getDouble("scale")) / 1.5)) { //俩个东西撞一起了
//                        if (create) continue;
//
//                        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(level.dimension().location().toString());
//                        if (astronomicalPool == null) continue;
//                        Astronomical thisPlanet = astronomicalPool.getAstronomical("CosmosStar-" + thisData.getString("object_name"));
//                        Astronomical otherPlanet = astronomicalPool.getAstronomical("CosmosStar-" + otherData.getString("object_name"));
//
//                        Astronomical astronomical;
//                        if (thisPlanet.mass > otherPlanet.mass) astronomical = thisPlanet; else astronomical = otherPlanet;
//                        Vec3 centerPos = thisPos.add(otherPos.subtract(thisPos).scale(thisData.getDouble("scale") / (thisData.getDouble("scale") + otherData.getDouble("scale"))));
//                        Vector3d[] planet = getPlaneXY(thisPos, otherPos);
//                        planet[0] = new Vector3d(centerPos.x, centerPos.y, centerPos.z);
//
//                        CreateSplinterFromPlanet(astronomicalPool, astronomical, planet, 64);
//
//                        thisPlanet.x_speed = thisPlanet.x_speed * 0.1;
//                        thisPlanet.y_speed = thisPlanet.y_speed * 0.1;
//                        thisPlanet.z_speed = thisPlanet.z_speed * 0.1;
//
//                        otherPlanet.x_speed = otherPlanet.x_speed * 0.1;
//                        otherPlanet.y_speed = otherPlanet.y_speed * 0.1;
//                        otherPlanet.z_speed = otherPlanet.z_speed * 0.1;
//
//                        create = true;
//                    }
//                }
//            }
//        }
//    }
//
//    private static boolean create = true;
//    public static void CreateSplinterFromPlanet(AstronomicalPool astronomicalPool, Astronomical mainPlanet, Vector3d[] planet, int quantity) {
//        if (planet.length != 3) return;
//        planet[1].normalize();
//        planet[2].normalize();
//        for (int i = 0;i <= quantity;i++) {
//            int mass = (int) (Math.random() * 20000);
//            Astronomical astronomical = new Astronomical(astronomicalPool.size(), "Splinter-" + astronomicalPool.size(), "vs_orbit:splinter", false, mass, planet[0].x, planet[0].y, planet[0].z, new Quaterniond(Math.random(), Math.random(), Math.random(), Math.random()));
//            Vector3d Force = new Vector3d(planet[1]).mul((Math.random() * 2 - 1)).add(new Vector3d(planet[2]).mul((Math.random() * 2 - 1))).mul((double) mass / 5);
//            astronomical.x_speed = mainPlanet.x_speed;
//            astronomical.y_speed = mainPlanet.y_speed;
//            astronomical.z_speed = mainPlanet.z_speed;
//            astronomical.addForce(new Force(("Collide" + System.currentTimeMillis()), Force.x, Force.y, Force.z, 10));
//            astronomicalPool.addAstronomical(astronomical);
//        }
//    }
//
//    public static Vector3d[] getPlaneXY(Vec3 A, Vec3 B) {
//        Vector3d pointA = new Vector3d(A.x, A.y, A.z);
//        Vector3d pointB = new Vector3d(B.x, B.y, B.z);
//        Vector3d abVector = new Vector3d(pointB).sub(pointA);
//        Vector3d midpoint = new Vector3d(pointA).add(pointB).mul(0.5);
//        Vector3d normal = new Vector3d(abVector).normalize();
//        Vector3d basisX;
//        if (Math.abs(normal.x) > 0.0001 || Math.abs(normal.y) > 0.0001) basisX = new Vector3d(-normal.y, normal.x, 0).normalize(); else basisX = new Vector3d(1, 0, 0);
//        Vector3d basisY = new Vector3d();
//        normal.cross(basisX, basisY).normalize();
//        return new Vector3d[]{midpoint, basisX, basisY};
//    }
//}
