package net.cn_good_grass.vs_orbit.procedures.gravitation.gameupdate;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.GravitationPool;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.Particle;
import net.cn_good_grass.vs_orbit.network.VariablesUpdate;
import net.lointain.cosmos.network.CosmosModVariables;
import net.lointain.cosmos.procedures.BrightnessProviderProcedure;
import net.lointain.cosmos.procedures.CubeVertexOrientorProcedure;
import net.lointain.cosmos.procedures.JsontomapconverterProcedure;
import net.lointain.cosmos.procedures.SimpleOcclusionProviderProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3d;

import java.text.DecimalFormat;
import java.util.*;

public class StarTick {
    public static Vec3 getPos(String dimension, double partialTick, CompoundTag StarTag) { //星球更新
        String WorldID = dimension;

        GravitationPool newGravitationPool = null;
        for (GravitationPool gravitationPool : VariablesUpdate.New_Gravitation_Core_World_Bus) {
            if (gravitationPool.WorldId.equals(WorldID)) {
                newGravitationPool = gravitationPool;
                break;
            }
        }
        GravitationPool oldGravitationPool = null;
        for (GravitationPool gravitationPool : VariablesUpdate.Old_Gravitation_Core_World_Bus) {
            if (gravitationPool.WorldId.equals(WorldID)) {
                oldGravitationPool = gravitationPool;
                break;
            }
        }

        if (newGravitationPool == null || oldGravitationPool == null) { return new Vec3(0, 0, 0); }
        if (newGravitationPool.Gravitation_Core_World.size() != oldGravitationPool.Gravitation_Core_World.size()) { return new Vec3(0, 0, 0); }

        String StarID = StarTag.getString("object_name");
        if (StarID.isEmpty()) { return new Vec3(0, 0, 0); }
        String ParticleID = "CosmosStar-" + StarID;
        Vector3d New_Pos = new Vector3d(0, 0, 0);
        Vector3d Old_Pos = new Vector3d(0, 0, 0);
        for (int i = 0; i < newGravitationPool.Gravitation_Core_World.size(); i++) {
            Particle NewParticle = newGravitationPool.Gravitation_Core_World.get(i);
            Particle OldPatricle = oldGravitationPool.Gravitation_Core_World.get(i);
            if (NewParticle.name.equals(ParticleID) && OldPatricle.name.equals(ParticleID)) {
                New_Pos = new Vector3d(NewParticle.x, NewParticle.y, NewParticle.z);
                Old_Pos = new Vector3d(OldPatricle.x, OldPatricle.y, OldPatricle.z);
            }
        }
        return new Vec3(Mth.lerp(partialTick, Old_Pos.x, New_Pos.x), Mth.lerp(partialTick, Old_Pos.y, New_Pos.y), Mth.lerp(partialTick, Old_Pos.z, New_Pos.z));
    }

    public static List<Object> changeOrder(LevelAccessor world, Entity entity, double partialTick, ListTag map, double order, String dimension, Vec3 position) {
        if (map == null || dimension == null || position == null)
            return new ArrayList<>();
        Map<Object, Object> starting_map = new HashMap<>();
        List<Object> sorted_order = new ArrayList<>();
        for (int iter = 0; iter < map.size(); iter++) {
            CompoundTag mint = (CompoundTag) map.get(iter);
            Vec3 start_pos = getPos(entity.level().dimension().location().toString(), partialTick, mint);
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

    public static Tag recalculateLight(CompoundTag instance, String pKey, Operation<Tag> original, LevelAccessor world, Entity entity, double partialTick) {
        if (!pKey.equals("light_data") && !pKey.equals("alpha_data") && !pKey.equals("i_alpha_data")) return original.call(instance, pKey);
        ListTag light_source_list = (ListTag) CosmosModVariables.WorldVariables.get(world).light_source_map.get(entity.level().dimension().location().toString());
        if (light_source_list != null) {
            for (int i = 0; i < light_source_list.size(); i++) {
                Vec3 objPos = getPos(entity.level().dimension().location().toString(), partialTick, light_source_list.getCompound(i));
                CompoundTag compoundTag = light_source_list.getCompound(i).copy();
                compoundTag.putDouble("x", objPos.x);
                compoundTag.putDouble("y", objPos.y);
                compoundTag.putDouble("z", objPos.z);
                light_source_list.set(i, compoundTag);
            }
        }
        ListTag opaque_object_list = (ListTag) CosmosModVariables.WorldVariables.get(world).opaque_object_map.get(entity.level().dimension().location().toString());
        Vec3 objPos = getPos(entity.level().dimension().location().toString(), partialTick, instance);
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
            default:
                return original.call(instance, pKey);
        }
    }
    //杂七杂八的
    public static float getPartialTick(LevelAccessor world) {
        return world.isClientSide() ? getClientPartialTick() : 0;
    }

    @OnlyIn(Dist.CLIENT)
    private static float getClientPartialTick() {
        return Minecraft.getInstance().getPartialTick();
    }
}
