package spinnery.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import spinnery.widget.WInterface;
import spinnery.widget.WInterfaceHolder;
import spinnery.widget.WWidget;

public class BaseScreen extends Screen {
	WInterfaceHolder interfaceHolder = new WInterfaceHolder();

	private boolean isPauseScreen = false;

	public BaseScreen() {
		super(new LiteralText(""));
	}

	public WInterfaceHolder getInterfaceHolder() {
		return interfaceHolder;
	}

	public void setIsPauseScreen(boolean isPauseScreen) {
		this.isPauseScreen = isPauseScreen;
	}

	@Override
	public void render(int mouseX, int mouseY, float tick) {
		getInterfaceHolder().draw();
	}

	@Override
	public boolean keyPressed(int character, int keyCode, int keyModifier) {
		getInterfaceHolder().keyPressed(character, keyCode, keyModifier);

		if (character == GLFW.GLFW_KEY_ESCAPE) {
			minecraft.player.closeScreen();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		getInterfaceHolder().tick();
	}

	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		for (WInterface wInterface : interfaceHolder.getInterfaces()) {
			wInterface.align();
			wInterface.onAlign();
			for (WWidget widgetA : wInterface.getWidgets()) {
				widgetA.align();
				widgetA.onAlign();
			}
		}
		super.resize(client, width, height);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		getInterfaceHolder().onMouseClicked((int) mouseX, (int) mouseY, mouseButton);

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		getInterfaceHolder().onMouseReleased((int) mouseX, (int) mouseY, mouseButton);

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		getInterfaceHolder().onMouseDragged((int) mouseX, (int) mouseY, mouseButton, (int) deltaX, (int) deltaY);

		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaY) {
		getInterfaceHolder().onMouseScrolled((int) mouseX, (int) mouseY, deltaY);

		return false;
	}

	@Override
	public boolean keyReleased(int character, int keyCode, int keyModifier) {
		getInterfaceHolder().onKeyReleased(character, keyCode, keyModifier);

		return false;
	}

	@Override
	public boolean charTyped(char character, int keyCode) {
		getInterfaceHolder().onCharTyped(character, keyCode);

		return super.charTyped(character, keyCode);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		getInterfaceHolder().mouseMoved((int) mouseX, (int) mouseY);
	}
}
