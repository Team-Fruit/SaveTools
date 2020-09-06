package net.teamfruit.savetools;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {

	public static final InputHandler INSTANCE = new InputHandler();

	public static final KeyBinding KEY_TOGGLE = new KeyBinding("key.savetools.toggle", GLFW.GLFW_KEY_B, "key.savetools.category");
	public static final KeyBinding KEY_CONFIG = new KeyBinding("key.savetools.config", GLFW.GLFW_KEY_UNKNOWN, "key.savetools.category");

	private boolean enabled = true;
	private boolean autoEnable = true;

	private InputHandler() {
	}

	@SubscribeEvent
	public void onInput(final InputEvent event) {
		if (KEY_TOGGLE.isPressed())
			if (this.enabled)
				disable(!isShiftPressed());
			else
				enable();

		if (KEY_CONFIG.isPressed())
			Minecraft.getInstance().displayGuiScreen(Config.INSTANCE.buildClothConfig(Minecraft.getInstance().currentScreen));
	}

	private int slot = -1;
	private ItemStack item = null;

	public void onTick() {
		if (Minecraft.getInstance().player==null||isEnabled()||!this.autoEnable)
			return;

		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player!=null&&(this.slot!=player.inventory.currentItem||!ItemStack.areItemsEqualIgnoreDurability(this.item, player.getHeldItemMainhand())))
			enableAuto();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void enable() {
		this.enabled = true;
		ChatUtil.saveToolsMessage(new TranslationTextComponent("message.savetools.enabled.manually").func_230530_a_(Style.field_240709_b_.func_240712_a_(TextFormatting.YELLOW)));
	}

	private void enableAuto() {
		this.enabled = true;
		ChatUtil.saveToolsMessage(new TranslationTextComponent("message.savetools.enabled.auto").func_230530_a_(Style.field_240709_b_.func_240712_a_(TextFormatting.YELLOW)));
	}

	public void disable(final boolean autoEnable) {
		this.enabled = false;

		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (autoEnable&&player!=null) {
			this.autoEnable = true;
			this.slot = player.inventory.currentItem;
			this.item = player.getHeldItemMainhand();
			ChatUtil.saveToolsMessage(new TranslationTextComponent("message.savetools.disabled.temporary").func_230530_a_(Style.field_240709_b_.func_240712_a_(TextFormatting.RED)));
		} else {
			this.autoEnable = false;
			ChatUtil.saveToolsMessage(new TranslationTextComponent("message.savetools.disabled.while").func_230530_a_(Style.field_240709_b_.func_240712_a_(TextFormatting.RED)));
		}
	}

	public boolean isShiftPressed() {
		final long handle = Minecraft.getInstance().getMainWindow().getHandle();
		return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS||GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)==GLFW.GLFW_PRESS;
	}
}
