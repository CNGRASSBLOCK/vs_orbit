package net.cn_good_grass.vs_orbit.client.render.object.PlanetEngine;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class UpdataPlanetEngineFire {
    @SubscribeEvent
    public static void ServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        remove.clear();

        for (PlanetEngineFire fire : PlanetEngineFire.fires_server) {
            ServerLevel serverLevel = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(fire.WorldId)));
            if (serverLevel == null) continue;

            if (serverLevel.isLoaded(fire.blockPos))
                if (!serverLevel.getBlockState(new BlockPos(fire.blockPos.getX(), fire.blockPos.getY() - 5, fire.blockPos.getZ())).is(VSOrbitModBlocks.jump_engine_controller.get()))
                    remove.add(fire);
        }

        PlanetEngineFire.fires_server.removeAll(remove);
    }


    private static final List<PlanetEngineFire> remove = new ArrayList<>();
}