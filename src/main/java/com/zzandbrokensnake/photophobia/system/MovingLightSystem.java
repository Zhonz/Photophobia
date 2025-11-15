package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.entity.GhostEntity;
import com.zzandbrokensnake.photophobia.items.LanternItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 移动光源系统 - 基于Minecraft原生功能实现移动光源效果
 */
public class MovingLightSystem {

    private static final Map<UUID, LightData> entityLightData = new HashMap<>();

    public static class LightData {
        private int lightLevel = 0;
        private long lastUpdateTime = 0;
        private boolean isActive = false;
        private LightSourceType sourceType;

        public LightData(LightSourceType sourceType) {
            this.sourceType = sourceType;
        }

        public int getLightLevel() {
            return lightLevel;
        }

        public void setLightLevel(int level) {
            this.lightLevel = level;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            this.isActive = active;
        }

        public LightSourceType getSourceType() {
            return sourceType;
        }
    }

    public enum LightSourceType {
        LANTERN(15), // 提灯 - 强光源
        GLOWING_MUSHROOM(8), // 荧光蘑菇 - 中等光源
        GHOST(10), // 幽灵 - 特殊光源
        PLAYER_HEARTBEAT(5); // 玩家心跳 - 弱光源

        private final int baseLightLevel;

        LightSourceType(int baseLightLevel) {
            this.baseLightLevel = baseLightLevel;
        }

        public int getBaseLightLevel() {
            return baseLightLevel;
        }
    }

    /**
     * 初始化移动光源系统
     */
    public static void initialize() {
        PhotophobiaMod.LOGGER.info("初始化移动光源系统");
    }

    /**
     * 获取幽灵实体的光照等级
     */
    public static int getGhostLightLevel(GhostEntity ghost) {
        // 幽灵根据状态提供不同的光照
        if (ghost.isPossessing()) {
            return 12; // 附身时强光
        } else {
            return 8; // 普通状态中等光
        }
    }

    /**
     * 为实体添加移动光源
     */
    public static void addMovingLight(Entity entity, LightSourceType sourceType) {
        UUID entityId = entity.getUuid();
        LightData data = new LightData(sourceType);
        data.setActive(true);
        data.setLightLevel(sourceType.getBaseLightLevel());
        entityLightData.put(entityId, data);

        PhotophobiaMod.LOGGER.debug("为实体 {} 添加移动光源: {}", entity.getName().getString(), sourceType);
    }

    /**
     * 移除实体的移动光源
     */
    public static void removeMovingLight(Entity entity) {
        entityLightData.remove(entity.getUuid());
        PhotophobiaMod.LOGGER.debug("移除实体 {} 的移动光源", entity.getName().getString());
    }

    /**
     * 更新实体的移动光源
     */
    public static void updateMovingLight(Entity entity, int lightLevel) {
        UUID entityId = entity.getUuid();
        LightData data = entityLightData.get(entityId);
        if (data != null && data.isActive()) {
            data.setLightLevel(MathHelper.clamp(lightLevel, 0, 15));
        }
    }

    /**
     * 获取实体的光照等级
     */
    public static int getEntityLightLevel(Entity entity) {
        LightData data = entityLightData.get(entity.getUuid());
        if (data != null && data.isActive()) {
            return data.getLightLevel();
        }
        return 0;
    }

    /**
     * 检查实体是否有移动光源
     */
    public static boolean hasMovingLight(Entity entity) {
        LightData data = entityLightData.get(entity.getUuid());
        return data != null && data.isActive();
    }

    /**
     * 根据玩家心率动态调整光照
     */
    public static void updatePlayerHeartbeatLight(PlayerEntity player) {
        int heartRate = HeartRateManager.getHeartRate(player.getUuid());

        // 心率越高，光照越强（模拟紧张时的视觉增强）
        int lightLevel = MathHelper.clamp(heartRate / 20, 0, 8);

        if (lightLevel > 0) {
            addMovingLight(player, LightSourceType.PLAYER_HEARTBEAT);
            updateMovingLight(player, lightLevel);
        } else {
            removeMovingLight(player);
        }
    }

    /**
     * 为趋光性怪物添加光源检测
     */
    public static boolean hasLightSourceNearby(LivingEntity entity, int range) {
        World world = entity.getWorld();
        BlockPos entityPos = entity.getBlockPos();

        // 检查周围的实体光源
        for (Entity nearbyEntity : world.getOtherEntities(entity,
                entity.getBoundingBox().expand(range),
                e -> hasMovingLight(e))) {

            int lightLevel = getEntityLightLevel(nearbyEntity);
            if (lightLevel > 5) { // 只对强光源有反应
                return true;
            }
        }

        // 检查方块光源
        int blockLight = world.getLightLevel(entityPos);
        return blockLight > 7;
    }

    /**
     * 清理过期的光源数据
     */
    public static void cleanupExpiredLights() {
        long currentTime = System.currentTimeMillis();
        entityLightData.entrySet().removeIf(entry -> {
            LightData data = entry.getValue();
            // 如果光源超过5分钟没有更新，清理掉
            return currentTime - data.lastUpdateTime > 300000;
        });
    }
}
