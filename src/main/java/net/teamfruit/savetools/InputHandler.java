package net.teamfruit.savetools;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {

	public static final InputHandler INSTANCE = new InputHandler();

	public static final KeyBinding KEY_TOGGLE = new KeyBinding("savetools.key.toggle", GLFW.GLFW_KEY_B, "savetools.key.category");

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
	}

	private int slot = -1;
	private ItemStack item = null;

	public void onTick() {
		if (Minecraft.getInstance().player==null||isEnabled()||!this.autoEnable)
			return;

		final EntityPlayerSP player = Minecraft.getInstance().player;
		if (this.slot!=player.inventory.currentItem||!ItemStack.areItemsEqualIgnoreDurability(this.item, player.getHeldItemMainhand()))
			enable();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void enable() {
		this.enabled = true;
		ChatUtil.saveToolsMessage(new TextComponentTranslation("savetools.message.enabled").setStyle(new Style().setColor(TextFormatting.YELLOW)));
	}

	public void disable(final boolean autoEnable) {
		this.enabled = false;

		if (autoEnable) {
			this.autoEnable = true;
			this.slot = Minecraft.getInstance().player.inventory.currentItem;
			this.item = Minecraft.getInstance().player.getHeldItemMainhand();
			ChatUtil.saveToolsMessage(new TextComponentTranslation("savetools.message.disabled.temporary").setStyle(new Style().setColor(TextFormatting.RED)));
		} else {
			this.autoEnable = false;
			ChatUtil.saveToolsMessage(new TextComponentTranslation("savetools.message.disabled.while").setStyle(new Style().setColor(TextFormatting.RED)));
		}
	}

	public boolean isShiftPressed() {
		final long handle = Minecraft.getInstance().mainWindow.getHandle();
		return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS||GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)==GLFW.GLFW_PRESS;
	}
}
