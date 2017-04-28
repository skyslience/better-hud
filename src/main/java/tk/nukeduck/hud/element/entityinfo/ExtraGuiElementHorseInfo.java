package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementHorseInfo extends ExtraGuiElementEntityInfo {
	ElementSettingBoolean jump;
	ElementSettingBoolean speed;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		jump.value = true;
		speed.value = true;
		distance.value = 100;
	}
	
	@Override
	public String getName() {
		return "horseInfo";
	}
	
	public ExtraGuiElementHorseInfo() {
		this.settings.add(jump = new ElementSettingBoolean("jump"));
		this.settings.add(speed = new ElementSettingBoolean("speed"));
		this.settings.add(distance = new ElementSettingSlider("distance", 5, 200) {
			@Override
			public String getSliderText() {
				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.distanceShort", this.value));
			}
		});
	}
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled && entity instanceof EntityHorse) {
			Tessellator t = Tessellator.getInstance();
			
			glPushMatrix(); {
				ArrayList<String> infoParts = new ArrayList<String>();
				
				EntityHorse horse = (EntityHorse) entity;
				
				if(jump.value)  infoParts.add(I18n.format("betterHud.strings.jump", Math.round(getJumpHeight(horse) * 1000.0d) / 1000.0d));
				if(speed.value) infoParts.add(I18n.format("betterHud.strings.speed", Math.round(getSpeed(horse) * 1000.0d) / 1000.0d));
				
				int horseWidth = FormatUtil.getLongestWidth(mc.fontRendererObj, infoParts) + 10;
				int horseHeight = infoParts.size() * (mc.fontRendererObj.FONT_HEIGHT + 2) + 8;
				
				RenderUtil.billBoard(entity, mc.thePlayer, partialTicks);
				
				float scale = 1.0F / horseWidth;
				glScalef(scale, scale, scale);
				
				// Rendering starts
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				glTranslatef(0.0F, -horseHeight - 5, 0.0F);
				
				RenderUtil.renderQuad(t, 0, 0, horseWidth, horseHeight, 0, 0, 0, 0.5F);
				RenderUtil.zIncrease();
				for(int i = 0; i < infoParts.size(); i++) {
					mc.ingameGUI.drawString(mc.fontRendererObj, infoParts.get(i), 5, 5 + (mc.fontRendererObj.FONT_HEIGHT + 2) * i, Colors.WHITE);
				}
			}
			glPopMatrix();
		}
	}
	
	// TODO might want to check these matey
	public double getJumpHeight(EntityHorse horse) {
		double yVelocity = horse.getHorseJumpStrength(); //horses's jump strength attribute
		double jumpHeight = 0;
		while(yVelocity > 0) {
			jumpHeight += yVelocity;
			yVelocity -= 0.08;
			yVelocity *= 0.98;
		}
		return jumpHeight;
	}

	public double getSpeed(EntityHorse horse) {
		return horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 43.05;
	}
}
