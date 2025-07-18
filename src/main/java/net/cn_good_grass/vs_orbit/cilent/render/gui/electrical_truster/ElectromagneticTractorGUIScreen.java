package net.cn_good_grass.vs_orbit.cilent.render.gui.electrical_truster;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.ElectricalTrusterBlockEntity;
import net.cn_good_grass.vs_orbit.gui.ElectromagneticTractorGUI.ElectromagneticTractorGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;

public class ElectromagneticTractorGUIScreen extends AbstractContainerScreen<ElectromagneticTractorGUIMenu> {
	private final static HashMap<String, Object> guistate = ElectromagneticTractorGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();

	public ElectromagneticTractorGUIScreen(ElectromagneticTractorGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 134;
		this.imageHeight = 126;
	}

	private static final ResourceLocation texture = new ResourceLocation("vs_orbit:textures/screens/electrical_truster/main.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		force.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();

		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		if (!(blockEntity instanceof ElectricalTrusterBlockEntity electricalTrusterBlockEntity)) return;

		//文字绘制
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(2.0f, 2.0f, 1.0f); // 放大2倍
		guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.electrical_truster_gui.title"), (int) (this.leftPos / 2.0 + 4.5), (int) (this.topPos / 2.0 + 4.5), 0xFF00FFFF, true);
		guiGraphics.pose().popPose();
		//质量
		guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.electrical_truster_gui.force"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, true);
		//信息
		Ship ship = VSGameUtilsKt.getShipManagingPos(world, new BlockPos(x, y, z));
		if (ship instanceof ServerShip serverShip) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale(1.25f, 1.25f, 1.0f); // 放大1.25倍
			guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.electrical_truster_gui.info.pos").getString() + " §5"), (int) (this.leftPos / 1.25 + 8), (int) (((this.topPos + this.imageHeight) / 1.25) - 17), 0xFFFFFFFF, false);
			guiGraphics.pose().popPose();
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (force.isFocused()) return force.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		force.tick();
		textstate.put("textin:force", force.getValue());
		VSOrbitMod.PACKET_HANDLER.sendToServer(new ElectromagneticTractorGUIMenu.ElectromagneticTractorGUIOtherMessage(0, x, y, z, textstate));
		ElectromagneticTractorGUIMenu.ElectromagneticTractorGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String force_Value = force.getValue();
		super.resize(minecraft, width, height);
		force.setValue(force_Value);
	}

	@Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

	EditBox force;

	@Override
	public void init() {
		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		ElectricalTrusterBlockEntity electricalTrusterBlockEntity;
		if (blockEntity instanceof ElectricalTrusterBlockEntity) electricalTrusterBlockEntity = (ElectricalTrusterBlockEntity) blockEntity; else electricalTrusterBlockEntity = null;
		super.init();

		force = new EditBox(this.font, this.leftPos + this.imageWidth - 80, this.topPos + 36, 72, 8, Component.translatable("gui.vs_orbit.electrical_truster_gui.editbox.force")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.electrical_truster_gui.editbox.force").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.electrical_truster_gui.editbox.force").getString());else setSuggestion(null);
			}
		};
		force.setSuggestion(Component.translatable("gui.vs_orbit.electrical_truster_gui.editbox.force").getString());
		if (electricalTrusterBlockEntity != null) force.setValue(String.valueOf(electricalTrusterBlockEntity.force));
		force.setMaxLength(16);
		guistate.put("vs_orbit:force", force);
		this.addWidget(this.force);
	}
}
