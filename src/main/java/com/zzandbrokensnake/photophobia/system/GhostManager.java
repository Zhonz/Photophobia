package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GhostManager {
    private static final Map<UUID, GhostData> ghostDataMap = new HashMap<>();
    private static final Map<UUID, UUID> possessionMap = new HashMap<>(); // 幽灵UUID -> 目标玩家UUID

    public static class GhostData {
        private final UUID ghostUUID;
        private long creationTime;
        private boolean isActive;

        public GhostData(UUID ghostUUID) {
            this.ghostUUID = ghostUUID;
            this.creationTime = System.currentTimeMillis();
            this.isActive = true;
        }

        public UUID getGhostUUID() {
            return ghostUUID;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            this.isActive = active;
        }

        public boolean shouldExpire() {
            return System.currentTimeMillis() - creationTime > 300000; // 5分钟后过期
        }
    }

    /**
     * 注册幽灵数据
     */
    public static void registerGhost(UUID ghostUUID) {
        ghostDataMap.put(ghostUUID, new GhostData(ghostUUID));
    }

    /**
     * 移除幽灵数据
     */
    public static void removeGhost(UUID ghostUUID) {
        ghostDataMap.remove(ghostUUID);
        // 同时移除附身关系
        possessionMap.entrySet().removeIf(entry -> entry.getValue().equals(ghostUUID));
    }

    /**
     * 获取幽灵数据
     */
    public static GhostData getGhostData(UUID ghostUUID) {
        return ghostDataMap.get(ghostUUID);
    }

    /**
     * 检查幽灵是否活跃
     */
    public static boolean isGhostActive(UUID ghostUUID) {
        GhostData data = ghostDataMap.get(ghostUUID);
        return data != null && data.isActive();
    }

    /**
     * 处理幽灵附身
     */
    public static ActionResult handleGhostPossession(PlayerEntity player, Hand hand, Entity target,
            EntityHitResult hitResult) {
        if (!(target instanceof LivingEntity livingTarget)) {
            return ActionResult.PASS;
        }

        // 检查是否是幽灵
        if (!isGhost(player.getUuid())) {
            return ActionResult.PASS;
        }

        // 检查目标是否可被附身
        if (!canBePossessed(livingTarget)) {
            return ActionResult.FAIL;
        }

        // 执行附身
        if (possessEntity(player, livingTarget)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("已附身 " + livingTarget.getName().getString() + "，按Shift退出"), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    /**
     * 检查实体是否是幽灵
     */
    public static boolean isGhost(UUID entityUUID) {
        return ghostDataMap.containsKey(entityUUID);
    }

    /**
     * 检查实体是否可被附身
     */
    public static boolean canBePossessed(LivingEntity entity) {
        // 玩家不能被附身
        if (entity instanceof PlayerEntity) {
            return false;
        }

        // 怪物可以被附身
        if (entity instanceof Monster) {
            return true;
        }

        // 其他生物需要特殊条件
        return entity.getHealth() < entity.getMaxHealth() * 0.5f;
    }

    /**
     * 执行附身
     */
    public static boolean possessEntity(PlayerEntity ghost, LivingEntity target) {
        if (!canBePossessed(target)) {
            return false;
        }

        // 设置附身关系
        possessionMap.put(ghost.getUuid(), target.getUuid());

        // 应用附身效果
        applyPossessionEffect(target);

        // 幽灵进入旁观模式
        if (ghost instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.changeGameMode(GameMode.SPECTATOR);
        }

        return true;
    }

    /**
     * 退出附身
     */
    public static boolean releasePossession(PlayerEntity ghost) {
        UUID targetUUID = possessionMap.get(ghost.getUuid());
        if (targetUUID == null) {
            return false;
        }

        // 移除附身关系
        possessionMap.remove(ghost.getUuid());

        // 移除附身效果
        if (ghost.getWorld() instanceof ServerWorld serverWorld) {
            Entity target = serverWorld.getEntity(targetUUID);
            if (target instanceof LivingEntity livingTarget) {
                removePossessionEffect(livingTarget);
            }
        }

        // 幽灵返回生存模式
        if (ghost instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.changeGameMode(GameMode.SURVIVAL);
            serverPlayer.sendMessage(Text.literal("已退出附身"), true);
        }

        return true;
    }

    /**
     * 应用附身效果
     */
    private static void applyPossessionEffect(LivingEntity entity) {
        // 这里可以添加附身时的特殊效果
        // 例如：发光、粒子效果等
    }

    /**
     * 移除附身效果
     */
    private static void removePossessionEffect(LivingEntity entity) {
        // 移除附身时的特殊效果
    }

    /**
     * 获取被附身的实体
     */
    public static LivingEntity getPossessedEntity(PlayerEntity ghost) {
        UUID targetUUID = possessionMap.get(ghost.getUuid());
        if (targetUUID == null) {
            return null;
        }

        if (ghost.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(targetUUID);
            return entity instanceof LivingEntity ? (LivingEntity) entity : null;
        }
        return null;
    }

    /**
     * 检查玩家是否正在附身
     */
    public static boolean isPossessing(PlayerEntity player) {
        return possessionMap.containsKey(player.getUuid());
    }

    /**
     * 清理过期的幽灵
     */
    public static void cleanupExpiredGhosts() {
        ghostDataMap.entrySet().removeIf(entry -> entry.getValue().shouldExpire());
    }

    /**
     * 获取活跃幽灵数量
     */
    public static int getActiveGhostCount() {
        return (int) ghostDataMap.values().stream()
                .filter(GhostData::isActive)
                .count();
    }
}
