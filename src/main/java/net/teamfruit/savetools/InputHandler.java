package net.teamfruit.savetools;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {

	public static final InputHandler INSTANCE = new InputHandler();

	public static final KeyBinding KEY_TOGGLE = new KeyBinding("savetools.key.toggle", GLFW.GLFW_KEY_B, "savetools.key.category");

	private InputHandler() {
	}

	@SubscribeEvent
	public void onInput(final InputEvent event) {
		if (KEY_TOGGLE.isPressed()) {
			// TODO
		}
	}
}
