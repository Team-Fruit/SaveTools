package net.teamfruit.savetools;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatUtil {

	public static void saveToolsMessage(final ITextComponent text) {
		final ITextComponent base = new TextComponentString("[")
				.appendSibling(new TextComponentTranslation("savetools.message.savetools").setStyle(new Style().setColor(TextFormatting.AQUA).setBold(true)))
				.appendText("] ");
		Minecraft.getInstance().player.sendMessage(base.appendSibling(text));
	}
}
