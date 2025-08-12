package net.cn_good_grass.vs_orbit.client.render.gui.jump_engine_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cn_good_grass.vs_orbit.VSOrbitMod;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.block.blocks.JumpEngineControllerBlock;
import net.cn_good_grass.vs_orbit.gui.menu.JumpEngineControllerGUIMenu;
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
			if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER)) {
				editBox_1.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {
				editBox_2.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_3.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_4.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_5.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.PLANET_ENGINE)) {
				editBox_2.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_3.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_4.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_6.render(guiGraphics, mouseX, mouseY, partialTicks);
				editBox_7.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
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
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER) || jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 1);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.button_toggle_mode"), this.leftPos + this.imageWidth - 41 + Minecraft.getInstance().font.width(Component.translatable("gui.vs_orbit.jump_engine_controller_gui.button_toggle_mode").getString()) / 2, this.topPos + 18, 0xFFFFFFFF, false);
			guiGraphics.pose().popPose();
		}
		//力
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER)) {
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.power.force"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
		}
		//跃迁
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.pos"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.x"), this.leftPos + 9, this.topPos + 48, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.y"), this.leftPos + 9, this.topPos + 60, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.z"), this.leftPos + 9, this.topPos + 72, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.jump.world"), this.leftPos + 9, this.topPos + 84, 0xFFFFFFFF, false);
		}
		//行星发动机
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.PLANET_ENGINE)) {
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.force"), this.leftPos + 9, this.topPos + 36, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.force_x"), this.leftPos + 9, this.topPos + 48, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.force_y"), this.leftPos + 9, this.topPos + 60, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.force_z"), this.leftPos + 9, this.topPos + 72, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.fire_display"), this.leftPos + 9, this.topPos + 84, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.fire_display_h"), this.leftPos + 9, this.topPos + 96, 0xFFFFFFFF, false);
			guiGraphics.drawString(this.font, Component.translatable("gui.vs_orbit.jump_engine_controller_gui.mode.planet_engine.fire_display_r"), this.leftPos + 9, this.topPos + 108, 0xFFFFFFFF, false);
		}
		//信息
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(1.25f, 1.25f, 1.0f); // 放大1.25倍
		guiGraphics.drawString(this.font, (Component.translatable("gui.vs_orbit.jump_engine_controller_gui.info.pos").getString() + " §5X:" + x + " Y:" + y + " Z:" + z), (int) (this.leftPos / 1.25 + 8), (int) (((this.topPos + this.imageHeight) / 1.25) - 17), 0xFFFFFFFF, false);
		guiGraphics.pose().popPose();
		//右侧屏幕绘制
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 1);
		DrawGuiGraphics drawGuiGraphics = new DrawGuiGraphics(guiGraphics);
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER)) {
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
		} else if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {

		}
		guiGraphics.pose().popPose();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (editBox_1.isFocused()) return editBox_1.keyPressed(key, b, c);
		if (editBox_2.isFocused()) return editBox_2.keyPressed(key, b, c);
		if (editBox_3.isFocused()) return editBox_3.keyPressed(key, b, c);
		if (editBox_4.isFocused()) return editBox_4.keyPressed(key, b, c);
		if (editBox_5.isFocused()) return editBox_5.keyPressed(key, b, c);
		if (editBox_6.isFocused()) return editBox_6.keyPressed(key, b, c);
		if (editBox_7.isFocused()) return editBox_7.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		editBox_1.tick();
		editBox_2.tick();
		editBox_3.tick();
		editBox_4.tick();
		editBox_5.tick();
		editBox_6.tick();
		editBox_7.tick();
		textstate.put("textin:editBox_1", editBox_1.getValue());
		textstate.put("textin:editBox_2", editBox_2.getValue());
		textstate.put("textin:editBox_3", editBox_3.getValue());
		textstate.put("textin:editBox_4", editBox_4.getValue());
		textstate.put("textin:editBox_5", editBox_5.getValue());
		textstate.put("textin:editBox_6", editBox_6.getValue());
		textstate.put("textin:editBox_7", editBox_7.getValue());
		VSOrbitMod.PACKET_HANDLER.sendToServer(new JumpEngineControllerGUIMenu.JumpEngineControllerGUIOtherMessage(0, x, y, z, textstate));
		JumpEngineControllerGUIMenu.JumpEngineControllerGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
		editBox_1.setValue(editBox_1.getValue());
		editBox_2.setValue(editBox_2.getValue());
		editBox_3.setValue(editBox_3.getValue());
		editBox_4.setValue(editBox_4.getValue());
		editBox_5.setValue(editBox_5.getValue());
		editBox_6.setValue(editBox_6.getValue());
		editBox_7.setValue(editBox_7.getValue());
	}

	@Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

	Button button_toggle_mode;
	EditBox editBox_1;
	EditBox editBox_2;
	EditBox editBox_3;
	EditBox editBox_4;
	EditBox editBox_5;
	EditBox editBox_6;
	EditBox editBox_7;

	@Override
	public void init() {
		BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
		JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity;
		if (blockEntity instanceof JumpEngineControllerBlockEntity) jumpEngineControllerBlockEntity = (JumpEngineControllerBlockEntity) blockEntity; else jumpEngineControllerBlockEntity = null;
        super.init();



		button_toggle_mode = new ImageButton(this.leftPos + this.imageWidth - 42, this.topPos + 16, 35, 13, 0, 0, 13, new ResourceLocation("vs_orbit:textures/screens/jump_engine_controller_gui/button.png"), 35, 26, e -> {
			if (jumpEngineControllerBlockEntity != null) if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER) || jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {
				VSOrbitMod.PACKET_HANDLER.sendToServer(new JumpEngineControllerGUIButton(0, x, y, z, textstate));
				JumpEngineControllerGUIButton.handleButtonAction(entity, 0, x, y, z, textstate);
				upEditBoxValue();
			}
		}) { @Override public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) { if (jumpEngineControllerBlockEntity != null) if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER) || jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) super.render(guiGraphics, gx, gy, ticks); } };
		if (jumpEngineControllerBlockEntity != null) if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.PLANET_ENGINE)) guistate.put("button:button_toggle_mode", button_toggle_mode); this.addRenderableWidget(button_toggle_mode);



		editBox_1 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 36, 72, 8, Component.translatable(""));
		editBox_1.setMaxLength(64);
		guistate.put("vs_orbit:editBox_1", editBox_1);
		this.addWidget(this.editBox_1);

		editBox_2 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 48, 72, 8, Component.translatable(""));
		editBox_2.setSuggestion("");
		editBox_2.setMaxLength(64);
		guistate.put("vs_orbit:editBox_2", editBox_2);
		this.addWidget(this.editBox_2);

		editBox_3 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 60, 72, 8, Component.translatable(""));
		editBox_3.setSuggestion("");
		editBox_3.setMaxLength(64);
		guistate.put("vs_orbit:editBox_3", editBox_3);
		this.addWidget(this.editBox_3);

		editBox_4 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 72, 72, 8, Component.translatable(""));
		editBox_4.setSuggestion("");
		editBox_4.setMaxLength(64);
		guistate.put("vs_orbit:editBox_4", editBox_4);
		this.addWidget(this.editBox_4);

		editBox_5 = new EditBox(this.font, this.leftPos + this.imageWidth - 223, this.topPos + 84, 84 , 8, Component.translatable(""));
		editBox_5.setSuggestion("");
		editBox_5.setMaxLength(64);
		guistate.put("vs_orbit:editBox_5", editBox_5);
		this.addWidget(this.editBox_5);

		editBox_6 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 96, 72 , 8, Component.translatable(""));
		editBox_6.setSuggestion("");
		editBox_6.setMaxLength(64);
		guistate.put("vs_orbit:editBox_6", editBox_6);
		this.addWidget(this.editBox_6);

		editBox_7 = new EditBox(this.font, this.leftPos + this.imageWidth - 211, this.topPos + 108, 72 , 8, Component.translatable(""));
		editBox_7.setSuggestion("");
		editBox_7.setMaxLength(64);
		guistate.put("vs_orbit:editBox_7", editBox_7);
		this.addWidget(this.editBox_7);

		upEditBoxValue();
	}

	public void upEditBoxValue(){
		if (!(world.getBlockEntity(new BlockPos(x, y, z)) instanceof JumpEngineControllerBlockEntity jumpEngineControllerBlockEntity)) return;

		if (editBox_1 == null || editBox_2 == null || editBox_3 == null || editBox_4 == null || editBox_5 == null) return;

		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.POWER)) {
			editBox_1.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("force")));
		}
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.JUMP)) {
			editBox_2.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_x")));
			editBox_3.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_y")));
			editBox_4.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("pos_z")));
			editBox_5.setValue(jumpEngineControllerBlockEntity.setting.getString("pos_world"));
		}
		if (jumpEngineControllerBlockEntity.mode.equals(JumpEngineControllerBlock.Mode.PLANET_ENGINE)) {
			editBox_2.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("planet_force_x")));
			editBox_3.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("planet_force_y")));
			editBox_4.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getDouble("planet_force_z")));
			editBox_6.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getInt("planet_fire_display_height")));
			editBox_7.setValue(String.valueOf(jumpEngineControllerBlockEntity.setting.getInt("planet_fire_display_radius")));
		}
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
