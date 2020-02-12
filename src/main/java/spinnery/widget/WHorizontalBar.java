package spinnery.widget;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import spinnery.client.BaseRenderer;

@OnlyIn(Dist.CLIENT)
public class WHorizontalBar extends WAbstractBar {
	@Override
	public void draw() {
		if (isHidden()) {
			return;
		}

		int x = getX();
		int y = getY();
		int z = getZ() + 25;

		int sX = getWidth();
		int sY = getHeight();

		int rawHeight = Minecraft.getInstance().func_228018_at_().getHeight();
		double scale = Minecraft.getInstance().func_228018_at_().getGuiScaleFactor();

		int sBGY = (int) ((((float) sY / limit.getValue().intValue()) * progress.getValue().intValue()));

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		GL11.glScissor((int) (x * scale), (int) (rawHeight - ((y + sY - sBGY) * scale)), (int) (sX * scale), (int) ((sY - sBGY) * scale));

		BaseRenderer.drawImage(getX(), getY(), z, getWidth(), getHeight(), getBackgroundTexture());

		GL11.glScissor((int) (x * scale), (int) (rawHeight - ((y + sY) * scale)), (int) (sX * scale), (int) (sBGY * scale));

		BaseRenderer.drawImage(getX(), getY(), z, getWidth(), getHeight(), getForegroundTexture());

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
}
