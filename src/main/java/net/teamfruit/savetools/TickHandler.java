package net.teamfruit.savetools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickHandler {

	public static final TickHandler INSTANCE = new TickHandler();

	private TickHandler() {
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase!=TickEvent.Phase.START)
			return;

		InputHandler.INSTANCE.onTick();

		if (!InputHandler.INSTANCE.isEnabled())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final ClientPlayerEntity player = mc.player;
		final World world = mc.world;
		final RayTraceResult rayTrace = mc.objectMouseOver;

		if (player==null||world==null||rayTrace==null)
			return;

		if (mc.gameSettings.keyBindAttack.isKeyDown()&&!player.isCreative()&&rayTrace.getType()!=RayTraceResult.Type.MISS&&rayTrace instanceof BlockRayTraceResult) {
			final ItemStack item = player.getHeldItemMainhand();
			final BlockPos pos = ((BlockRayTraceResult) rayTrace).getPos();
			if (item.isDamaged()&&(rayTrace.getType()==RayTraceResult.Type.ENTITY||world.getBlockState(pos).getBlockHardness(world, pos)!=0.0f)) {
				final int remaiming = item.getMaxDamage()-item.getDamage();
				if (remaiming<=item.getMaxDamage()/100f||remaiming<=2) {
					saveTool();
					ChatUtil.saveToolsMessage(new TranslationTextComponent("savetools.message.saved").setStyle(new Style().setColor(TextFormatting.YELLOW)));
				}
			}
		}
	}

	public void saveTool() {
		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player==null)
			return;

		final Container con = getInventoryContainer(player);
		final int toolSlotId = player.inventory.currentItem;

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

		// If anything other than hot bar is filled

		if (swapSlot<0)
			// Last slot
			swapSlot = 35;

		click(con, serverToolSlotId);
		click(con, toServerSlotId(swapSlot));
		click(con, serverToolSlotId);
	}

	private Container getInventoryContainer(final ClientPlayerEntity player) {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.currentScreen!=null&&mc.currentScreen instanceof ContainerScreen)
			return ((ContainerScreen<?>) mc.currentScreen).getContainer();
		else
			return player.container;
	}

	private void click(final Container container, final int slotId) {
		final PlayerController playerController = Minecraft.getInstance().playerController;
		if (playerController!=null)
			playerController.windowClick(container.windowId, slotId, 0, ClickType.PICKUP, Minecraft.getInstance().player);
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
