package com.cabchinoe.gotcha;

import com.cabchinoe.gotcha.gui.BlockTip;
import com.cabchinoe.gotcha.gui.EntityTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MovingObjectPosition;


/**
 * Created by n3212 on 2017/9/19.
 */
public class GotCha {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static GotCha instance;
    public int ticks = 0;
    public GotCha(){
        instance = this;
    }
    public Tracer tracer = new Tracer(this.mc);
    public BlockTip blockTip = new BlockTip(this.mc);
    public EntityTip entityTip = new EntityTip(this.mc);
    public EquipmentListener equipmentListener = new EquipmentListener(this.mc);

    public void OnTick(){
        if(this.ticks % 64 == 0){
            equipmentListener.check();
        }
        if(!(this.mc.currentScreen instanceof GuiScreen)){
            if(this.ticks %16 ==0) {
                tracer.trace();
            }
            int drawX = 4,drawY = 4;
            MovingObjectPosition target = tracer.getCurrent();
            if(target!= null){
                if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
                    blockTip.draw(target);
                }else if(target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY ){
                    drawY = entityTip.draw(target,drawX);
                }
            }
            equipmentListener.draw(drawX,drawY);

        }

        ++this.ticks;
        this.ticks %= 10000;
    }


}
