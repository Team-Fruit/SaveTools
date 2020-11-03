package net.teamfruit.savetools;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

	private int tickCount = 0;

	private boolean tickInterval() {
		final int interval = Config.INSTANCE.general.interval.get();
		if (interval<=0)
			return true;

		if (this.tickCount==interval) {
			this.tickCount = 0;
			return true;
		}

		this.tickCount++;

		return false;
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase!=TickEvent.Phase.START)
			return;

		InputHandler.INSTANCE.onTick();

		if (!InputHandler.INSTANCE.isEnabled())
			return;

		if (!tickInterval())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final ClientPlayerEntity player = mc.player;

		if (player==null||player.isCreative())
			return;

		final boolean attack = mc.gameSettings.keyBindAttack.isKeyDown()&&Config.INSTANCE.general.attack.get();
		final boolean use = mc.gameSettings.keyBindUseItem.isKeyDown()&&Config.INSTANCE.general.use.get();

		if (!attack&&!use)
			return;

		final ItemStack item = player.getHeldItemMainhand();

		if (!item.isDamageable())
			return;

		if (Config.INSTANCE.general.onlyEnchanted.get()&&!item.isEnchanted())
			return;

		final int remaiming = item.getMaxDamage()-item.getDamage();
		final boolean save = Config.INSTANCE.general.usePercentage.get()&&
				remaiming<=item.getMaxDamage()*(Config.INSTANCE.general.percentageThreshold.get()/100f)||
				Config.INSTANCE.general.useAbsolute.get()&&
						remaiming<=Config.INSTANCE.general.absoluteThreshold.get();

		if (!save)
			return;

		final String name = Optional.ofNullable(item.getItem().getRegistryName())
				.map(ResourceLocation::toString)
				.orElseGet(() -> item.getItem().toString());

		if (Config.INSTANCE.advanced.enableListFiler.get())
			if (Config.INSTANCE.advanced.whitelistMode.get()) {
				if (Config.INSTANCE.advanced.list.get().stream().noneMatch(name::equals))
					return;
			} else if (Config.INSTANCE.advanced.list.get().stream().anyMatch(name::equals))
				return;

		if (!Config.INSTANCE.material.wood.get()&&name.startsWith("minecraft:wooden_"))
			return;
		if (!Config.INSTANCE.material.stone.get()&&name.startsWith("minecraft:stone_"))
			return;
		if (!Config.INSTANCE.material.iron.get()&&name.startsWith("minecraft:iron_"))
			return;
		if (!Config.INSTANCE.material.gold.get()&&name.startsWith("minecraft:golden_"))
			return;
		if (!Config.INSTANCE.material.diamond.get()&&name.startsWith("minecraft:diamond_"))
			return;
		if (!Config.INSTANCE.material.netherite.get()&&name.startsWith("minecraft:netherite_"))
			return;

		if (attack) {
			final RayTraceResult rayTrace = mc.objectMouseOver;

			if (rayTrace!=null&&rayTrace.getType()!=RayTraceResult.Type.MISS)
				if (rayTrace instanceof BlockRayTraceResult) {
					final World world = mc.world;
					if (world!=null) {
						final BlockPos pos = ((BlockRayTraceResult) rayTrace).getPos();
						if (world.getBlockState(pos).getBlockHardness(world, pos)!=0.0f)
							saveTool();
					}
				} else
					saveTool();
		}

		if (use)
			saveTool();
	}

	public void saveTool() {
		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player==null)
			return;

		final Container con = getInventoryContainer(player);
		final int toolSlotId = player.inventory.currentItem;

		final int serverToolSlotId = toServerSlotId(toolSlotId);

		// Conflict between the crafting slot and hot bar slot index.
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

				sendSaveMessage();
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

		sendSaveMessage();
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

	private void sendSaveMessage() {
		ChatUtil.saveToolsMessage(new TranslationTextComponent("message.savetools.saved").setStyle(Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
	}

}
