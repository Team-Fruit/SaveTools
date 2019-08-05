package net.teamfruit.savetools;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {

	public static final InputHandler INSTANCE = new InputHandler();

	public static final KeyBinding KEY_TOGGLE = new KeyBinding("savetools.key.toggle", GLFW.GLFW_KEY_B, "savetools.key.category");

	private boolean enabled = true;

	private InputHandler() {
	}

	@SubscribeEvent
	public void onInput(final InputEvent event) {
		if (KEY_TOGGLE.isPressed())
			if (this.enabled)
				disable();
			else
				enable();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void enable() {
		this.enabled = true;
		ChatUtil.saveToolsMessage(new TextComponentTranslation("savetools.message.enabled").setStyle(new Style().setColor(TextFormatting.YELLOW)));
	}

	public void disable() {
		this.enabled = false;
		ChatUtil.saveToolsMessage(new TextComponentTranslation("savetools.message.disabled").setStyle(new Style().setColor(TextFormatting.RED)));
	}
}
