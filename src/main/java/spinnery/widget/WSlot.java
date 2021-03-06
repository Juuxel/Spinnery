package spinnery.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import spinnery.Spinnery;
import spinnery.client.BaseRenderer;
import spinnery.registry.NetworkRegistry;

import java.util.Map;

public class WSlot extends WWidget implements WClient, WServer, WFocusedMouseListener {

	public static final int TOP_LEFT = 0;
	public static final int BOTTOM_RIGHT = 1;
	public static final int BACKGROUND_FOCUSED = 2;
	public static final int BACKGROUND_UNFOCUSED = 3;
	protected int slotNumber;
	protected Identifier previewTexture;
	protected int maximumCount = 0;
	protected boolean overrideMaximumCount = false;
	protected int inventoryNumber;
	protected boolean ignoreOnRelease = false;

	@Environment(EnvType.CLIENT)
	public WSlot(WPosition position, WSize size, WInterface linkedInterface, int slotNumber, int inventoryNumber) {
		setInterface(linkedInterface);
		setPosition(position);
		setSize(size);
		setSlotNumber(slotNumber);
		setInventoryNumber(inventoryNumber);
		setTheme("light");
	}

	public WSlot(WInterface linkedInterface, int slotNumber, int inventoryNumber) {
		setInterface(linkedInterface);
		setSlotNumber(slotNumber);
		setInventoryNumber(inventoryNumber);
	}

	@Environment(EnvType.CLIENT)
	public static void addPlayerInventory(WSize size, WInterface linkedInterface, int inventoryNumber) {
		int temporarySlotNumber = 0;
		addArray(
				WPosition.of(WType.ANCHORED, 4, linkedInterface.getHeight() - 82 + size.getY() * 3 + 4, 0, linkedInterface),
				size,
				linkedInterface,
				temporarySlotNumber,
				inventoryNumber,
				9,
				1);
		temporarySlotNumber = 9;
		addArray(
				WPosition.of(WType.ANCHORED, 4, linkedInterface.getHeight() - 82, 0, linkedInterface),
				size,
				linkedInterface,
				temporarySlotNumber,
				inventoryNumber,
				9,
				3);
	}

	@Environment(EnvType.CLIENT)
	public static void addArray(WPosition position, WSize size, WInterface linkedInterface, int slotNumber, int inventoryNumber, int arrayWidth, int arrayHeight) {
		for (int y = 0; y < arrayHeight; ++y) {
			for (int x = 0; x < arrayWidth; ++x) {
				WSlot.addSingle(WPosition.of(WType.FREE, position.getX() + (size.getX() * x), position.getY() + (size.getY() * y), position.getZ()), WSize.of(size.getX(), size.getY()), linkedInterface, slotNumber++, inventoryNumber);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void addSingle(WPosition position, WSize size, WInterface linkedInterface, int slotNumber, int inventoryNumber) {
		linkedInterface.add(new WSlot(position, size, linkedInterface, slotNumber, inventoryNumber));
	}

	public static void addPlayerInventory(WInterface linkedInterface, int inventoryNumber) {
		int temporarySlotNumber = 0;
		addArray(linkedInterface, temporarySlotNumber, inventoryNumber, 9, 1);
		temporarySlotNumber = 9;
		addArray(linkedInterface, temporarySlotNumber, inventoryNumber, 9, 3);
	}

	public static void addArray(WInterface linkedInterface, int slotNumber, int inventoryNumber, int arrayWidth, int arrayHeight) {
		for (int y = 0; y < arrayHeight; ++y) {
			for (int x = 0; x < arrayWidth; ++x) {
				WSlot.addSingle(linkedInterface, slotNumber++, inventoryNumber);
			}
		}
	}

	public static void addSingle(WInterface linkedInterface, int slotNumber, int inventoryNumber) {
		linkedInterface.add(new WSlot(linkedInterface, slotNumber, inventoryNumber));
	}

	@Environment(EnvType.CLIENT)
	public static WWidget.Theme of(Map<String, String> rawTheme) {
		WWidget.Theme theme = new WWidget.Theme();
		theme.put(TOP_LEFT, WColor.of(rawTheme.get("top_left")));
		theme.put(BOTTOM_RIGHT, WColor.of(rawTheme.get("bottom_right")));
		theme.put(BACKGROUND_FOCUSED, WColor.of(rawTheme.get("background_focused")));
		theme.put(BACKGROUND_UNFOCUSED, WColor.of(rawTheme.get("background_unfocused")));
		return theme;
	}

	public int getMaxCount() {
		return maximumCount;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		PlayerEntity playerEntity = getInterface().getContainer().getLinkedPlayerInventory().player;

		if (!ignoreOnRelease && mouseButton == 0 && !Screen.hasShiftDown() && !playerEntity.inventory.getCursorStack().isEmpty()) {
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.PICKUP, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.PICKUP));
		} else if (!ignoreOnRelease && mouseButton == 1 && !Screen.hasShiftDown() && !playerEntity.inventory.getCursorStack().isEmpty()) {
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 1, SlotActionType.PICKUP, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 1, SlotActionType.PICKUP));
		}

