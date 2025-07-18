package net.cn_good_grass.vs_orbit.cilent.render.gui.mass_generator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.MassGeneratorBlockEntity;
import net.cn_good_grass.vs_orbit.gui.MassGeneratorGUI.MassGeneratorGUIMenu;
import net.cn_good_grass.vs_orbit.procedures.gravitation.classes.physics.Astronomical;
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

public class MassGeneratorGUIScreen extends AbstractContainerScreen<MassGeneratorGUIMenu> {
	private final static HashMap<String, Object> guistate = MassGeneratorGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();

	public MassGeneratorGUIScreen(MassGeneratorGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 134;
		this.imageHeight = 126;
	}

	private static final ResourceLocation texture = new ResourceLocation("vs_orbit:textures/screens/mass_generator_gui/main.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		mass.render(guiGraphics, mouseX, mouseY, partialTicks);
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
		if (!(blockEntity instanceof MassGeneratorBlockEntity massGeneratorBlockEntity)) return;

		//文字绘制
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(2.0f, 2.0f, 1.0f); // 放大2倍
		guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.mass_generator_gui.title"), (int) (this.leftPos / 2.0 + 4.5), (int) (this.topPos / 2.0 + 4.5), 0xFF00FFFF, true);
		guiGraphics.pose().popPose();
		//质量
		guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.mass_generator_gui.mass"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, true);
		//信息
		Ship ship = VSGameUtilsKt.getShipManagingPos(world, new BlockPos(x, y, z));
		if (ship instanceof ServerShip serverShip) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale(1.25f, 1.25f, 1.0f); // 放大1.25倍
			guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.mass_generator_gui.info.all_mass").getString() + " §5" + (serverShip.getInertiaData().getMass() + massGeneratorBlockEntity.mass)), (int) (this.leftPos / 1.25 + 8), (int) (((this.topPos + this.imageHeight) / 1.25) - 17), 0xFFFFFFFF, false);
			guiGraphics.pose().popPose();
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (mass.isFocused()) return mass.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		mass.tick();
		textstate.put("textin:mass", mass.getValue());
		VSOrbitMod.PACKET_HANDLER.sendToServer(new MassGeneratorGUIMenu.MassGeneratorGUIOtherMessage(0, x, y, z, textstate));
		MassGeneratorGUIMenu.MassGeneratorGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String mass_Value = mass.getValue();
		super.resize(minecraft, width, height);
		mass.setValue(mass_Value);
	}

	@Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

	EditBox mass;

	@Override
	public void init() {
		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		MassGeneratorBlockEntity massGeneratorBlockEntity;
		if (blockEntity instanceof MassGeneratorBlockEntity) massGeneratorBlockEntity = (MassGeneratorBlockEntity) blockEntity; else massGeneratorBlockEntity = null;
		super.init();

		mass = new EditBox(this.font, this.leftPos + this.imageWidth - 80, this.topPos + 36, 72, 8, Component.translatable("gui.vs_orbit.mass_generator_gui.editbox.mass")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.mass_generator_gui.editbox.mass").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.mass_generator_gui.editbox.mass").getString());else setSuggestion(null);
			}
		};
		mass.setSuggestion(Component.translatable("gui.vs_orbit.mass_generator_gui.editbox.mass").getString());
		if (massGeneratorBlockEntity != null) mass.setValue(String.valueOf(massGeneratorBlockEntity.mass));
		mass.setMaxLength(16);
		guistate.put("vs_orbit:mass", mass);
		this.addWidget(this.mass);
	}
}
