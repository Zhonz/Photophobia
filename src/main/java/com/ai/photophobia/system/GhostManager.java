package com.ai.photophobia.system;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 幽灵管理器 - 处理玩家死亡后的幽灵状态和附身机制
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class GhostManager {
    private static final Map<UUID, GhostData> ghostPlayers = new HashMap<>();
    private static final int GHOST_DURATION = 3 * 60 * 20; // 3分钟 (ticks)
    private static final int DEATH_SHADOW_DURATION = 5 * 60 * 20; // 5分钟 (ticks)

    public static class GhostData {
        private final UUID playerUUID;
        private final long deathTime;
        private UUID possessedEntity = null;
        private boolean isGhostMode = false;

        public GhostData(UUID playerUUID) {
            this.playerUUID = playerUUID;
            this.deathTime = System.currentTimeMillis();
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public long getDeathTime() {
            return deathTime;
        }

        public UUID getPossessedEntity() {
            return possessedEntity;
        }

        public void setPossessedEntity(UUID entityUUID) {
            this.possessedEntity = entityUUID;
        }

        public boolean isGhostMode() {
            return isGhostMode;
        }

        public void setGhostMode(boolean ghostMode) {
            this.isGhostMode = ghostMode;
        }

        public boolean isPossessing() {
            return possessedEntity != null;
        }

        public long getRemainingGhostTime() {
            long elapsed = System.currentTimeMillis() - deathTime;
            return Math.max(0, GHOST_DURATION * 50 - elapsed); // 转换为毫秒
        }
    }

    /**
     * 玩家死亡时进入幽灵模式
     */
    public static void enterGhostMode(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        GhostData ghostData = new GhostData(playerUUID);
        ghostData.setGhostMode(true);
        ghostPlayers.put(playerUUID, ghostData);

        // 设置幽灵模式
        player.changeGameMode(GameMode.SPECTATOR);
        player.sendMessage(Text.literal("你已进入幽灵模式，可以附身怪物（按Shift退出）"), true);

        // 生成死亡残影
        createDeathShadow(player);
    }

    /**
     * 创建死亡残影（同名诡异类怪物）
     */
    private static void createDeathShadow(ServerPlayerEntity player) {
        // TODO: 实现死亡残影生成
        // 生成名为"[玩家名]的残影"的半透明怪物
        // 在死亡点游荡，接近其他玩家时触发心率上升
        player.sendMessage(Text.literal("你的残影已生成，将在5分钟后消失"), true);
    }

    /**
     * 附身怪物
     */
    public static ActionResult possessEntity(ServerPlayerEntity player, Entity target) {
        if (!(target instanceof LivingEntity) || target instanceof PlayerEntity) {
            return ActionResult.PASS;
        }

        // 不能附身Boss
        if (isBossEntity((LivingEntity) target)) {
            player.sendMessage(Text.literal("无法附身强大的Boss"), true);
            return ActionResult.FAIL;
        }

        GhostData ghostData = ghostPlayers.get(player.getUuid());
        if (ghostData == null || !ghostData.isGhostMode()) {
            return ActionResult.PASS;
        }

        // 开始附身
        ghostData.setPossessedEntity(target.getUuid());

        // 设置玩家为旁观者模式，跟随被附身实体
        player.setCameraEntity(target);

        // 给被附身实体添加发光效果
        addPossessionEffect((LivingEntity) target);

        player.sendMessage(Text.literal("已附身 " + target.getName().getString() + "，按Shift退出"), true);

        return ActionResult.SUCCESS;
    }

    /**
     * 退出附身
     */
    public static void exitPossession(ServerPlayerEntity player) {
        GhostData ghostData = ghostPlayers.get(player.getUuid());
        if (ghostData == null || !ghostData.isPossessing()) {
            return;
        }

        // 移除被附身实体的效果
        Entity possessedEntity = player.getServerWorld().getEntity(ghostData.getPossessedEntity());
        if (possessedEntity instanceof LivingEntity) {
            removePossessionEffect((LivingEntity) possessedEntity);
        }

        // 重置玩家视角
        player.setCameraEntity(player);
        ghostData.setPossessedEntity(null);

        player.sendMessage(Text.literal("已退出附身"), true);
    }

    /**
     * 检查是否是Boss实体
     */
    private static boolean isBossEntity(LivingEntity entity) {
        // TODO: 实现Boss检测逻辑
        // 暂时返回false
        return false;
    }

    /**
     * 添加附身效果（发光等）
     */
    private static void addPossessionEffect(LivingEntity entity) {
        // TODO: 实现附身视觉效果
        // 被附身实体发出蓝光
    }

    /**
     * 移除附身效果
     */
    private static void removePossessionEffect(LivingEntity entity) {
        // TODO: 实现移除附身效果
    }

    /**
     * 更新幽灵状态
     */
    public static void updateGhostStates() {
        long currentTime = System.currentTimeMillis();

        for (GhostData ghostData : ghostPlayers.values()) {
            if (ghostData.getRemainingGhostTime() <= 0) {
                // 幽灵时间结束，强制复活
                forceRespawn(ghostData.getPlayerUUID());
            }
        }
    }

    /**
     * 强制复活玩家
     */
    private static void forceRespawn(UUID playerUUID) {
        // TODO: 实现强制复活逻辑
        ghostPlayers.remove(playerUUID);
    }

    /**
     * 检查玩家是否在幽灵模式
     */
    public static boolean isInGhostMode(PlayerEntity player) {
        GhostData ghostData = ghostPlayers.get(player.getUuid());
        return ghostData != null && ghostData.isGhostMode();
    }

    /**
     * 检查玩家是否在附身状态
     */
    public static boolean isPossessing(PlayerEntity player) {
        GhostData ghostData = ghostPlayers.get(player.getUuid());
        return ghostData != null && ghostData.isPossessing();
    }

    /**
     * 获取被附身的实体
     */
    public static Entity getPossessedEntity(PlayerEntity player) {
        GhostData ghostData = ghostPlayers.get(player.getUuid());
        if (ghostData == null || !ghostData.isPossessing()) {
            return null;
        }
        // 在Minecraft 1.21.1中，需要通过服务器获取实体
        if (player instanceof ServerPlayerEntity serverPlayer) {
            return serverPlayer.getServerWorld().getEntity(ghostData.getPossessedEntity());
        }
        return null;
    }

    /**
     * 处理玩家互动（附身尝试）
     */
    public static ActionResult handlePlayerInteraction(ServerPlayerEntity player, Hand hand,
            EntityHitResult hitResult) {
        if (!isInGhostMode(player) || isPossessing(player)) {
            return ActionResult.PASS;
        }

        Entity target = hitResult.getEntity();
        if (target instanceof LivingEntity && !(target instanceof PlayerEntity)) {
            return possessEntity(player, target);
        }

        return ActionResult.PASS;
    }

    /**
     * 处理Shift键按下（退出附身）
     */
    public static void handleSneakKey(ServerPlayerEntity player) {
        if (isPossessing(player)) {
            exitPossession(player);
        }
    }

    /**
     * 玩家复活时清理幽灵数据
     */
    public static void onPlayerRespawn(ServerPlayerEntity player) {
        ghostPlayers.remove(player.getUuid());
    }
}
