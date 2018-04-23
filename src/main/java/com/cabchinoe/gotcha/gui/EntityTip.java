package com.cabchinoe.gotcha.gui;

import com.cabchinoe.common.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * Created by n3212 on 2017/9/21.
 */
public class EntityTip {
    public Minecraft mc;
    private HashMap<String,EntityDesc> entityDescMap = new HashMap<String, EntityDesc>();
    public EntityTip(Minecraft mc){
        this.mc = mc;
        entityDescMap.put("CaveSpider",new EntityDesc(
            "CaveSpider", EntityDesc.attackType.neutral,new String[]{
            "§c快速击杀！",
            "§f洞穴蜘蛛实际攻击力很低",
            "§f但会造成中毒, 需要迅速击杀"
        }));
        entityDescMap.put("Blaze",new EntityDesc(
            "Blaze", EntityDesc.attackType.aggressive,new String[]{
            "§c抗火药水! 雪球! ",
            "§f烈焰人所有攻击都与火相关,",
            "§f雪球会对烈焰人造成伤害"
        }));
        entityDescMap.put("Skeleton",new EntityDesc(
            "Skeleton", EntityDesc.attackType.aggressive,new String[]{
            "§c利用掩体!",
            "§f距离骷髅越近，骷髅射击越快,",
            "§f被攻击时在掩体后等待伏击骷髅"
        }));
        entityDescMap.put("Shulker",new EntityDesc(
            "Shulker", EntityDesc.attackType.aggressive,new String[]{
            "§c全副武装!",
            "§f不要与大量潜影贝同时作战!",
            "§f盾牌和盔甲能有效阻挡伤害"
        }));
        entityDescMap.put("Witch",new EntityDesc(
            "Witch", EntityDesc.attackType.aggressive,new String[]{
            "§c远程射杀!",
            "§f女巫会使用和投掷各种药水,",
            "§f注意状态, 作好持久战的准备"
        }));
        entityDescMap.put("Enderman",new EntityDesc(
            "Enderman", EntityDesc.attackType.neutral,new String[]{
            "§c不要对视!",
            "§f末影人生命值和攻击极高,",
            "§f与它对视后会主动攻击玩家"
        }));
        entityDescMap.put("Creeper",new EntityDesc(
            "Creeper", EntityDesc.attackType.aggressive,new String[]{
            "§c易爆! 远离",
            "§f爬行者在玩家过于接近时,",
            "§f会在几秒后发生爆炸"
        }));
        entityDescMap.put("Endermite",new EntityDesc(
            "Endermite", EntityDesc.attackType.aggressive,new String[]{
            "§c障碍击杀!",
            "§f末影螨的攻击高度较低,",
            "§f站在高台可有效防止被攻击"
        }));
        entityDescMap.put("Stray",new EntityDesc(
            "Stray", EntityDesc.attackType.aggressive,new String[]{
            "§c举起盾牌!",
            "§f流髑是骷髅的雪地变种,",
            "§f盾牌能有效阻挡弓箭的伤害"
        }));
        entityDescMap.put("Spider",new EntityDesc(
            "Spider", EntityDesc.attackType.neutral,new String[]{
            "§c障碍击杀!",
            "§f蜘蛛在黑暗时会攻击玩家,",
            "§f无法穿过宽为1格的空间"
        }));
        entityDescMap.put("Ghast",new EntityDesc(
            "Ghast", EntityDesc.attackType.aggressive,new String[]{
            "§c障碍击杀!",
            "§f恶魂会向玩家发射火球,",
            "§f火球可通过攻击反弹"
        }));
        entityDescMap.put("Zombie",new EntityDesc(
            "Zombie", EntityDesc.attackType.aggressive,new String[]{
            "§c且战且退!",
            "§f僵尸速度较慢(小僵尸除外),",
            "§f站在两格高台上可防止被围攻"
        }));
        entityDescMap.put("ElderGuardian",new EntityDesc(
            "ElderGuardian", EntityDesc.attackType.aggressive,new String[]{
            "§c全副武装!保持距离!",
            "§f远古守卫者拥有80点生命!",
            "§f距离过近会被它们用尖刺反击"
        }));
        entityDescMap.put("Wolf",new EntityDesc(
            "Wolf", EntityDesc.attackType.neutral,new String[]{
            "§c不要惹它!",
            "§f狼可用骨头驯服, 成为好帮手;",
            "§f未驯服的狼被攻击后会反击玩家"
        }));
        entityDescMap.put("LavaSlime",new EntityDesc(
            "LavaSlime", EntityDesc.attackType.aggressive,new String[]{
            "§c且战且退!",
            "§f岩浆怪被打倒后会分裂,",
            "§f小岩浆怪仍能造成伤害"
        }));
        entityDescMap.put("Slime",new EntityDesc(
            "Slime", EntityDesc.attackType.aggressive,new String[]{
            "§c且战且退!",
            "§f史莱姆被打倒后会分裂,",
            "§f小史莱姆不会造成伤害"
        }));
        entityDescMap.put("VindicationIllager",new EntityDesc(
            "VindicationIllager", EntityDesc.attackType.aggressive,new String[]{
            "§c且战且退!",
            "§f卫道士和僵尸攻击模式类似;",
            "§f站在两格高台上可防止被围攻"
        }));
        entityDescMap.put("PolarBear",new EntityDesc(
            "PolarBear", EntityDesc.attackType.neutral,new String[]{
            "§c不要惹它!",
            "§f北极熊在被玩家攻击后变为敌对,",
            "§f攻击小北极熊会被成年北极熊围攻"
        }));
        entityDescMap.put("WitherSkeleton",new EntityDesc(
            "WitherSkeleton", EntityDesc.attackType.aggressive,new String[]{
            "§c障碍击杀!",
            "§f凋灵骷髅超过两格高,",
            "§f第三格搭建障碍物可阻挡"
        }));
        entityDescMap.put("EvocationIllager",new EntityDesc(
            "EvocationIllager", EntityDesc.attackType.aggressive,new String[]{
            "§c乱剑压制!远程射杀!",
            "§f唤魔者会召唤恼鬼和尖牙;",
            "§f它没有超远程攻击,可以射杀"
        }));
        entityDescMap.put("PigZombie",new EntityDesc(
            "PigZombie", EntityDesc.attackType.neutral,new String[]{
            "§c不要惹它!",
            "§f僵尸猪人一旦被攻击,",
            "§f会成群结队进行反击"
        }));
        entityDescMap.put("Silverfish",new EntityDesc(
            "Silverfish", EntityDesc.attackType.aggressive,new String[]{
            "§c障碍击杀!",
            "§f蠹虫的攻击高度较低,",
            "§f站在高台可有效防止被攻击"
        }));
        entityDescMap.put("Guardian",new EntityDesc(
            "Guardian", EntityDesc.attackType.aggressive,new String[]{
            "§c各个击破!",
            "§f守卫者大多成群生活,",
            "§f可以吸引到掩体后逐个击杀"
        }));
        entityDescMap.put("Vex",new EntityDesc(
            "Vex", EntityDesc.attackType.aggressive,new String[]{
            "§c击杀唤魔者!",
            "§f恼鬼会不断被唤魔者召唤,",
            "§f它的攻击不受掩体影响"
        }));
        entityDescMap.put("EnderDragon",new EntityDesc(
            "EnderDragon", EntityDesc.attackType.BOSS,new String[]{
            "§c全副武装!",
            "§f末影龙血量极多,危险性极高;",
            "§f先打爆末影水晶,再击杀末影龙"
        }));
        entityDescMap.put("VillagerGolem",new EntityDesc(
            "VillagerGolem", EntityDesc.attackType.ally,new String[]{
            "§c不要惹它和它的村民!",
            "§f自然生成的铁傀儡会保护村民,",
            "§f攻击它或范围内的村民会激怒它"
        }));
        entityDescMap.put("WitherBoss",new EntityDesc(
            "WitherBoss", EntityDesc.attackType.BOSS,new String[]{
            "§c全副武装!喝下药水!",
            "§f凋灵血量极多,范围破坏力极强;",
            "§f可先用弓箭射击,再用剑迅速杀死"
        }));

    }

