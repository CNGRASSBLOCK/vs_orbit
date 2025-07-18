package net.cn_good_grass.vs_orbit.cilent.render.gui.jump_engine_controller;

import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.block.blocks.JumpEngineControllerBlock;
import net.cn_good_grass.vs_orbit.gui.JumpEngineControllerGUI.JumpEngineControllerGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JumpEngineControllerGUIButton {
    public static void ButtonToggleMode(Player player, BlockPos blockPos) {
        JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) player.level().getBlockEntity(blockPos);
        if (blockEntity == null) return;
        if (blockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER)) blockEntity.mode = JumpEngineControllerBlock.Mode.JUMP; else if (blockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) blockEntity.mode = JumpEngineControllerBlock.Mode.POWER;
    }

    private final int buttonID, x, y, z;
    private HashMap<String, String> textstate;

    public JumpEngineControllerGUIButton(FriendlyByteBuf buffer) { this.buttonID = buffer.readInt(); this.x = buffer.readInt(); this.y = buffer.readInt(); this.z = buffer.readInt(); this.textstate = readTextState(buffer); }
    public JumpEngineControllerGUIButton(int buttonID, int x, int y, int z, HashMap<String, String> textstate) { this.buttonID = buttonID; this.x = x; this.y = y; this.z = z; this.textstate = textstate;}
    public static void buffer(JumpEngineControllerGUIButton message, FriendlyByteBuf buffer) { buffer.writeInt(message.buttonID); buffer.writeInt(message.x); buffer.writeInt(message.y); buffer.writeInt(message.z); writeTextState(message.textstate, buffer); }
    public static void handler(JumpEngineControllerGUIButton message, Supplier<NetworkEvent.Context> contextSupplier) { NetworkEvent.Context context = contextSupplier.get(); context.enqueueWork(() -> { HashMap<String, String> textstate = message.textstate; handleButtonAction(context.getSender(), message.buttonID, message.x, message.y, message.z, textstate); });  context.setPacketHandled(true);}

    public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z, HashMap<String, String> textstate) {
        Level world = entity.level();
        HashMap guistate = JumpEngineControllerGUIMenu.guistate;
        for (Map.Entry<String, String> entry : textstate.entrySet()) { String key = entry.getKey(); String value = entry.getValue(); guistate.put(key, value); }
        if (!world.hasChunkAt(new BlockPos(x, y, z))) return;
        if (buttonID == 0) ButtonToggleMode(entity, new BlockPos(x, y, z));
    }

    @SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) { VSOrbitMod.addNetworkMessage(JumpEngineControllerGUIButton.class, JumpEngineControllerGUIButton::buffer, JumpEngineControllerGUIButton::new, JumpEngineControllerGUIButton::handler); }
    public static void writeTextState(HashMap<String, String> map, FriendlyByteBuf buffer) { buffer.writeInt(map.size()); for (Map.Entry<String, String> entry : map.entrySet()) { buffer.writeComponent(Component.literal(entry.getKey())); buffer.writeComponent(Component.literal(entry.getValue())); } }
    public static HashMap<String, String> readTextState(FriendlyByteBuf buffer) { int size = buffer.readInt(); HashMap<String, String> map = new HashMap<>(); for (int i = 0; i < size; i++) { String key = buffer.readComponent().getString(); String value = buffer.readComponent().getString(); map.put(key, value); } return map; }
}
