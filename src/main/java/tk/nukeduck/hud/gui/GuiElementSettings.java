package tk.nukeduck.hud.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSetting;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FuncsUtil;
import tk.nukeduck.hud.util.constants.Colors;

public class GuiElementSettings extends GuiScreen {
	public ExtraGuiElement element;
	ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();
	public HashMap<Gui, ElementSetting> settingLinks = new HashMap<Gui, ElementSetting>();
	
	private float scrollFactor = 0.0F;
	private int scrollHeight = 0;
	
	private int totalHeight;
	private int top, barHeight, bottom;
	
	public GuiElementSettings(ExtraGuiElement element, GuiScreen prev) {
		this.element = element;
		this.prev = prev;
	}
	
	GuiScreen prev;
	
	@Override
	public void initGui() {
		this.textboxList.clear();
		
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, height / 16 + 20, I18n.format("gui.done")));
		
		this.totalHeight = 0;
		for(ElementSetting setting : element.settings) {
			Gui[] guis = setting.getGuiParts(this.width, height / 16 + 45 + this.totalHeight);
			for(Gui gui : guis) {
				if(gui instanceof GuiButton) this.buttonList.add((GuiButton) gui);
				else if(gui instanceof GuiTextField) this.textboxList.add((GuiTextField) gui);
				
				settingLinks.put(gui, setting);
			}
			this.totalHeight += setting.getGuiHeight() + 5;
			setting.otherAction(settingLinks.values());
		}
		this.totalHeight += 25;
		this.scrollFactor = this.scrollHeight = 0;
		
		this.top = this.height / 16 + 20;
		this.bottom = this.height - 16;
		this.barHeight = this.bottom - this.top;
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			mc.displayGuiScreen(prev);
		} else {
			settingLinks.get(button).actionPerformed(this, button);
			// Notify the rest of the elements that a button has been pressed
			for(ElementSetting setting : settingLinks.values()) {
				setting.otherAction(settingLinks.values());
			}
		}
	}
	
	public ElementSettingAbsolutePosition currentPicking = null;
	private int clickTimer = 0;
	
	private GuiButton clickedUpDown = null;
	
	private static final int REPEAT_SPEED = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s
	
	@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) {
			field.updateCursorCounter();
		}
		
		if(Mouse.isButtonDown(0)) {
			if(clickedUpDown == null) {
				for(Object obj : this.buttonList) {
					GuiButton button = (GuiButton) obj;
					if(button instanceof GuiUpDownButton && button.isMouseOver()) {
						clickedUpDown = button;
						break;
					}
				}
			}
		} else {
			clickedUpDown = null;
		}
		
		if(clickedUpDown != null) {
			if(++clickTimer % Math.max(1, Math.round(REPEAT_SPEED / clickTimer)) == 0) {
				int c = Math.max(1, (clickTimer - REPEAT_SPEED) / REPEAT_SPEED_FAST);
				for(int i = 0; i < c; i++) {
					this.actionPerformed(clickedUpDown);
				}
			}
		} else {
			clickTimer = 0;
		}
		
		if(currentPicking != null) {
			ScaledResolution res = new ScaledResolution(mc);
			
			Bounds b = this.element.getBounds(res).clone();
			b.setX(Mouse.getEventX() * this.width / this.mc.displayWidth);
			b.setY(this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1);
			
			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				ArrayList<Bounds> bounds = new ArrayList<Bounds>();
				for(ExtraGuiElement element : BetterHud.proxy.elements.elements) {
					if(element == this.element || !element.enabled) continue;
					bounds.add(element.getBounds(res));
				}
				b.snapTest(10, bounds.toArray(new Bounds[bounds.size()]));
				b.snapTest(10, new Bounds(this.width, 0, -this.width, this.height));
				b.snapTest(10, new Bounds(0, this.height, this.width, -this.height));
			}
			
			currentPicking.x = b.getX();
			currentPicking.y = b.getY();
			
			currentPicking.updateText();
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		for(GuiTextField field : this.textboxList) {
			field.textboxKeyTyped(typedChar, keyCode);
			settingLinks.get(field).keyTyped(typedChar, keyCode);
		}
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		if(!this.scrolling && this.totalHeight > barHeight) {
			int i = Mouse.getEventDWheel();
			this.setScroll(this.scrollFactor - (float) i / 1500.0F);
		}
	}
	
	public boolean scrolling;
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.totalHeight > barHeight) {
			int x = this.width / 2 + 160;
			if(this.scrolling = mouseX >= x && mouseX < x + 4 && mouseY >= top && mouseY < bottom) {
				this.setScroll((float) (mouseY - top - 15) / (float) (barHeight - 30));
			}
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for(GuiTextField field : this.textboxList) {
			field.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		if(currentPicking != null && !currentPicking.pick.isMouseOver()) {
			currentPicking.pick.displayString = I18n.format("betterHud.menu.pick");
			currentPicking.isPicking = false;
			
			currentPicking = null;
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		this.scrolling = false;
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if(this.scrolling) {
			this.setScroll((float) (mouseY - top - 15) / (float) (barHeight - 30));
		}
	}
	
	public void setScroll(float scroll) {
		if(this.totalHeight <= barHeight) return;
		
		// Bring buttons back to 0
		this.scrollHeight = this.totalHeight - barHeight;
		this.scrollHeight *= this.scrollFactor;
		for(Object button : this.buttonList) ((GuiButton) button).yPosition += this.scrollHeight;
		for(GuiTextField box : this.textboxList) box.yPosition += this.scrollHeight;
		
		this.scrollFactor = FuncsUtil.clamp(scroll, 0.0F, 1.0F);
		
		// Move buttons to new Y
		this.scrollHeight = this.totalHeight - barHeight;
		this.scrollHeight *= this.scrollFactor;
		for(Object button : this.buttonList) ((GuiButton) button).yPosition -= this.scrollHeight;
		for(GuiTextField box : this.textboxList) box.yPosition -= this.scrollHeight;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawDefaultBackground();
		ScaledResolution res = new ScaledResolution(mc);
		//super.drawScreen(mouseX, mouseY, p_73863_3_);
		
		this.drawCenteredString(this.fontRendererObj, I18n.format("betterHud.menu.settings", this.element.getLocalizedName()), this.width / 2, height / 16 + 5, 16777215);
		
		if(this.totalHeight > barHeight) {
			int noduleY = top + Math.round(this.scrollFactor * (barHeight - 30));
			
			Gui.drawRect(this.width / 2 + 160, top, this.width / 2 + 164, top + barHeight, 0xAA555555);
			Gui.drawRect(this.width / 2 + 160, noduleY, this.width / 2 + 164, noduleY + 30, 0x66FFFFFF);
		} else this.setScroll(0);
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(0, 16 * res.getScaleFactor(), mc.displayWidth, (this.height - top - 15) * res.getScaleFactor());
		//GL11.glTranslatef(0, -scrollAmt, 0);
		
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		for(GuiTextField field : this.textboxList) {
			field.drawTextBox();
		}
		
		/*int heightShown = this.height - (top + 16);
		int scrollAmt = this.totalHeight - heightShown;
		scrollAmt *= this.scrollFactor;*/
		//GL11.glTranslatef(0, -scrollAmt, 0);
		for(ElementSetting setting : element.settings) {
			setting.render(this, this.scrollHeight);
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
		int w = 100;
		int h = Math.round(100 * (float) height / (float) width);
		
		String wStr = String.valueOf(width);
		String hStr = String.valueOf(height);
		int wStrW = fontRendererObj.getStringWidth(wStr) + 10;
		int hStrH = fontRendererObj.FONT_HEIGHT + 10;
		
		Gui.drawRect(20, 10, 20 + (w - wStrW) / 2, 11, Colors.fromRGB(255, 255, 255));
		Gui.drawRect(20 + (w + wStrW) / 2, 10, 20 + w, 11, Colors.fromRGB(255, 255, 255));
		this.drawCenteredString(fontRendererObj, wStr, 20 + w / 2, 10, Colors.fromRGB(255, 255, 255));
		
		Gui.drawRect(10, 20, 11, 20 + (h - hStrH) / 2, Colors.fromRGB(255, 255, 255));
		Gui.drawRect(10, 20 + (h + hStrH) / 2, 11, 20 + h, Colors.fromRGB(255, 255, 255));
		this.drawString(fontRendererObj, hStr, 10, 20 + (h - hStrH + 10) / 2, Colors.fromRGB(255, 255, 255));
		
		if(this.currentPicking != null) {
			String disableSnap = I18n.format("betterHud.text.unsnap", Keyboard.getKeyName(Keyboard.KEY_LCONTROL));
			this.drawString(fontRendererObj, disableSnap, 5, this.height - fontRendererObj.FONT_HEIGHT - 5, 0xffffff);
			
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			for(ExtraGuiElement element : BetterHud.proxy.elements.elements) {
				Bounds b = element.getBounds(res);
				if(element.enabled) {
					Gui.drawRect(b.getX(), b.getY(), b.getX2(), b.getY2(), Colors.fromARGB(element == this.element ? 255 : 50, 255, 0, 0));
				}
			}
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
	}
}