package com.cabchinoe.gotcha.gui;


/**
 * Created by n3212 on 2017/9/26.
 */
public class EquipmentTip {
    public String itemName;
    public long showTime = System.currentTimeMillis();
    public String lines[] ;
    public EquipmentTip(String itemName){
        this.itemName = itemName;
        this.lines = new String[]{
            String.format("亲爱的玩家, 您的§l[%s]§r",this.itemName),
            "耐久度已不足10%%, 请您留意哦~",
        };
    }
}
