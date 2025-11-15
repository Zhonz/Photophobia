package com.zzandbrokensnake.photophobia.system;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.system.PlayerLanternSystem;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class WorldEventHandler {

    private static boolean worldTimeLocked = false;

    public static void initialize() {
        // 玩家进入世界时触发
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            onPlayerJoinWorld(newPlayer);
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            onPlayerJoinWorld(newPlayer);
        });

        // 服务器每tick检查
        ServerTickEvents.START_SERVER_TICK.register(WorldEventHandler::onServerTick);
    }

    /**
     * 玩家进入世界时执行的操作
     */
    private static void onPlayerJoinWorld(ServerPlayerEntity player) {
        if (player.getServer() == null)
            return;

        ServerWorld world = player.getServerWorld();

        // 设置时间为夜晚 (13000 ticks = 午夜)
        world.setTimeOfDay(13000);

        // 禁止世界流动
        setWorldTimeLocked(world, true);

        // 锁定玩家伽马值
        lockPlayerGamma(player);

        // 给予玩家首次提灯
        PlayerLanternSystem.giveFirstLantern(player);

        PhotophobiaMod.LOGGER.info("玩家 {} 进入世界，已设置夜晚时间并锁定世界", player.getName().getString());
    }

    /**
     * 服务器每tick检查
     */
    private static void onServerTick(MinecraftServer server) {
        // 确保所有世界的时间都被锁定
        for (ServerWorld world : server.getWorlds()) {
            if (worldTimeLocked) {
                // 保持时间为夜晚
                if (world.getTimeOfDay() != 13000) {
                    world.setTimeOfDay(13000);
                }

                // 确保时间流动被禁止
                setWorldTimeLocked(world, true);
            }
        }
    }

    /**
     * 设置世界时间锁定状态
     */
    private static void setWorldTimeLocked(ServerWorld world, boolean locked) {
        worldTimeLocked = locked;

        // 设置游戏规则 - 修复：直接设置游戏规则值
        GameRules.BooleanRule daylightCycle = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
        GameRules.BooleanRule weatherCycle = world.getGameRules().get(GameRules.DO_WEATHER_CYCLE);

        daylightCycle.set(!locked, world.getServer());
        weatherCycle.set(!locked, world.getServer());

        if (locked) {
            // 清除天气并锁定时间
            world.setWeather(0, 0, false, false);
            world.setTimeOfDay(13000); // 确保时间被锁定在夜晚
        }
    }

    /**
     * 锁定玩家伽马值
     */
    private static void lockPlayerGamma(ServerPlayerEntity player) {
        // 在服务器端设置玩家的伽马值
        // 注意：伽马值主要在客户端控制，这里通过数据包同步
        // 实际伽马值锁定需要在客户端实现
        PhotophobiaMod.LOGGER.info("锁定玩家 {} 的伽马值", player.getName().getString());
    }

    /**
     * 获取世界时间锁定状态
     */
    public static boolean isWorldTimeLocked() {
        return worldTimeLocked;
    }

    /**
     * 切换世界时间锁定状态
     */
    public static void toggleWorldTimeLock() {
        worldTimeLocked = !worldTimeLocked;
        PhotophobiaMod.LOGGER.info("世界时间锁定状态: {}", worldTimeLocked ? "已锁定" : "已解锁");
    }
}
