package net.teamfruit.savetools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ChatUtil {

	public static void saveToolsMessage(final ITextComponent text) {
		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player==null)
			return;

		final IFormattableTextComponent base = new StringTextComponent("[")
				.append(new TranslationTextComponent("message.savetools.savetools").setStyle(Style.EMPTY.createStyleFromFormattings(TextFormatting.AQUA, TextFormatting.BOLD)))
				.append(new StringTextComponent("] "));
		// UUID unused
		player.sendMessage(base.append(text), null);
	}
}