    public int picWidth = 50, picHeight = 75, marginY =4;
    public int draw(MovingObjectPosition movingObjectPosition, int marginX){
        Entity entity = movingObjectPosition.entityHit;
        String entityNameID = EntityList.getEntityString(entity);
        if(entityNameID != null){
            EntityDesc entityDesc = entityDescMap.get(entityNameID);
            if(entityDesc != null){
                String entityName = "§l"+entity.getCommandSenderName();
                entityName = entityName.replace("蠹","蠹(dù)").replace("髑","髑(dú)");
                GL11.glPushMatrix();
                try{
                    Render.setColourWithAlphaPercent(0x656565,65);
                    int strWidth = 0;
                    for(String s : entityDesc.lines){
                        int fl = mc.fontRenderer.getStringWidth(s);
                        if(fl >= strWidth)
                            strWidth = fl;
                    }
                    Render.drawRect(marginX,marginY,strWidth+picWidth +marginX*2,this.picHeight+marginY);
                    Render.setColour(0xffffffff);
                    this.mc.renderEngine.bindTexture(entityDesc.resourceLocation);
                    Render.drawTexturedRect(marginX+2,marginY+2,this.picWidth,this.picHeight);
                    int TextX = this.picWidth + 5 + marginX ;
                    Render.drawString(TextX, marginY*2, 0xffffff,entityName);
                    int renderY = 25;
                    if(entityDesc.entityAttackType ==  EntityDesc.attackType.aggressive){
                        Render.drawString(TextX, renderY, 0xff0000,"§c攻击型");
                    }else if(entityDesc.entityAttackType ==  EntityDesc.attackType.BOSS){
                        Render.drawString(TextX, renderY, 0xff0000,"§c§lBOSS!!");
                    }else if(entityDesc.entityAttackType ==  EntityDesc.attackType.neutral){
                        Render.drawString(TextX, renderY, 0xffff00,"中立型");
                    }else if(entityDesc.entityAttackType ==  EntityDesc.attackType.ally){
                        Render.drawString(TextX, renderY, 0x00ff00,"效用型");
                    }
                    renderY = 40;
                    for(String s : entityDesc.lines){
                        Render.drawString(TextX, renderY, 0xffffff,s);
                        renderY += mc.fontRenderer.FONT_HEIGHT;
                    }
                }catch (Exception e){}
                finally {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glPopMatrix();
                    return this.picHeight + 10;
                }
            }

        }
        return marginY;
    }



}

