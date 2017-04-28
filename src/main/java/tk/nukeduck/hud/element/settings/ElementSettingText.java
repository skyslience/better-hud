package tk.nukeduck.hud.element.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.util.constants.Colors;

public class ElementSettingText extends ElementSettingDivider {
	public ElementSettingText(String name) {
		super(name);
	}

	@Override
	public void render(GuiScreen gui, int yScroll) {
		gui.drawCenteredString(gui.mc.fontRendererObj, I18n.format("betterHud.text." + this.getName()), gui.width / 2, y + 1 - yScroll, Colors.WHITE);
	}
	
	public int getHeight() {
		return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
	}
}
