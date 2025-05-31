package net.cn_good_grass.vs_orbit.cilent.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.gui.JumpEngineControllerGUI.JumpEngineControllerGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
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
		this.imageWidth = 268;
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
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.state").getString() + " " + Component.translatable("gui.vs_orbit.jump_engine_controller_gui.state." + jumpEngineControllerBlockEntity.state).getString()), this.leftPos + 138, this.topPos + 8, 0xFFFFFFFF, false);
		//模式
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode").getString() + " " + Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode." + jumpEngineControllerBlockEntity.mode).getString()), this.leftPos + 138, this.topPos + 18, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("power") || jumpEngineControllerBlockEntity.mode.equals("jump")) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 1);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.button_toggle_mode"), this.leftPos + this.imageWidth - 41 + Component.translatable("gui.vs_orbit.jump_engine_controller_gui.button_toggle_mode").getString().length() * 2, this.topPos + 18, 0xFFFFFFFF, false);
			guiGraphics.pose().popPose();
		}
		//力
		if (jumpEngineControllerBlockEntity.mode.equals("power")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.power.force"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
		//坐标
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.pos"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.x"), this.leftPos + 9, this.topPos + 48, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.y"), this.leftPos + 9, this.topPos + 60, 0xFFFFFFFF, false);
		if (jumpEngineControllerBlockEntity.mode.equals("jump")) guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.z"), this.leftPos + 9, this.topPos + 72, 0xFFFFFFFF, false);
		//信息
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(1.25f, 1.25f, 1.0f); // 放大1.25倍
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.info.pos").getString() + " §5X:" + x + " Y:" + y + " Z:" + z), (int) (this.leftPos / 1.25 + 8), (int) (((this.topPos + this.imageHeight) / 1.25) - 17), 0xFF00FFFF, false);
		guiGraphics.pose().popPose();
		//右侧屏幕绘制
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 1);
		DrawGuiGraphics drawGuiGraphics = new DrawGuiGraphics(guiGraphics);
		if (jumpEngineControllerBlockEntity.mode.equals("power")) {
			{
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 125, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 125, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 115, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 115, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 105, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 105, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 95, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 95, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 85, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 85, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 75, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 75, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 65, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 65, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 55, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 55, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 45, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 45, 7.5, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 35, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 35, 7.5, 5, 0xFF008000);
			}
			{
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 130, this.leftPos + this.imageWidth - 129, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 116, this.leftPos + this.imageWidth - 115, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 102, this.leftPos + this.imageWidth - 101, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 88, this.leftPos + this.imageWidth - 87, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 74, this.leftPos + this.imageWidth - 73, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 60, this.leftPos + this.imageWidth - 59, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 46, this.leftPos + this.imageWidth - 45, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 130, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 31, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 116, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 17, this.topPos + this.imageHeight - 131, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 102, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 126, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 88, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 112, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 98, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 84, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 46, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 70, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 32, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 56, 10, 5, 0xFF008000);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 18, this.topPos + this.imageHeight - 32, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 42, 10, 5, 0xFF008000);
			}
			{
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 87, this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 77, 7.5, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 87, this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 87, 7.5, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 77, this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 77, 7.5, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 77, this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 87, 7.5, 5, 0xFF00FFFF);

				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 74, this.topPos + this.imageHeight - 87, this.leftPos + this.imageWidth - 70, this.topPos + this.imageHeight - 91, 15, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 87, this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 91, 15, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 64, this.topPos + this.imageHeight - 77, this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 81, 15, 5, 0xFF00FFFF);

				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 70, this.topPos + this.imageHeight - 91, this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 91, 7.5, 5, 0xFF00FFFF);
				drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 80, this.leftPos + this.imageWidth - 60, this.topPos + this.imageHeight - 91, 7.5, 5, 0xFF00FFFF);

				//drawGuiGraphics.drawLine(this.leftPos + this.imageWidth - 69, this.topPos + this.imageHeight - 82, this.leftPos + this.imageWidth - 69, this.topPos + this.imageHeight - 82, 10, 5, 0xFF00FFFF);
			}
		} else if (jumpEngineControllerBlockEntity.mode.equals("jump")) {

		}
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
		textstate.put("textin:power_force", power_force.getValue());
		textstate.put("textin:pos_x", pos_x.getValue());
		textstate.put("textin:pos_y", pos_y.getValue());
		textstate.put("textin:pos_z", pos_z.getValue());
		VSOrbitMod.PACKET_HANDLER.sendToServer(new JumpEngineControllerGUIMenu.JumpEngineControllerGUIOtherMessage(0, x, y, z, textstate));
		JumpEngineControllerGUIMenu.JumpEngineControllerGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
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
		button_toggle_mode = new ImageButton(this.leftPos + this.imageWidth - 42, this.topPos + 16, 35, 13, 0, 0, 13, new ResourceLocation("vs_orbit:textures/screens/jump_engine_controller_gui/button.png"), 35, 26, e -> {
			if (jumpEngineControllerBlockEntity.mode.equals("power") || jumpEngineControllerBlockEntity.mode.equals("jump")) {
				VSOrbitMod.PACKET_HANDLER.sendToServer(new JumpEngineControllerGUIButton(0, x, y, z, textstate));
				JumpEngineControllerGUIButton.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}) { @Override public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) { if (jumpEngineControllerBlockEntity.mode.equals("power") || jumpEngineControllerBlockEntity.mode.equals("jump")) super.render(guiGraphics, gx, gy, ticks); } };
		if (jumpEngineControllerBlockEntity.mode.equals("planet_engine")) guistate.put("button:button_toggle_mode", button_toggle_mode); this.addRenderableWidget(button_toggle_mode);



		power_force = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 36, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());else setSuggestion(null);
			}
		};
		power_force.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.power_force").getString());
		power_force.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("force")));
		power_force.setMaxLength(16);
		guistate.put("vs_orbit:power_force", power_force);
		this.addWidget(this.power_force);

		pos_x = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 48, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());else setSuggestion(null);
			}
		};
		pos_x.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_x")));
		pos_x.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_x").getString());
		pos_x.setMaxLength(16);
		guistate.put("vs_orbit:pos_x", pos_x);
		this.addWidget(this.pos_x);

		pos_y = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 60, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());else setSuggestion(null);
			}
		};
		pos_y.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_y")));
		pos_y.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_y").getString());
		pos_y.setMaxLength(16);
		guistate.put("vs_orbit:pos_y", pos_y);
		this.addWidget(this.pos_y);

		pos_z = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 72, 72, 8, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z")) {
			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());else setSuggestion(null);
			}
			@Override public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty()) setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());else setSuggestion(null);
			}
		};
		pos_z.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_z")));
		pos_z.setSuggestion(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.editbox.pos_z").getString());
		pos_z.setMaxLength(16);
		guistate.put("vs_orbit:pos_z", pos_z);
		this.addWidget(this.pos_z);
	}

	public static class DrawGuiGraphics {
		public static GuiGraphics guiGraphics;
		public DrawGuiGraphics(GuiGraphics guiGraphics) { this.guiGraphics = guiGraphics; }

		public void drawLine(double X1, double Y1, double X2, double Y2, double Width, double precision, int color) {
			double x1 = X1 * precision;
			double y1 = Y1 * precision;
			double radius = Width / 2 / precision;
			if (guiGraphics == null) return;
			double min = Math.min(Math.abs(X2 * precision - x1), Math.abs(Y2 * precision - y1));
			if (min == 0) {
				min = 1;
				radius = Width / 1.25 / precision;
			}
			double x_step = (X2 * precision - x1) / min;
			double y_step = (Y2 * precision - y1) / min;

			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale((float) (1 / precision), (float) (1 / precision), (float) (1 / precision));
			for (int i = 1;i <= min;i++) guiGraphics.fill((int) (x1 + (i - 1) * x_step - radius), (int) (y1 + (i - 1) * y_step - radius), (int) (x1 + i * x_step + radius), (int) (y1 + i * y_step + radius), color);
			guiGraphics.pose().popPose();
		}
	}
}
