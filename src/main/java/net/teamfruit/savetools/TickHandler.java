package net.teamfruit.savetools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickHandler {

	@SubscribeEvent
	public void onInput(final TickEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.gameSettings.keyBindAttack.isKeyDown()&&mc.objectMouseOver.type!=RayTraceResult.Type.MISS&&!mc.player.isCreative()) {
			final ItemStack item = Minecraft.getInstance().player.getHeldItemMainhand();
			if (item.isDamaged()) {
				final int remaiming = item.getMaxDamage()-item.getDamage();
				if (remaiming<=2)
					saveTool();
			}
		}
	}

	public void saveTool() {
		final Container con = getInventoryContainer();
		final int toolSlotId = Minecraft.getInstance().player.inventory.currentItem;

		// Bug? Conflict between the crafting slot and the hot bar slot index.
		int i = 0;
		for (final Slot slot : con.inventorySlots) {
			i++;

			// 1-5 Crafting Slot, 6-9 Armor Slot
			if (i<=9)
				continue;

			// Hotbar and offhand
			if (slot.getSlotIndex()<=8||slot.getSlotIndex()==40)
				break;

			SaveTools.LOGGER.info("Index:{} ID:{} Item:{} HasStack:{}", i, slot.getSlotIndex(), slot.getStack(), slot.getHasStack());
			if (!slot.getHasStack()) {
				// Mismatched slot ID with server (hotbar)
				final int slotId = toolSlotId+36;
				click(con, slotId);
				click(con, slot.getSlotIndex());
				return;
			}
		}

		// TODO: If anything other than the hot bar is filled
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

}
