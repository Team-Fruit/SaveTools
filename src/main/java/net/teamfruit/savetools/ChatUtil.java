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
				.func_230529_a_(new TranslationTextComponent("message.savetools.savetools").func_230530_a_(Style.field_240709_b_.func_240720_a_(TextFormatting.AQUA, TextFormatting.BOLD)))
				.func_230529_a_(new StringTextComponent("] "));
		// UUID unused
		player.sendMessage(base.func_230529_a_(text), null);
	}
}
