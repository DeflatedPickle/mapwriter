package com.cabchinoe.minimap.item;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.gui.MwGui;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class itemTPbook extends Item{
    public final static String itemName = "TPbook";
    public itemTPbook(){

    }


    @Override
    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
        if(!worldIn.isRemote){
            Mw.instance.mc.displayGuiScreen(new MwGui(Mw.instance,playerIn.getHeldItem(handIn),playerIn));
        }
        MwUtil.log("right click");
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

}
