package net.teamfruit.savetools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickHandler {

	public static final TickHandler INSTANCE = new TickHandler();

	private TickHandler() {
	}

	@SubscribeEvent
	public void onTick(final TickEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.gameSettings.keyBindAttack.isKeyDown()&&mc.objectMouseOver.type!=RayTraceResult.Type.MISS&&!mc.player.isCreative()) {
			final ItemStack item = Minecraft.getInstance().player.getHeldItemMainhand();
			if (item.isDamaged()) {
				final int remaiming = item.getMaxDamage()-item.getDamage();
				if (remaiming<=2) {
					saveTool();
					final ITextComponent text = new TextComponentString("SAVED").setStyle(new Style().setColor(TextFormatting.AQUA));
					Minecraft.getInstance().player.sendMessage(text);
				}
			}
		}
	}

	public void saveTool() {
		final Container con = getInventoryContainer();
		final int toolSlotId = Minecraft.getInstance().player.inventory.currentItem;

		// Mismatched slot ID with server (hotbar)
		final int serverToolSlotId = toServerSlotId(toolSlotId);

		// Bug? Conflict between the crafting slot and hot bar slot index.
		int i = 0;
		int swapSlot = -1;
		for (final Slot slot : con.inventorySlots) {
			i++;

			// 1-5 Crafting Slot, 6-9 Armor Slot
			if (i<=9)
				continue;

			if (swapSlot<0&&!slot.getStack().isDamageable())
				swapSlot = slot.getSlotIndex();

			if (!slot.getHasStack()) {
				click(con, serverToolSlotId);
				click(con, toServerSlotId(slot.getSlotIndex()));
				return;
			}
		}

		// If anything other than the hot bar is filled

		if (swapSlot<0)
			// Last slot
			swapSlot = 35;

		click(con, serverToolSlotId);
		click(con, toServerSlotId(swapSlot));
		click(con, serverToolSlotId);
	}

	private Container getInventoryContainer() {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.currentScreen!=null&&mc.currentScreen instanceof GuiContainer)
			return ((GuiContainer) mc.currentScreen).inventorySlots;
		else
			return mc.player.inventoryContainer;
	}

	private void click(final Container container, final int slotId) {
		Minecraft.getInstance().playerController.windowClick(container.windowId, slotId, 0, ClickType.PICKUP, Minecraft.getInstance().player);
	}

	// Mismatched slot ID with server
	private int toServerSlotId(final int clientSlotId) {
		// Hotbar
		if (clientSlotId<=8)
			return clientSlotId+36;
		// Offhand
		if (clientSlotId==40)
			return 45;
		return clientSlotId;
	}

}