		ignoreOnRelease = false;

		super.onMouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		PlayerEntity playerEntity = getInterface().getContainer().getLinkedPlayerInventory().player;

		if (mouseButton == 0 && Screen.hasShiftDown()) {
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.QUICK_MOVE, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.QUICK_MOVE));
		} else if (mouseButton == 0 && !Screen.hasShiftDown() && playerEntity.inventory.getCursorStack().isEmpty()) {
			ignoreOnRelease = true;
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.PICKUP, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 0, SlotActionType.PICKUP));
		} else if (mouseButton == 1 && !Screen.hasShiftDown() && playerEntity.inventory.getCursorStack().isEmpty()) {
			ignoreOnRelease = true;
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 1, SlotActionType.PICKUP, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 1, SlotActionType.PICKUP));
		} else if (mouseButton == 2) {
			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), 2, SlotActionType.CLONE, playerEntity);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), 2, SlotActionType.CLONE));
		}
		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseDragged(int mouseX, int mouseY, int mouseButton, double deltaX, double deltaY) {
		if (Screen.hasShiftDown() && mouseButton == 0) {
			PlayerEntity playerEntity = getInterface().getContainer().getLinkedPlayerInventory().player;

			getInterface().getContainer().onSlotClicked(getSlotNumber(), getInventoryNumber(), mouseButton, SlotActionType.QUICK_MOVE, MinecraftClient.getInstance().player);
			ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(getSlotNumber(), getInventoryNumber(), mouseButton, SlotActionType.QUICK_MOVE));
		}

		super.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw() {
		if (isHidden()) {
			return;
		}

		int x = getX();
		int y = getY();
		int z = getZ();

		int sX = getWidth();
		int sY = getHeight();

		BaseRenderer.drawBeveledPanel(x, y, z, sX, sY, getResourceAsColor(TOP_LEFT), getResourceAsColor(BACKGROUND_UNFOCUSED), getResourceAsColor(BOTTOM_RIGHT));

		if (getFocus()) {
			BaseRenderer.drawRectangle(x + 1, y + 1, z, sX - 2, sY - 2, getResourceAsColor(BACKGROUND_FOCUSED));
		}

		if (hasPreviewTexture()) {
			BaseRenderer.drawImage(x + 1, y + 1, z, sX - 2, sY - 2, getPreviewTexture());
		}

		RenderSystem.enableLighting();
		BaseRenderer.getItemRenderer().renderGuiItem(getStack(), 1 + x + (sX - 18) / 2, 1 + y + (sY - 18) / 2);
		BaseRenderer.getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, getStack(), 1 + x + (sX - 18) / 2, 1 + y + (sY - 18) / 2, getStack().getCount() == 1 ? "" : withSuffix(getStack().getCount()));
		RenderSystem.disableLighting();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void setTheme(String theme) {
		super.setTheme(theme);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasPreviewTexture() {
		return previewTexture != null;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getPreviewTexture() {
		return previewTexture;
	}

	@Environment(EnvType.CLIENT)
	public void setPreviewTexture(Identifier previewTexture) {
		this.previewTexture = previewTexture;
	}

	public ItemStack getStack() {
		try {
			return getLinkedInventory().getInvStack(getSlotNumber());
		} catch (ArrayIndexOutOfBoundsException exception) {
			Spinnery.logger.log(Level.ERROR, "Cannot access slot " + getSlotNumber() + ", as it does exist in the inventory!");
			return ItemStack.EMPTY;
		}
	}

	@Environment(EnvType.CLIENT)
	private static String withSuffix(long value) {
		if (value < 1000) return "" + value;
		int exp = (int) (Math.log(value) / Math.log(1000));
		return String.format("%.1f%c", value / Math.pow(1000, exp), "KMGTPE".charAt(exp - 1));
	}

	public Inventory getLinkedInventory() {
		return getInterface().getContainer().getInventories().get(inventoryNumber);
	}

	public void setStack(ItemStack stack) {
		try {
			getLinkedInventory().setInvStack(slotNumber, stack);
			if (!isOverrideMaximumCount()) {
				setMaximumCount(stack.getMaxCount());
			}
		} catch (ArrayIndexOutOfBoundsException exception) {
			Spinnery.logger.log(Level.ERROR, "Cannot access slot " + getSlotNumber() + ", as it does exist in the inventory!");
		}
	}

	public boolean isOverrideMaximumCount() {
		return overrideMaximumCount;
	}

	public void setMaximumCount(int maximumCount) {
		this.maximumCount = maximumCount;
	}

	public void setOverrideMaximumCount(boolean overrideMaximumCount) {
		this.overrideMaximumCount = overrideMaximumCount;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public int getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(int inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}
}
