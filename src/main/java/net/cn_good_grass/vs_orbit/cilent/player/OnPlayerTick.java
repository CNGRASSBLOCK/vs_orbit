package net.cn_good_grass.vs_orbit.cilent.player;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class OnPlayerTick {
    private static Quaterniond quaterniond = new Quaterniond(0, 0, 0, 0);

    @SubscribeEvent
    public void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (!(event.phase == TickEvent.Phase.START)) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.getPersistentData().contains("Quaterniond")) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putDouble("x", quaterniond.x);
            compoundTag.putDouble("y", quaterniond.y);
            compoundTag.putDouble("z", quaterniond.z);
            compoundTag.putDouble("w", quaterniond.w);
            player.getPersistentData().put("Quaterniond" , compoundTag);
        } else {
            CompoundTag compoundTag = player.getPersistentData().getCompound("Quaterniond");
            quaterniond = new Quaterniond(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"), compoundTag.getDouble("w"));
        }
    }
}
