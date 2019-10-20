package spinnery.container.common;

import spinnery.container.common.widget.WDropdown;
import spinnery.container.common.widget.WDynamicImage;
import spinnery.container.common.widget.WList;
import spinnery.container.common.widget.WSlot;
import spinnery.container.common.widget.WPanel;
import spinnery.container.common.widget.WSlider;
import spinnery.container.common.widget.WStaticImage;
import spinnery.container.common.widget.WToggle;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;


// maybe an addWidgets which takes a vararg widgets?


public class TestContainer extends BaseContainer {
	public TestContainer(int synchronizationID, Inventory linkedInventory, PlayerInventory linkedPlayerInventory) {
		super(synchronizationID, linkedInventory, linkedPlayerInventory);
		linkedInventory = new BasicInventory(9);

		setLinkedPanel(new WPanel(0, 0, -10, 150 + 32, 178 + 32, this));

		getLinkedPanel().setSizeX(172);
		getLinkedPanel().setSizeY(250);

		getLinkedPanel().getLinkedContainer().tick();

		getLinkedPanel().alignWithContainerEdge();

		//WStaticImage exampleWStaticImage = new WStaticImage(0, 20, 137, 59.2, 73, new Identifier("spinnery:textures/widget/catgirl.png"), linkedWPanel);

		WSlider exampleWSlider = new WSlider(0, 110, -5, 100, 12, 16, new Identifier("spinnery:textures/widget/test.png"), linkedWPanel);

		WDropdown exampleWDropdown1 = new WDropdown(331, 143, -3, 96, 18, "OwO who dis?", linkedWPanel);
		WDropdown exampleWDropdown2 = new WDropdown(331, 22, -3, 96, 18, "Whoms't ze fuck?", linkedWPanel);

		WSlot exampleSlot1 = new WSlot(58, 140, -6, 18, 18, 0, linkedInventory, linkedWPanel);
		WSlot exampleSlot2 = new WSlot(76, 140, -6, 18, 18, 1, linkedInventory, linkedWPanel);
		WSlot exampleSlot3 = new WSlot(94, 140, -6, 18, 18, 2, linkedInventory, linkedWPanel);

		WDynamicImage exampleWDynamicImage = new WDynamicImage(0, 161, 170, 10, 10, linkedWPanel,

		new Identifier("spinnery:textures/widget/0.png"),
		new Identifier("spinnery:textures/widget/1.png"),
		new Identifier("spinnery:textures/widget/2.png"),
		new Identifier("spinnery:textures/widget/3.png"),
		new Identifier("spinnery:textures/widget/4.png"),
		new Identifier("spinnery:textures/widget/5.png"),
		new Identifier("spinnery:textures/widget/6.png"),
		new Identifier("spinnery:textures/widget/7.png"));

		exampleWDropdown1.setMovable(true);
		exampleWDropdown2.setMovable(true);

		//getLinkedPanel().addWidget(exampleWStaticImage);
		getLinkedPanel().addWidget(exampleWSlider);
		getLinkedPanel().addWidget(exampleWDropdown1);
		getLinkedPanel().addWidget(exampleWDropdown2);

		getLinkedPanel().addWidget(exampleSlot1);
		getLinkedPanel().addWidget(exampleSlot2);
		getLinkedPanel().addWidget(exampleSlot3);

		getLinkedPanel().addWidget(exampleWDynamicImage);

		exampleSlot2.getSlot().setStack(new ItemStack(Items.BRAIN_CORAL, 64));

		for (int i = 0; i < 3; ++i) {
			exampleWDropdown1.addWidget(new WToggle(0, 0, -4, 18, 18, linkedWPanel));
		}

		//exampleWDropdown2.addWidget(new WStaticImage(29.1, 40, 137, 72.6, 102.4, new Identifier("spinnery:textures/widget/cattegirl.png"), linkedWPanel));

		//exampleWStaticImage.alignWithContainerCenter();
		exampleWSlider.alignWithContainerCenter();
		exampleWDynamicImage.alignWithContainerCenter();

		WSlot.addPlayerInventory(0, 18, 18, linkedPlayerInventory, linkedWPanel);

		WList exampleList = new WList(0, 0, -3, 96, 96, 3, 5, 18, 18, linkedWPanel);
		//exampleList.addWidget(new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/cattegirl.png"), linkedWPanel), new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/cattegirl.png"), linkedWPanel), new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/cattegirl.png"), linkedWPanel));

		for (int i = 0; i < 9; ++i) {
			exampleList.addWidget(new WToggle(0, 0, -4, 18, 18, linkedWPanel), new WToggle(0, 0, -4, 18, 18, linkedWPanel), new WToggle(0, 0, -4, 18, 18, linkedWPanel));
		}

		//exampleList.addWidget(new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/catgirl.png"), linkedWPanel), new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/catgirl.png"), linkedWPanel), new WStaticImage(0, 0, 137, 18, 18, new Identifier("spinnery:textures/widget/catgirl.png"), linkedWPanel));

		//WSlot exampleDumpsterFire = new WSlot(40, 140, -3, 18, 18, 0, linkedInventory, linkedWPanel);
		//exampleDumpsterFire.getSlot().setStack(new ItemStack(Items.PUFFERFISH, 64));
		//exampleList.addWidget(exampleDumpsterFire);

		//exampleList.setMovable(true);

		getLinkedPanel().addWidget(exampleList);
	}
}