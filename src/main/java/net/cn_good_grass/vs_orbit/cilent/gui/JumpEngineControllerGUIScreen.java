package net.cn_good_grass.vs_orbit.cilent.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.gui.JumpEngineControllerGUI.JumpEngineControllerGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class JumpEngineControllerGUIScreen extends AbstractContainerScreen<JumpEngineControllerGUIMenu> {
	private final static HashMap<String, Object> guistate = JumpEngineControllerGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();

	public JumpEngineControllerGUIScreen(JumpEngineControllerGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("vs_orbit:textures/screens/jump_engine_controller_gui/main.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		if (blockEntity instanceof JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity) {
			if (jumpEngineControllerBlockEntity.mode.equals("power")) power_force.render(guiGraphics, mouseX, mouseY, partialTicks);
			if (jumpEngineControllerBlockEntity.mode.equals("jump")) pos_x.render(guiGraphics, mouseX, mouseY, partialTicks);
			if (jumpEngineControllerBlockEntity.mode.equals("jump")) pos_y.render(guiGraphics, mouseX, mouseY, partialTicks);
			if (jumpEngineControllerBlockEntity.mode.equals("jump")) pos_z.render(guiGraphics, mouseX, mouseY, partialTicks);
		}
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
		if (!(blockEntity instanceof JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity)) return;

		//文字绘制
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(2.0f, 2.0f, 1.0f); // 放大2倍
		guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.title"), (int) (this.leftPos / 2.0 + 4.5), (int) (this.topPos / 2.0 + 4.5), 0xFF00FFFF, true);
		guiGraphics.pose().popPose();
		//状态
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.state").getString() + " " + Component.translatable("gui.vs_orbit.jump_engine_controller_gui.state." + jumpEngineControllerBlockEntity.state).getString()), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
		//模式
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode").getString() + " " + Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode." + jumpEngineControllerBlockEntity.mode).getString()), this.leftPos + 9, this.topPos + 48, 0xFFFFFFFF, false);
		//力
		if (jumpEngineControllerBlockEntity.mode.equals("power")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.power.force"), this.leftPos + 9, this.topPos + 68, 0xFFFFFFFF, false);
		//坐标
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.pos"), this.leftPos + 9, this.topPos + 68, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.x"), this.leftPos + 9, this.topPos + 80, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.y"), this.leftPos + 9, this.topPos + 92, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.z"), this.leftPos + 9, this.topPos + 104, 0xFFFFFFFF, false);
		//信息
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(1.25f, 1.25f, 1.0f); // 放大1.25倍
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.info.pos").getString() + " §5X:" + x + " Y:" + y + " Z:" + z), (int) (this.leftPos / 1.25 + 8), (int) (((this.topPos + this.imageHeight) / 1.25) - 17), 0xFF00FFFF, false);
		guiGraphics.pose().popPose();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (power_force.isFocused()) return power_force.keyPressed(key, b, c);
		if (pos_x.isFocused()) return pos_x.keyPressed(key, b, c);
		if (pos_y.isFocused()) return pos_y.keyPressed(key, b, c);
		if (pos_z.isFocused()) return pos_z.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		power_force.tick();
		pos_x.tick();
		pos_y.tick();
		pos_z.tick();
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String power_force_Value = power_force.getValue();
		String pos_x_Value = pos_x.getValue();
		String pos_y_Value = pos_y.getValue();
		String pos_z_Value = pos_z.getValue();
		super.resize(minecraft, width, height);
		power_force.setValue(power_force_Value);
		pos_x.setValue(pos_x_Value);
		pos_y.setValue(pos_y_Value);
		pos_z.setValue(pos_z_Value);
	}

	@Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

	Button button_toggle_mode;
	EditBox power_force;
	EditBox pos_x;
	EditBox pos_y;
	EditBox pos_z;

	@Override
	public void init() {
		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		if (!(blockEntity instanceof JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity)) return;
		super.init();
		button_toggle_mode = Button.builder(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.button_toggle_mode"), e -> {
			if (true) {
				VSOrbitMod.PACKET_HANDLER.sendToServer(new JumpEngineControllerGUIButton(0, x, y, z, textstate));
				JumpEngineControllerGUIButton.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}).bounds(this.leftPos + this.imageWidth - 44, this.topPos + 46, 36, 12).build();
		if (jumpEngineControllerBlockEntity.mode.equals("planet_engine")) guistate.put("button:button_toggle_mode", button_toggle_mode); this.addRenderableWidget(button_toggle_mode);



		power_force = new EditBox(this.font, this.leftPos + this.imageWidth - 144, this.topPos + 68, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());
				else setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());
				else setSuggestion(null);
			}
		};
		power_force.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());
		power_force.setMaxLength(16);
		guistate.put("vs_orbit:power_force", power_force);
		this.addWidget(this.power_force);

		pos_x = new EditBox(this.font, this.leftPos + this.imageWidth - 150, this.topPos + 80, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());
				else setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());
				else setSuggestion(null);
			}
		};
		pos_x.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());
		pos_x.setMaxLength(16);
		guistate.put("vs_orbit:pos_x", pos_x);
		this.addWidget(this.pos_x);

		pos_y = new EditBox(this.font, this.leftPos + this.imageWidth - 150, this.topPos + 92, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());
				else setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());
				else setSuggestion(null);
			}
		};
		pos_y.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());
		pos_y.setMaxLength(16);
		guistate.put("vs_orbit:pos_y", pos_y);
		this.addWidget(this.pos_y);

		pos_z = new EditBox(this.font, this.leftPos + this.imageWidth - 150, this.topPos + 104, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());
				else setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());
				else setSuggestion(null);
			}
		};
		pos_z.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());
		pos_z.setMaxLength(16);
		guistate.put("vs_orbit:pos_z", pos_z);
		this.addWidget(this.pos_z);
	}
}
