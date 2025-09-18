package net.cn_good_grass.vs_orbit.procedures.cosmos;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.cn_good_grass.vs_orbit.network.SyncDataTick;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomical;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.AstronomicalPool;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.VSOrbitDataPack;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Astronomicals.CosmosAstronomical;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.BrightnessProviderProcedure;
import net.lointain.cosmos.procedures.CubeVertexOrientorProcedure;
import net.lointain.cosmos.procedures.JsontomapconverterProcedure;
import net.lointain.cosmos.procedures.SimpleOcclusionProviderProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public abstract class StarAPI {
    @Nullable
    public static CosmosAstronomical getAstronomical(String dimension, double partialTick, CompoundTag StarTag, boolean isServer) { //星球更新
        AstronomicalPool newAstronomicalPool;
        AstronomicalPool oldAstronomicalPool;
        if (isServer) {
            newAstronomicalPool = AstronomicalPool.getFromWorldID(dimension);
            oldAstronomicalPool = newAstronomicalPool;
        } else {
            newAstronomicalPool = AstronomicalPool.getFromWorldIDCilent(dimension, false);
            oldAstronomicalPool = AstronomicalPool.getFromWorldIDCilent(dimension, true);
        }
        if (newAstronomicalPool == null || oldAstronomicalPool == null) return null;

        String StarID = "";
        if (StarTag.getString("function").contains("ring")) {
            if (!isServer) StarID = getStarIdFromRing(dimension, StarTag);
        } else {
            StarID = StarTag.getString("object_name");
        }

        String AstronomicalID = "CosmosStar-" + StarID;
        if (!(newAstronomicalPool.getAstronomical(AstronomicalID) instanceof CosmosAstronomical newAstronomical)) return null;
        if (!(oldAstronomicalPool.getAstronomical(AstronomicalID) instanceof CosmosAstronomical oldAstronomical)) return null;

        CosmosAstronomical astronomical = newAstronomical.copy();
        astronomical.x = Mth.lerp(partialTick, oldAstronomical.x, newAstronomical.x);
        astronomical.y = Mth.lerp(partialTick, oldAstronomical.y, newAstronomical.y);
        astronomical.z = Mth.lerp(partialTick, oldAstronomical.z, newAstronomical.z);

        return astronomical;
    }

    public static List<Object> changeOrder(Entity entity, double partialTick, ListTag map, double order, String dimension, Vec3 position) {
        if (map == null || dimension == null || position == null) return new ArrayList<>();
        Map<Object, Object> starting_map = new HashMap<>();
        List<Object> sorted_order = new ArrayList<>();
        for (int iter = 0; iter < map.size(); iter++) {
            CompoundTag mint = (CompoundTag) map.get(iter);
            CosmosAstronomical astronomical = getAstronomical(entity.level().dimension().location().toString(), partialTick, mint, false);
            Vec3 start_pos = new Vec3(0, 0, 0);
            if (astronomical != null) start_pos = new Vec3(astronomical.x, astronomical.y, astronomical.z);
            if (mint.contains("function") && mint.get("function").getAsString().equals("ring")) {
                Vec3 ring_pos = switch (mint.get("type").getAsString()) {
                    case "ring1" -> new Vec3(-((DoubleTag) mint.get("radius")).getAsDouble(), 0.0F, 0.0F);
                    case "ring2" -> new Vec3(((DoubleTag) mint.get("radius")).getAsDouble(), 0.0F, 0.0F);
                    case "ring3" -> new Vec3(0.0F, 0.0F, -((DoubleTag) mint.get("radius")).getAsDouble());
                    default -> new Vec3(0.0F, 0.0F, ((DoubleTag) mint.get("radius")).getAsDouble());
                };
                start_pos = start_pos.add(ring_pos.zRot(0.017453293F * (float)(((DoubleTag) mint.get("roll")).getAsDouble())).xRot(-0.017453293F * (float)((DoubleTag) mint.get("pitch")).getAsDouble()).yRot(0.017453293F * (float)(-((DoubleTag) mint.get("yaw")).getAsDouble())));
            }
            starting_map.put(position.distanceTo(start_pos), iter);
        }
        for (Object _listValueIterator : starting_map.keySet().stream().sorted().toList()) sorted_order.add(starting_map.get(_listValueIterator));
        if (order == (double)-1.0F) Collections.reverse(sorted_order);
        return sorted_order;
    }
    //光照计算
    public static Tag recalculateLight(CompoundTag instance, String pKey, Operation<Tag> original, LevelAccessor world, Entity entity, double partialTick) {
        if (!pKey.equals("light_data") && !pKey.equals("alpha_data") && !pKey.equals("i_alpha_data")) return original.call(instance, pKey);
        ListTag light_source_list = (ListTag) CosmosModVariables.WorldVariables.get(world).light_source_map.get(entity.level().dimension().location().toString());
        if (light_source_list != null) {
            for (int i = 0; i < light_source_list.size(); i++) {
                CosmosAstronomical astronomical = getAstronomical(entity.level().dimension().location().toString(), partialTick, light_source_list.getCompound(i), false);
                if (astronomical == null) return original.call(instance, pKey);
                CompoundTag compoundTag = light_source_list.getCompound(i).copy();
                compoundTag.putDouble("x", astronomical.x);
                compoundTag.putDouble("y", astronomical.y);
                compoundTag.putDouble("z", astronomical.z);
                light_source_list.set(i, compoundTag);
            }
        }
        ListTag opaque_object_list = (ListTag) CosmosModVariables.WorldVariables.get(world).opaque_object_map.get(entity.level().dimension().location().toString());
        CosmosAstronomical astronomical = getAstronomical(entity.level().dimension().location().toString(), partialTick, instance, false);
        if (astronomical == null) return original.call(instance, pKey);
        Vec3 objPos = new Vec3(astronomical.x, astronomical.y, astronomical.z);
        double scale;
        Vec3 objScale = Vec3.ZERO;
        if (!instance.get("function").getAsString().equals("ring")) {
            scale = ((DoubleTag) instance.get("scale")).getAsDouble();
            objScale = new Vec3(scale, scale, scale);
        }
        Vec3 objRot = new Vec3(((DoubleTag) instance.get("pitch")).getAsDouble(), ((DoubleTag) instance.get("yaw")).getAsDouble(), ((DoubleTag) instance.get("roll")).getAsDouble());
        int i = 0;
        switch (pKey) {
            case "light_data":
                JsonObject light_data = new JsonObject();
                if (instance.get("function").getAsString().equals("ring")) {
                    for (int seq = 0; seq < 4; seq++) {
                        float ring_rot = switch (instance.get("type").getAsString()) {
                            case "ring1" -> 0.0F;
                            case "ring2" -> 180.0F;
                            case "ring3" -> 270.0F;
                            default -> 90.0F;
                        };
                        float ring_scale = (float) ((DoubleTag) instance.get("scale_radius")).getAsDouble();
                        light_data.addProperty((new DecimalFormat("##.##")).format(seq * 4), SimpleOcclusionProviderProcedure.execute(light_source_list, objRot.x(), objRot.z(), ring_scale*0.25, objRot.y(), objPos, seq == 1 ? objPos.add((new Vec3(-0.5F, 0.0F, 0.0F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 2 ? objPos.add((new Vec3(-0.25F, 0.0F, 0.0F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 3 ? objPos.add((new Vec3(-0.25F, 0.0F, 0.0F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : objPos.add((new Vec3(-0.5F, 0.0F, 0.0F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y())))))));
                        light_data.addProperty((new DecimalFormat("##.##")).format(1 + seq * 4), SimpleOcclusionProviderProcedure.execute(light_source_list, objRot.x(), objRot.z(), ring_scale*0.25, objRot.y(), objPos, seq == 1 ? objPos.add((new Vec3(-0.5F, 0.0F, -0.5F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 2 ? objPos.add((new Vec3(-0.25F, 0.0F, 0.25F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 3 ? objPos.add((new Vec3(-0.25F, 0.0F, 0.25F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : objPos.add((new Vec3(-0.5F, 0.0F, -0.5F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y())))))));
                        light_data.addProperty((new DecimalFormat("##.##")).format(2 + seq * 4), SimpleOcclusionProviderProcedure.execute(light_source_list, objRot.x(), objRot.z(), ring_scale*0.25, objRot.y(), objPos, seq == 1 ? objPos.add((new Vec3(-0.25F, 0.0F, -0.25F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 2 ? objPos.add((new Vec3(-0.5F, 0.0F, 0.5F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 3 ? objPos.add((new Vec3(-0.5F, 0.0F, 0.5F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : objPos.add((new Vec3(-0.25F, 0.0F, -0.25F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y())))))));
                        light_data.addProperty((new DecimalFormat("##.##")).format(3 + seq * 4), SimpleOcclusionProviderProcedure.execute(light_source_list, objRot.x(), objRot.z(), ring_scale*0.25, objRot.y(), objPos, seq == 1 ? objPos.add((new Vec3(-0.25F, 0.0F, 0.0F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 2 ? objPos.add((new Vec3(-0.5F, 0.0F, 0.0F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : (seq == 3 ? objPos.add((new Vec3(-0.5F, 0.0F, 0.0F)).xRot(-(float)Math.PI).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y()))) : objPos.add((new Vec3(-0.25F, 0.0F, 0.0F)).yRot(0.017453293F * ring_rot).scale(ring_scale).zRot(-0.017453292F * (float)(-objRot.z())).xRot(-0.017453292F * (float)objRot.x()).yRot(0.017453293F * (float)(-objRot.y())))))));
                    }
                    return JsontomapconverterProcedure.execute(light_data);
                }
                for (Direction directioniterator : Direction.values()) {
                    Vec3 direction_vector = new Vec3(directioniterator.step());
                    light_data.addProperty((new DecimalFormat("##.##")).format(i * (double)4.0F), BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "color", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, -0.5F))));
                    light_data.addProperty((new DecimalFormat("##.##")).format((double)1.0F + i * (double)4.0F), BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "color", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, 0.5F))));
                    light_data.addProperty((new DecimalFormat("##.##")).format((double)2.0F + i * (double)4.0F), BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "color", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, 0.5F))));
                    light_data.addProperty((new DecimalFormat("##.##")).format((double)3.0F + i * (double)4.0F), BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "color", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, -0.5F))));
                    i++;
                }
                return JsontomapconverterProcedure.execute(light_data);
            case "alpha_data":
                JsonObject transparency_data = new JsonObject();
                for (Direction directioniterator : Direction.values()) {
                    Vec3 direction_vector = new Vec3(directioniterator.step());
                    transparency_data.addProperty((new DecimalFormat("##.##")).format(i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, -0.5F))) >>> 24);
                    transparency_data.addProperty((new DecimalFormat("##.##")).format((double)1.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, 0.5F))) >>> 24);
                    transparency_data.addProperty((new DecimalFormat("##.##")).format((double)2.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, 0.5F))) >>> 24);
                    transparency_data.addProperty((new DecimalFormat("##.##")).format((double)3.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, -0.5F))) >>> 24);
                    i++;
                }
                return JsontomapconverterProcedure.execute(transparency_data);
            case "i_alpha_data":
                JsonObject i_alpha_data = new JsonObject();
                for (Direction directioniterator : Direction.values()) {
                    Vec3 direction_vector = new Vec3(directioniterator.step());
                    i_alpha_data.addProperty((new DecimalFormat("##.##")).format(i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "i_alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, -0.5F))) >>> 24);
                    i_alpha_data.addProperty((new DecimalFormat("##.##")).format((double)1.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "i_alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(-0.5F, 0.5F, 0.5F))) >>> 24);
                    i_alpha_data.addProperty((new DecimalFormat("##.##")).format((double)2.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "i_alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, 0.5F))) >>> 24);
                    i_alpha_data.addProperty((new DecimalFormat("##.##")).format((double)3.0F + i * (double)4.0F), (int)BrightnessProviderProcedure.execute(light_source_list, opaque_object_list, -1.0F, "i_alpha", "none", direction_vector, objPos, objRot, objScale, CubeVertexOrientorProcedure.execute(directioniterator, 0.0F, new Vec3(0.5F, 0.5F, -0.5F))) >>> 24);
                    i++;
                }
                return JsontomapconverterProcedure.execute(i_alpha_data);
            default: return original.call(instance, pKey);
        }
    }
    //杂七杂八的
    @OnlyIn(Dist.CLIENT)
    public static String getStarIdFromRing(String dimension, CompoundTag RingTag) {
        if (!RingTag.getString("function").equals("ring")) return "";

        Minecraft minecraft = Minecraft.getInstance();
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimension));
        if (!(minecraft.level != null && minecraft.level.dimension() == dimensionKey)) return "";
        ClientLevel world = minecraft.level;

        ListTag listtag = StarAPI.getAllStarData(world, true);
        if (listtag == null) return "";

        for (int i = 0 ; i < listtag.size() ; i++) {
            CompoundTag compoundTag = listtag.getCompound(i);
            Vec3 pos = new Vec3(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"));
            Vec3 thispos = new Vec3(RingTag.getDouble("x"), RingTag.getDouble("y"), RingTag.getDouble("z"));
            if (pos.equals(thispos)) return compoundTag.getString("object_name");
        }
        return "";
    }

    private final static Map<String, ListTag> AllStarDataSave = new HashMap<>();
    public static ListTag getAllStarData(Level world, boolean hasStar) {
        String WorldId = world.dimension().location().toString();
        if (AllStarDataSave.containsKey(WorldId + ";" + hasStar)) {
            return AllStarDataSave.get(WorldId + ";" + hasStar);
        } else {
            ListTag listtag = new ListTag();
            CosmosModVariables.WorldVariables worldVars = CosmosModVariables.WorldVariables.get(world);

            if (!worldVars.opaque_object_map.contains(WorldId)) return listtag;
            Tag opaque_object_map = worldVars.opaque_object_map.get(WorldId); //星球数据
            Tag light_source_map = worldVars.light_source_map.get(WorldId); //恒星数据
            //找黑洞的
            Tag render_data_map = worldVars.render_data_map.get(WorldId);
            if (render_data_map instanceof ListTag listTag) for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag compoundTag = listTag.getCompound(i);
                    if (compoundTag.getString("type").equals("blackhole")) listtag.add(compoundTag);
            }

            if (opaque_object_map instanceof ListTag listTag) listtag.addAll(listTag.copy());
            if (hasStar) if (light_source_map instanceof ListTag listTag) listtag.addAll(listTag.copy());
            if (listtag.isEmpty()) return listtag;

            AllStarDataSave.put(WorldId + ";" + hasStar, listtag);

            return listtag;
        }
    }

    @Nullable public static CompoundTag getStarDataFormLevel(Level world) {
        for (String WorldId : VSOrbitDataPack.OrbitWorld) {
            ServerLevel level = world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldId)));
            if (level == null) continue;
            ListTag data = StarAPI.getAllStarData(level, false);
            if (data == null) continue;
            for (int i = 0; i < data.size(); i++) {
                CompoundTag StarTag = data.getCompound(i);
                if (StarTag.getString("travel_to").equals(world.dimension().location().toString())) return StarTag;
            }
        }
        return null;
    }

    @Nullable public static Astronomical getAstronomicalFormLevel(Level world) {
        if (!world.isClientSide) {
            for (String WorldId : VSOrbitDataPack.OrbitWorld) {
                Level level = world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(WorldId)));
                if (level == null) continue;
                ListTag data = StarAPI.getAllStarData(level, false);
                if (data == null) continue;
                for (int i = 0; i < data.size(); i++) {
                    CompoundTag StarTag = data.getCompound(i);
                    if (StarTag.getString("travel_to").equals(world.dimension().location().toString())) {
                        AstronomicalPool astronomicalPool = AstronomicalPool.getFromWorldID(WorldId);
                        if (astronomicalPool == null) continue;
                        return astronomicalPool.getAstronomical("CosmosStar-" + StarTag.getString("object_name"));
                    }
                }
            }
        } else {
            for (AstronomicalPool astronomicalPool : SyncDataTick.New_Gravitation_Core_World_Bus) for (Astronomical astronomical : astronomicalPool.getAllAstronomical()) if (astronomical.Tag.getCompound("cosmos:data").getString("travel_to").equals(world.dimension().location().toString())) return astronomical;
        }
        return null;
    }
}
