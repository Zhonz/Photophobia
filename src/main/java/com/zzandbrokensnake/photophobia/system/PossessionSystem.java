package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import com.zzandbrokensnake.photophobia.entity.GhostEntity;

public class PossessionSystem {

    /**
     * 开始附身玩家
     */
    public static void startPossession(GhostEntity ghost, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // 使用GhostEntity的现有方法开始附身
            boolean success = ghost.startPossession(serverPlayer, 200); // 10秒附身

            if (success) {
                // 发送附身消息给玩家
                serverPlayer.sendMessage(
                        net.minecraft.text.Text.translatable("message.photophobia.ghost_possession"),
                        true);

                // 设置玩家无敌状态
                serverPlayer.setInvulnerable(true);
            }
        }
    }

    /**
     * 结束附身
     */
    public static void endPossession(GhostEntity ghost) {
        PlayerEntity player = ghost.getPossessedPlayer();
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // 清除无敌状态
            serverPlayer.setInvulnerable(false);

            // 发送释放消息
            serverPlayer.sendMessage(
                    net.minecraft.text.Text.translatable("message.photophobia.ghost_released"),
                    true);
        }

        // 调用GhostEntity的结束附身方法
        ghost.endPossession();
    }

    /**
     * 检查是否可以附身玩家
     */
    public static boolean canPossessPlayer(GhostEntity ghost, PlayerEntity player) {
        // 检查距离
        double distance = ghost.squaredDistanceTo(player);
        if (distance > 16.0) { // 4格距离
            return false;
        }

        // 检查玩家是否已经有藏匿效果
        if (player.hasStatusEffect(com.zzandbrokensnake.photophobia.registry.StatusEffectRegistry.CONCEALMENT_EFFECT)) {
            return false;
        }

        // 检查玩家是否已经在被附身
        if (ghost.isPossessing()) {
            return false;
        }

        // 随机几率
        return player.getRandom().nextFloat() < 0.1f; // 10%几率
    }

    /**
     * 更新附身状态
     */
    public static void updatePossession(GhostEntity ghost, ServerWorld world) {
        if (!ghost.isPossessing()) {
            return;
        }

        PlayerEntity player = ghost.getPossessedPlayer();
        if (player == null || !player.isAlive()) {
            endPossession(ghost);
            return;
        }

        // 控制玩家移动
        controlPlayerMovement(ghost, player);
    }

    /**
     * 控制被附身玩家的移动
     */
    private static void controlPlayerMovement(GhostEntity ghost, PlayerEntity player) {
        // 随机移动方向
        if (player.getRandom().nextInt(20) == 0) {
            double dx = (player.getRandom().nextDouble() - 0.5) * 2.0;
            double dz = (player.getRandom().nextDouble() - 0.5) * 2.0;

            Vec3d velocity = new Vec3d(dx, 0, dz).normalize().multiply(0.3);
            player.setVelocity(velocity);
            player.velocityModified = true;
        }

        // 随机跳跃
        if (player.getRandom().nextInt(40) == 0 && player.isOnGround()) {
            player.jump();
        }
    }
}
