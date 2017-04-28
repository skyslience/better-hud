package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;

public class ElementSettingBoolean extends ElementSetting {
	protected GuiButton toggler;
	
	public ElementSettingBoolean(String name) {
		super(name);
	}
	
	public boolean value;
	
	public boolean toggle() {
		set(!value);
		return value;
	}
	public void set(boolean bool) {
		value = bool;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		toggler = new GuiButton(0, width / 2 - 100, y, I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format(this.value ? "options.on" : "options.off")));
		return new Gui[] {toggler};
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggle();
		button.displayString = I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format(this.value ? "options.on" : "options.off"));
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	@Override
	public void fromString(String val) {
		if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
			this.value = Boolean.parseBoolean(val);
		}
	}
	@Override
	public void otherAction(Collection<ElementSetting> settings) {
		toggler.enabled = this.getEnabled();
	}
}