package com.cabchinoe.gotcha.gui;

import net.minecraft.util.ResourceLocation;

/**
 * Created by n3212 on 2017/9/22.
 */
public class EntityDesc {

    public String key;
    public attackType entityAttackType;
    public String[] lines;
    public ResourceLocation resourceLocation;
    public EntityDesc(String key, attackType entityAttackType, String[] lines){
        this.key = key;
        this.entityAttackType = entityAttackType;
        this.lines = lines;
        this.resourceLocation = new ResourceLocation("gotcha", "textures/entity/"+this.key.toLowerCase()+".png");
    }

    public static enum attackType{
        neutral, aggressive, BOSS, ally
    }

}