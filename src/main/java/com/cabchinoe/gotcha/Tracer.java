package com.cabchinoe.gotcha;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by n3212 on 2017/9/20.
 */
@SideOnly(Side.CLIENT)
public class Tracer {
    private Minecraft mc;
    private RayTraceResult current = null;

    private long entitySetTime = System.currentTimeMillis();
    private int range = 24;
    public int hoverTime = 500;

    public Tracer(Minecraft minecraft){
        this.mc = minecraft;
    }

    public RayTraceResult getCurrent(){
        return current;
    }

    public void trace(){
        RayTraceResult tmp = mc.objectMouseOver;
        if(tmp != null && tmp.typeOfHit != RayTraceResult.Type.MISS){
            this.dealWithTraceResult(tmp);
        }else {
            Entity entity = mc.getRenderViewEntity();
            tmp = this.getMouseOver(entity);
            if(tmp != null && tmp.typeOfHit != RayTraceResult.Type.MISS){
                this.dealWithTraceResult(tmp);
            }else {
                this.current = null;
                this.entitySetTime = System.currentTimeMillis();
            }
        }
    }

    private void setResult(RayTraceResult result){
        this.entitySetTime = System.currentTimeMillis();
        this.current = result;
    }

    private void dealWithTraceResult(RayTraceResult result){
        if(this.current == null ){
            if(result.typeOfHit == RayTraceResult.Type.BLOCK)
                this.setResult(result);
            else if(result.typeOfHit == RayTraceResult.Type.ENTITY){
                if(System.currentTimeMillis() - this.entitySetTime >= hoverTime){
                    this.setResult(result);
                }
            }
        }else {
            if(this.current.typeOfHit == RayTraceResult.Type.BLOCK&&result.typeOfHit == RayTraceResult.Type.ENTITY){
                if(System.currentTimeMillis() - this.entitySetTime < hoverTime){
                    this.setResult(null);
                }
            }else {
                this.setResult(result);
            }
        }
    }

    public RayTraceResult getMouseOver(Entity entity) {
        float partialTicks = 1.0F;
        RayTraceResult tmp = null;
        Vec3d targetposition = null;
        if (entity != null) {
            if (this.mc.world != null) {
//                double defaultDistance = (double)this.mc.playerController.getBlockReachDistance();
                Vec3d playerPosition = entity.getPositionEyes(partialTicks);

//                playerPosition = playerPosition.addVector(0,1F,0);
                Vec3d playerLookVec = entity.getLook(partialTicks);
//                Vec3 playerLookAtVec = playerPosition.addVector(playerLookVec.xCoord * defalutDistance, playerLookVec.yCoord * defalutDistance, playerLookVec.zCoord * defalutDistance);
                Vec3d playerLookAtVec = playerPosition.addVector(playerLookVec.x * this.range, playerLookVec.y * this.range, playerLookVec.z * this.range);

                ///////////////////////////////////////////////////////////
                Vec3d playerPosition2 = new Vec3d(playerPosition.x,playerPosition.y,playerPosition.z);

                RayTraceResult firstBlock = entity.world.rayTraceBlocks(playerPosition2, playerLookAtVec, true);//true 会拿到水, 无论如何不会拿到岩浆
                double firstBlockDistance = this.range;
                if(firstBlock != null && firstBlock.typeOfHit == RayTraceResult.Type.BLOCK) {
                    Vec3d firstBlockPosition = new Vec3d(firstBlock.getBlockPos());
                    firstBlockDistance = playerPosition.distanceTo(firstBlockPosition);
                }

                Entity pointedEntity = null;
                float f = 2.0F;
                List<Entity> list = this.mc.world.getEntitiesWithinAABBExcludingEntity(entity,
                    entity.getEntityBoundingBox().expand(playerLookVec.x * this.range, playerLookVec.y * this.range, playerLookVec.z * this.range).grow((double) f, (double) f, (double) f));
                double lastTargetDistance = this.range;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entityTarget = (Entity) list.get(j);
                    float f1 = entityTarget.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entityTarget.getEntityBoundingBox().grow((double) f1*4, (double) f1*8, (double) f1*4);
                    RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(playerPosition, playerLookAtVec);


                    if (axisalignedbb.contains(playerPosition)) {//无阻碍?
                        if (lastTargetDistance >= 0.0D) {
                            pointedEntity = entityTarget;
                            targetposition = movingobjectposition == null ? playerPosition : movingobjectposition.hitVec;
                            lastTargetDistance = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double targetDistance = playerPosition.distanceTo(movingobjectposition.hitVec);
                        if(firstBlock!=null&& firstBlock.typeOfHit== RayTraceResult.Type.BLOCK && targetDistance >= firstBlockDistance){
                            continue;
                        }

                        if (targetDistance < lastTargetDistance || lastTargetDistance == 0.0D) {
                            if (entityTarget == entity.getRidingEntity() && !entity.canRiderInteract()) {
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
                    tmp = new RayTraceResult(pointedEntity, targetposition);
                }

            }
        }
//        GCUtils.log("%s", flag);
        return tmp;
    }
}
