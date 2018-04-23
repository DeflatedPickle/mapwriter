package com.cabchinoe.minimap.item;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.gui.MwGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by n3212 on 2018/1/5.
 */
public class itemTPbook extends Item{
    public final static String itemName = "TPbook";
    public itemTPbook(){

    }


    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer){
        if(!world.isRemote){
            Mw.instance.mc.displayGuiScreen(new MwGui(Mw.instance,itemStack,entityPlayer));
        }
//        MwUtil.log("right click");
        return itemStack;
    }

}
