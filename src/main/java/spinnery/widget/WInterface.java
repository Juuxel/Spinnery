package spinnery.widget;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinnery.client.BaseRenderer;
import spinnery.common.BaseContainer;
import spinnery.packet.WidgetSyncPacket;
import spinnery.registry.NetworkHandler;
import spinnery.util.EventUtilities;
import spinnery.widget.api.Color;
import spinnery.widget.api.WDrawableCollection;
import spinnery.widget.api.WLayoutElement;
import spinnery.widget.api.WModifiableCollection;
import spinnery.widget.api.WNetworked;
import spinnery.widget.api.WThemable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WInterface implements WDrawableCollection, WModifiableCollection, WLayoutElement, WThemable {
	protected BaseContainer linkedContainer;
	protected Set<WAbstractWidget> widgets = new LinkedHashSet<>();
	protected List<WLayoutElement> orderedWidgets = new ArrayList<>();
	protected Map<Class<? extends WAbstractWidget>, WAbstractWidget> cachedWidgets = new HashMap<>();
	protected boolean isClientside;
	protected ResourceLocation theme;
	protected boolean isBlurred = false;

	public WInterface() {
		setClientside(true);
	}

	public <W extends WInterface> W setClientside(Boolean clientside) {
		isClientside = clientside;
		return (W) this;
	}

	public WInterface(BaseContainer linkedContainer) {
		setContainer(linkedContainer);
		if (getContainer().getWorld().isRemote()) {
			setClientside(true);
		}
	}

	public BaseContainer getContainer() {
		return linkedContainer;
	}

	public <W extends WInterface> W setContainer(BaseContainer linkedContainer) {
		this.linkedContainer = linkedContainer;
		return (W) this;
	}

	public boolean isClient() {
		return isClientside;
	}

	public Map<Class<? extends WAbstractWidget>, WAbstractWidget> getCachedWidgets() {
		return cachedWidgets;
	}

	@Override
	public ResourceLocation getTheme() {
		return theme;
	}

	public <W extends WInterface> W setTheme(ResourceLocation theme) {
		this.theme = theme;
		return (W) this;
	}

	public <W extends WInterface> W setTheme(String theme) {
		return setTheme(new ResourceLocation(theme));
	}

	public boolean isServer() {
		return !isClientside;
	}

	@Override
	public void add(WAbstractWidget... widgets) {
		this.widgets.addAll(Arrays.asList(widgets));
		onLayoutChange();
	}

	@Override
	public void recalculateCache() {
		orderedWidgets = new ArrayList<>(getWidgets());
		Collections.sort(orderedWidgets);
		Collections.reverse(orderedWidgets);
	}

	@Override
	public Set<WAbstractWidget> getWidgets() {
		return widgets;
	}

	@Override
	public boolean contains(WAbstractWidget... widgets) {
		return this.widgets.containsAll(Arrays.asList(widgets));
	}

	@Override
	public List<WLayoutElement> getOrderedWidgets() {
		return orderedWidgets;
	}

	@Override
	public void remove(WAbstractWidget... widgets) {
		this.widgets.removeAll(Arrays.asList(widgets));
		onLayoutChange();
	}

	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveMouse(widget)) continue;
			widget.onMouseClicked(mouseX, mouseY, mouseButton);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createMouseClickPacket(((WNetworked) widget), mouseX, mouseY, mouseButton));
			}
		}
	}

	public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveMouse(widget)) continue;
			widget.onMouseReleased(mouseX, mouseY, mouseButton);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createMouseReleasePacket(((WNetworked) widget), mouseX, mouseY, mouseButton));
			}
		}
		return false;
	}

	public boolean onMouseDragged(int mouseX, int mouseY, int mouseButton, int deltaX, int deltaY) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveMouse(widget)) continue;
			widget.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createMouseDragPacket(((WNetworked) widget), mouseX, mouseY, mouseButton, deltaX, deltaY));
			}
		}
		return false;
	}

	public void onMouseScrolled(int mouseX, int mouseY, double deltaY) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveMouse(widget)) continue;
			widget.onMouseScrolled(mouseX, mouseY, deltaY);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createMouseScrollPacket(((WNetworked) widget), mouseX, mouseY, deltaY));
			}
		}
	}

	public void onMouseMoved(int mouseX, int mouseY) {
		for (WAbstractWidget widget : getWidgets()) {
			widget.updateFocus(mouseX, mouseY);
			if (!EventUtilities.canReceiveMouse(widget)) continue;
			widget.onMouseMoved(mouseX, mouseY);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createFocusPacket(((WNetworked) widget), widget.isFocused()));
			}
		}
	}

	public void onKeyReleased(int keyCode, int character, int keyModifier) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveKeyboard(widget)) continue;
			widget.onKeyReleased(keyCode, character, keyModifier);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createKeyReleasePacket(((WNetworked) widget), character, keyCode, keyModifier));
			}
		}
	}

	public void onKeyPressed(int keyCode, int character, int keyModifier) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveKeyboard(widget)) continue;
			widget.onKeyPressed(keyCode, character, keyModifier);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createKeyPressPacket(((WNetworked) widget), character, keyCode, keyModifier));
			}
		}
	}

	public void onCharTyped(char character, int keyCode) {
		for (WAbstractWidget widget : getWidgets()) {
			if (!EventUtilities.canReceiveKeyboard(widget)) continue;
			widget.onCharTyped(character, keyCode);
			if (widget instanceof WNetworked) {
				NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createCharTypePacket(((WNetworked) widget), character, keyCode));
			}
		}
	}

	public void onDrawMouseoverTooltip(int mouseX, int mouseY) {
		for (WAbstractWidget widget : getWidgets()) {
			widget.onDrawTooltip(mouseX, mouseY);
		}
	}

	public void onAlign() {
		for (WAbstractWidget widget : getWidgets()) {
			widget.align();
			widget.onAlign();
		}
	}

	public void tick() {
		for (WAbstractWidget widget : getAllWidgets()) {
			widget.tick();
		}
	}

	@Override
	public void draw() {
		if (isBlurred()) {
			MainWindow window = Minecraft.getInstance().func_228018_at_();
			BaseRenderer.drawRectangle(0, 0, 0, window.getWidth(), window.getHeight(), Color.of(0x90000000));
		}

		for (WLayoutElement widget : getOrderedWidgets()) {
			widget.draw();
		}
	}

	public boolean isBlurred() {
		return isBlurred;
	}

	public <W extends WInterface> W setBlurred(boolean isBlurred) {
		this.isBlurred = isBlurred;
		return (W) this;
	}

	@Override
	public void onLayoutChange() {
		recalculateCache();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getX() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getY() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getZ() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getWidth() {
		return Minecraft.getInstance().func_228018_at_().getScaledWidth();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getHeight() {
		return Minecraft.getInstance().func_228018_at_().getScaledHeight();
	}
}
