package com.cabchinoe.gotcha;

import com.cabchinoe.common.Render;
import com.cabchinoe.gotcha.gui.EquipmentTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;


/**
 * Created by n3212 on 2017/9/26.
 */
@SideOnly(Side.CLIENT)
public class EquipmentListener {
    public Minecraft mc;
    public int hasTip[] = {0,0,0,0,0};
    public ArrayList<EquipmentTip> drawList = new ArrayList<EquipmentTip>();
    public EquipmentListener(Minecraft mc){
        this.mc = mc;
    }
    public void check(){
        ArrayList<EquipmentTip> tmp = new ArrayList<EquipmentTip>();
        for(EquipmentTip e:drawList){
            if(System.currentTimeMillis() - e.showTime<5000){
                tmp.add(e);
            }
        }
        drawList = (ArrayList<EquipmentTip>)tmp.clone();

        EntityPlayer player = this.mc.player;
        NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
        for(int i =0; i< armorInventory.size();++i){
            ItemStack itemStack = armorInventory.get(i);
            if(itemStack != null) {
                Item itemArmor = itemStack.getItem();
                if(itemArmor instanceof ItemArmor) {
                    String itemName = itemStack.getDisplayName();
                    int maxDamage = itemArmor.getMaxDamage();
                    int x = (int) (maxDamage * 0.1F);
                    if (maxDamage - itemArmor.getDamage(itemStack) <= x) {
                        if(hasTip[i]!=1) {
                            hasTip[i] = 1;
                            EquipmentTip equipmentTip = new EquipmentTip(itemName);
                            drawList.add(equipmentTip);
                        }
                    }else{
                        hasTip[i] = 0;
                    }
                }else{
                    hasTip[i] = 0;
                }
            }else{
                hasTip[i] = 0;
            }
        }
        ItemStack offHand = this.mc.player.getHeldItemOffhand();
        if(offHand!=null && offHand.getItem() instanceof ItemShield){
            ItemShield itemShield = (ItemShield)offHand.getItem();
            String itemName = offHand.getDisplayName();
            int maxDamage = itemShield.getMaxDamage();
            int x = (int) (maxDamage * 0.1F);
            if(maxDamage - itemShield.getDamage(offHand) <= x){
                if(hasTip[4]!=1) {
                    hasTip[4] = 1;
                    EquipmentTip equipmentTip = new EquipmentTip(itemName);
                    drawList.add(equipmentTip);
                }
            }else{
                hasTip[4] = 0;
            }
        }else{
            hasTip[4] = 0;
        }

    }

    public void draw(int drawX, int drawY){
        int strWidth = 0;
        ScaledResolution sRes = new ScaledResolution(mc);
        for(EquipmentTip equipmentTip:drawList){
            for(String l:equipmentTip.lines){
                int fl = mc.fontRenderer.getStringWidth(l);
                if(fl > strWidth)
                    strWidth = fl;
            }
        }
        drawX = sRes.getScaledWidth() - (strWidth+mc.fontRenderer.FONT_HEIGHT*2+10);
        GlStateManager.pushMatrix();
        try{
            for(EquipmentTip equipmentTip:drawList){
                Render.setColourWithAlphaPercent(0x656565,65);
                Render.drawRect(drawX,drawY,strWidth+mc.fontRenderer.FONT_HEIGHT*2+6,mc.fontRenderer.FONT_HEIGHT*2+5);
//                SkinManager skinManager = SkinManager.getInstance();
//                skinManager.skinValueToModSkin(Config.skinIndex).mergeFontSkin();
//                ResourceLocation gmhead = skinManager.skinValueToModSkin(Config.skinIndex).mergeIcon();
//                Render.resetColour();
//                this.mc.renderEngine.bindTexture(gmhead);
//                Render.drawTexturedRect(drawX+2,drawY+2,mc.fontRenderer.FONT_HEIGHT*2,mc.fontRenderer.FONT_HEIGHT*2);
//                drawY += 2;
                for(String s:equipmentTip.lines){
                    Render.drawString(drawX+/*mc.fontRenderer.FONT_HEIGHT*2+*/5,drawY,0xffffff,s);
                    drawY += mc.fontRenderer.FONT_HEIGHT;
                }
                drawY += 6;
            }
        }catch (Exception e){
            GCUtils.log(e.toString());
        }
        finally {
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }
}
