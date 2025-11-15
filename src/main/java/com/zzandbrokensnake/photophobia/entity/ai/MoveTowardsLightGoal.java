package com.zzandbrokensnake.photophobia.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoveTowardsLightGoal extends Goal {
    private final MobEntity mob;
    private final double speed;
    private BlockPos targetPos;

    public MoveTowardsLightGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        // 寻找附近的光源
        this.targetPos = findNearestLightSource();
        return this.targetPos != null;
    }

    @Override
    public void start() {
        if (this.targetPos != null) {
            this.mob.getNavigation().startMovingTo(
                    this.targetPos.getX(),
                    this.targetPos.getY(),
                    this.targetPos.getZ(),
                    this.speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPos != null &&
                !this.mob.getNavigation().isIdle() &&
                this.mob.squaredDistanceTo(
                        this.targetPos.getX(),
                        this.targetPos.getY(),
                        this.targetPos.getZ()) > 4.0;
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.mob.getNavigation().stop();
    }

    private BlockPos findNearestLightSource() {
        World world = this.mob.getWorld();
        BlockPos mobPos = this.mob.getBlockPos();
        BlockPos nearestLight = null;
        double nearestDistance = Double.MAX_VALUE;

        // 搜索半径内的光源
        int searchRadius = 16;
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = mobPos.add(x, y, z);

                    // 检查光照等级
                    int lightLevel = world.getLightLevel(checkPos);
                    if (lightLevel >= 8) { // 足够亮的光源
                        double distance = mobPos.getSquaredDistance(checkPos);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestLight = checkPos;
                        }
                    }
                }
            }
        }

        return nearestLight;
    }
}
