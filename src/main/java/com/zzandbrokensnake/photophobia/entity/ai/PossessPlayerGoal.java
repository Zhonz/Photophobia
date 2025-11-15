package com.zzandbrokensnake.photophobia.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import com.zzandbrokensnake.photophobia.entity.GhostEntity;

import java.util.EnumSet;

public class PossessPlayerGoal extends Goal {
    private final GhostEntity ghost;
    private final double speed;
    private PlayerEntity targetPlayer;
    private int cooldown = 0;

    public PossessPlayerGoal(GhostEntity ghost, double speed) {
        this.ghost = ghost;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // 冷却检查
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // 如果已经在附身状态，不执行
        if (ghost.isPossessing()) {
            return false;
        }

        // 寻找合适的玩家目标
        this.targetPlayer = findSuitablePlayer();
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldContinue() {
        return !ghost.isPossessing() &&
                targetPlayer != null &&
                targetPlayer.isAlive() &&
                ghost.squaredDistanceTo(targetPlayer) < 16.0 && // 4格范围内
                cooldown <= 0;
    }

    @Override
    public void start() {
        // 开始追逐玩家
        if (targetPlayer != null) {
            ghost.getNavigation().startMovingTo(targetPlayer, speed);
        }
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        // 设置冷却时间
        this.cooldown = 100; // 5秒冷却
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            // 看向玩家
            ghost.getLookControl().lookAt(targetPlayer, 30.0F, 30.0F);

            // 检查是否可以附身
            if (ghost.squaredDistanceTo(targetPlayer) < 4.0) { // 2格范围内
                attemptPossession();
            } else {
                // 继续追逐
                ghost.getNavigation().startMovingTo(targetPlayer, speed);
            }
        }
    }

    private PlayerEntity findSuitablePlayer() {
        // 寻找最近的玩家
        return ghost.getWorld().getClosestPlayer(ghost, 16.0); // 16格范围内
    }

    private void attemptPossession() {
        if (targetPlayer != null && !ghost.isPossessing()) {
            // 尝试附身
            boolean success = ghost.startPossession(targetPlayer, 200); // 10秒附身

            if (success) {
                // 附身成功，停止移动
                ghost.getNavigation().stop();

                // 设置更长的冷却时间
                this.cooldown = 400; // 20秒冷却
            }
        }
    }
}
