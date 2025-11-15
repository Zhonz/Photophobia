package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import java.util.List;
import java.util.stream.Collectors;

public class TeamResonanceSystem {

    /**
     * 更新团队心率共鸣
     */
    public static void updateTeamResonance(ServerWorld world) {
        // 获取所有在线玩家
        List<ServerPlayerEntity> players = world.getPlayers();

        // 按团队分组（基于距离）
        List<List<ServerPlayerEntity>> teams = groupPlayersByProximity(players);

        // 为每个团队应用共鸣效果
        for (List<ServerPlayerEntity> team : teams) {
            applyTeamResonance(team);
        }
    }

    /**
     * 根据距离将玩家分组为团队
     */
    private static List<List<ServerPlayerEntity>> groupPlayersByProximity(List<ServerPlayerEntity> players) {
        return players.stream()
                .collect(Collectors.groupingBy(player -> {
                    // 找到最近的玩家作为团队中心
                    return players.stream()
                            .filter(other -> other != player)
                            .min((a, b) -> Double.compare(
                                    a.squaredDistanceTo(player),
                                    b.squaredDistanceTo(player)))
                            .orElse(player);
                }))
                .values()
                .stream()
                .filter(team -> team.size() > 1) // 只处理有多个玩家的团队
                .collect(Collectors.toList());
    }

    /**
     * 为团队应用心率共鸣效果
     */
    private static void applyTeamResonance(List<ServerPlayerEntity> team) {
        if (team.size() < 2)
            return;

        // 计算团队平均心率
        double averageHeartRate = team.stream()
                .mapToInt(player -> HeartRateManager.getHeartRate(player.getUuid()))
                .average()
                .orElse(60.0);

        // 计算团队心率标准差（用于衡量心率同步度）
        double standardDeviation = calculateHeartRateStandardDeviation(team, averageHeartRate);

        // 应用共鸣效果
        for (ServerPlayerEntity player : team) {
            applyResonanceEffect(player, averageHeartRate, standardDeviation, team.size());
        }
    }

    /**
     * 计算团队心率标准差
     */
    private static double calculateHeartRateStandardDeviation(List<ServerPlayerEntity> team, double average) {
        double variance = team.stream()
                .mapToDouble(player -> Math.pow(HeartRateManager.getHeartRate(player.getUuid()) - average, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    /**
     * 为单个玩家应用共鸣效果
     */
    private static void applyResonanceEffect(ServerPlayerEntity player, double teamAverage,
            double standardDeviation, int teamSize) {
        int playerHeartRate = HeartRateManager.getHeartRate(player.getUuid());

        // 计算心率同步度（标准差越小，同步度越高）
        double syncLevel = Math.max(0, 1.0 - (standardDeviation / 20.0));

        // 应用共鸣效果
        if (syncLevel > 0.7) { // 高度同步
            applyHighSyncEffects(player, teamAverage, teamSize);
        } else if (syncLevel > 0.3) { // 中等同步
            applyMediumSyncEffects(player, teamAverage, teamSize);
        } else { // 低同步
            applyLowSyncEffects(player, teamAverage, teamSize);
        }
    }

    /**
     * 高度同步效果
     */
    private static void applyHighSyncEffects(ServerPlayerEntity player, double teamAverage, int teamSize) {
        int playerHeartRate = HeartRateManager.getHeartRate(player.getUuid());

        // 向团队平均值靠拢
        double adjustment = (teamAverage - playerHeartRate) * 0.1;
        HeartRateManager.forceHeartRate(player.getUuid(), (int) (playerHeartRate + adjustment));

        // 团队增益效果
        if (teamSize >= 3) {
            // 大团队增益
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, 100, 1, false, false, true));
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.STRENGTH, 100, 0, false, false, true));
        } else {
            // 小团队增益
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, 100, 0, false, false, true));
        }

        // 发送同步消息
        if (player.getRandom().nextInt(200) == 0) {
            player.sendMessage(
                    net.minecraft.text.Text.translatable("message.photophobia.team_sync_high"),
                    true);
        }
    }

    /**
     * 中等同步效果
     */
    private static void applyMediumSyncEffects(ServerPlayerEntity player, double teamAverage, int teamSize) {
        int playerHeartRate = HeartRateManager.getHeartRate(player.getUuid());

        // 轻微向团队平均值靠拢
        double adjustment = (teamAverage - playerHeartRate) * 0.05;
        HeartRateManager.forceHeartRate(player.getUuid(), (int) (playerHeartRate + adjustment));

        // 轻微增益效果
        if (teamSize >= 3) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, 60, 0, false, false, true));
        }
    }

    /**
     * 低同步效果
     */
    private static void applyLowSyncEffects(ServerPlayerEntity player, double teamAverage, int teamSize) {
        // 低同步时没有增益效果
        // 可以添加一些轻微惩罚或中性效果
        if (player.getRandom().nextInt(300) == 0) {
            player.sendMessage(
                    net.minecraft.text.Text.translatable("message.photophobia.team_sync_low"),
                    true);
        }
    }

    /**
     * 检查玩家是否在团队中
     */
    public static boolean isPlayerInTeam(PlayerEntity player, int minTeamSize) {
        List<PlayerEntity> nearbyPlayers = player.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                new Box(player.getBlockPos()).expand(16), // 16格范围内
                p -> p != player && p.isAlive());

        return nearbyPlayers.size() >= minTeamSize - 1;
    }

    /**
     * 获取玩家团队大小
     */
    public static int getPlayerTeamSize(PlayerEntity player) {
        List<PlayerEntity> nearbyPlayers = player.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                new Box(player.getBlockPos()).expand(16), // 16格范围内
                p -> p != player && p.isAlive());

        return nearbyPlayers.size() + 1; // 包括自己
    }
}
