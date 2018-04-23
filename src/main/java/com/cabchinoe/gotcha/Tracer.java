package com.cabchinoe.gotcha;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by n3212 on 2017/9/20.
 */
public class Tracer {
    private Minecraft mc;
    private MovingObjectPosition current = null;

    private long entitySetTime = System.currentTimeMillis();
    private int range = 24;
    public int hoverTime = 500;

    public Tracer(Minecraft minecraft){
        this.mc = minecraft;
    }

    public MovingObjectPosition getCurrent(){
        return current;
    }

    public void trace(){
        MovingObjectPosition tmp = mc.objectMouseOver;
        if(tmp != null && tmp.typeOfHit != MovingObjectPosition.MovingObjectType.MISS){
            this.dealWithTraceResult(tmp);
        }else {
            EntityLivingBase entity = mc.renderViewEntity;
            tmp = this.getMouseOver(entity);
            if(tmp != null && tmp.typeOfHit != MovingObjectPosition.MovingObjectType.MISS){
                this.dealWithTraceResult(tmp);
            }else {
                this.current = null;
                this.entitySetTime = System.currentTimeMillis();
            }
        }
    }

    private void setResult(MovingObjectPosition result){
        this.entitySetTime = System.currentTimeMillis();
        this.current = result;
    }

    private void dealWithTraceResult(MovingObjectPosition result){
        if(this.current == null ){
            if(result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                this.setResult(result);
            else if(result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
                if(System.currentTimeMillis() - this.entitySetTime >= hoverTime){
                    this.setResult(result);
                }
            }
        }else {
            if(this.current.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK&&result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
                if(System.currentTimeMillis() - this.entitySetTime < hoverTime){
                    this.setResult(null);
                }
            }else {
                this.setResult(result);
            }
        }
    }

    public MovingObjectPosition getMouseOver(EntityLivingBase entity) {
        float partialTicks = 1.0F;
        MovingObjectPosition tmp = null;
        Vec3 targetposition = null;
        if (entity != null) {
            if (this.mc.theWorld != null) {
//                double defaultDistance = (double)this.mc.playerController.getBlockReachDistance();
                Vec3 playerPosition = entity.getPosition(partialTicks);

//                playerPosition = playerPosition.addVector(0,1F,0);
                Vec3 playerLookVec = entity.getLook(partialTicks);
//                Vec3 playerLookAtVec = playerPosition.addVector(playerLookVec.xCoord * defalutDistance, playerLookVec.yCoord * defalutDistance, playerLookVec.zCoord * defalutDistance);
                Vec3 playerLookAtVec = playerPosition.addVector(playerLookVec.xCoord * this.range, playerLookVec.yCoord * this.range, playerLookVec.zCoord * this.range);

                ///////////////////////////////////////////////////////////
                Vec3 playerPosition2 = Vec3.createVectorHelper(playerPosition.xCoord,playerPosition.yCoord,playerPosition.zCoord);

                MovingObjectPosition firstBlock = entity.worldObj.rayTraceBlocks(playerPosition2, playerLookAtVec, true);//true 会拿到水, 无论如何不会拿到岩浆
                double firstBlockDistance = this.range;
                if(firstBlock != null && firstBlock.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Vec3 firstBlockPosition = Vec3.createVectorHelper(firstBlock.blockX,firstBlock.blockY,firstBlock.blockZ);
                    firstBlockDistance = playerPosition.distanceTo(firstBlockPosition);
                }

                Entity pointedEntity = null;
                float f = 2.0F;
                List<Entity> list = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(entity,
                    entity.boundingBox.addCoord(playerLookVec.xCoord * this.range, playerLookVec.yCoord * this.range, playerLookVec.zCoord * this.range).expand((double) f, (double) f, (double) f));
                double lastTargetDistance = this.range;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entityTarget = (Entity) list.get(j);
                    float f1 = entityTarget.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entityTarget.boundingBox.expand((double) f1*4, (double) f1*8, (double) f1*4);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(playerPosition, playerLookAtVec);


                    if (axisalignedbb.isVecInside(playerPosition)) {//无阻碍?
                        if (lastTargetDistance >= 0.0D) {
                            pointedEntity = entityTarget;
                            targetposition = movingobjectposition == null ? playerPosition : movingobjectposition.hitVec;
                            lastTargetDistance = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double targetDistance = playerPosition.distanceTo(movingobjectposition.hitVec);
                        if(firstBlock!=null&& firstBlock.typeOfHit== MovingObjectPosition.MovingObjectType.BLOCK && targetDistance >= firstBlockDistance){
                            continue;
                        }

                        if (targetDistance < lastTargetDistance || lastTargetDistance == 0.0D) {
                            if (entityTarget == entity.ridingEntity && !entity.canRiderInteract()) {
                                if (lastTargetDistance == 0.0D) {
                                    pointedEntity = entityTarget;
                                    targetposition = movingobjectposition.hitVec;
                                }
                            } else {
                                pointedEntity = entityTarget;
                                targetposition = movingobjectposition.hitVec;
                                lastTargetDistance = targetDistance;
                            }
                        }
                    }
                }

//                if (pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D) {
//                    pointedEntity = null;
//                    this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33);
//                }

                if (pointedEntity != null) {
                    tmp = new MovingObjectPosition(pointedEntity, targetposition);
                }

            }
        }
//        GCUtils.log("%s", flag);
        return tmp;
    }
}
