package com.cabchinoe.gotcha.gui;

import com.cabchinoe.common.Render;
import com.cabchinoe.gotcha.GCUtils;
import com.cabchinoe.gotcha.GotChaForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;


/**
 * Created by n3212 on 2017/9/21.
 */
public class BlockTip {
    public Minecraft mc;
    public BlockTip(Minecraft mc){
        this.mc = mc;
    }

    public void draw(MovingObjectPosition movingObjectPosition){
        ScaledResolution sRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glPushMatrix();
        int x = movingObjectPosition.blockX, y = movingObjectPosition.blockY,z = movingObjectPosition.blockZ;
        try{
            Block block = this.mc.theWorld.getBlock(x,y,z);
            Item item =  Item.getItemFromBlock(block);
            String blockLocalizedName = "";
            String modName = modName = getModName(block);
            if(item == null){
                blockLocalizedName = block.getLocalizedName();
//                if(block instanceof BlockStem || block instanceof BlockCrops){
//                    String[] typeName = block.delegate.name().split(":");
//                    if(typeName.length == 2){
//                        blockLocalizedName = I18n.format(String.format("item."))
//                    }
//                }
                if("tile.air.name".equals(blockLocalizedName)){
                    return;
                }
                ///////////////////////////////////////////////////////////////////////////
                //农作物等问题
                String[] typeName = block.delegate.name().split(":");
                if(typeName.length ==2) {
                    String realName = typeName[1];
                    if (realName.equals("wheat")) {
                        blockLocalizedName = I18n.format("item.wheat.name");
                    }else if(realName.contains("pumpkin_stem")){
                        blockLocalizedName = I18n.format("tile.pumpkin.name");
                    }else if(realName.contains("melon_stem")){
                        blockLocalizedName = I18n.format("tile.melon.name");
                    }else {
                        //////////////////////////////
                        //其他翻译不准确问题
                        if(blockLocalizedName.contains("tile.")) {
                            blockLocalizedName = blockLocalizedName.replace("tile.","item.");
                            blockLocalizedName = I18n.format(blockLocalizedName);
                        }
                        //替换后还是不准确
                        if(blockLocalizedName.contains("item.skull")){
                            int a = block.getDamageValue(mc.theWorld,x,y,z);
                            item = block.getItem(mc.theWorld,x,y,z);
                            ItemStack itemStack = new ItemStack(item, 1, a);
                            blockLocalizedName = itemStack.getTooltip(mc.thePlayer, false).get(0).toString();
                        }
                    }
                }
                //////////////////////////////////////////////////////////////////////



            }else {

                ItemStack itemStack = new ItemStack(item, 1, mc.theWorld.getBlockMetadata(x, y, z));
                blockLocalizedName = itemStack.getTooltip(mc.thePlayer, false).get(0).toString();

                ////////////////////////////////////////////////////////////////////////////////////
                //双层植物问题
                String sunflower = I18n.format("tile.doublePlant.sunflower.name");
                if(sunflower.equals(blockLocalizedName)){
                    block = this.mc.theWorld.getBlock(x,y-1,z);
                    if(block instanceof BlockDoublePlant){
                        item =  Item.getItemFromBlock(block);
                        itemStack = new ItemStack(item, 1, mc.theWorld.getBlockMetadata(x, y-1, z));
                        blockLocalizedName = itemStack.getTooltip(mc.thePlayer, false).get(0).toString();
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////

            }
//            GCUtils.log("HHHHHHHHHHHHH-------------------%s : %s", modName,blockLocalizedName);
            blockLocalizedName = this.replace(blockLocalizedName);
            int stringWidth = mc.fontRenderer.getStringWidth(blockLocalizedName);
            int rectHeight = mc.fontRenderer.FONT_HEIGHT+2;
            if(modName!=null){
                modName = String.format("Mod : %s",modName);
                stringWidth = Math.max(stringWidth,mc.fontRenderer.getStringWidth(modName));
                rectHeight += mc.fontRenderer.FONT_HEIGHT+5;
            }
            int drawX = sRes.getScaledWidth()/2 - stringWidth/2;
            Render.setColourWithAlphaPercent(0x3a3a3a, 70);
            Render.drawRect(drawX,2,stringWidth + 16,rectHeight + 5);
//            Render.setColourWithAlphaPercent(0xeeeeee, 100);
//            Render.drawString(drawX+8,5,0xeeeeeeee, blockLocalizedName);
            Render.drawCentredString(drawX,5,stringWidth + 16,mc.fontRenderer.FONT_HEIGHT+5,0xeeeeeeee, blockLocalizedName);
            if(modName!=null){
                Render.drawCentredString(drawX,mc.fontRenderer.FONT_HEIGHT+5,stringWidth + 16,mc.fontRenderer.FONT_HEIGHT*2,0x22B8DDee, modName);
            }


        }catch (Exception e){
            GCUtils.log(e.toString());
        }finally {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }

        // some shader mods seem to need depth testing re-enabled

    }

    private String getModName(Block block){
        if(block == null){
            return null;
        }
        String delegateName = block.delegate.name();
        String ModID = delegateName.split(":")[0];
        if(ModID.equals("minecraft")){
            return null;
        }
        return GotChaForge.ModMap.get(ModID);
    }

    private String replace(String s){
        return s.replace("錾","錾(zàn)").replace("曜","曜(yào)")
            .replace("罂","罂(yīng)").replace("蕨","蕨(jué)")
            .replace("燧","燧(suì)").replace("疣","疣(yóu)")
            .replace("砾","砾(lì)").replace("釉","釉(yòu)")
            .replace("砧","砧(zhēn)");

    }

}
