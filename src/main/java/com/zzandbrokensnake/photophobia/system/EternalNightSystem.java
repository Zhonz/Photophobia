package com.zzandbrokensnake.photophobia.system;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/**
 * 永恒黑夜系统
 * 每次进入游戏将时间设定为黑夜并关闭时间流动
 */
public class EternalNightSystem {

    private static final long NIGHT_TIME = 18000L; // 午夜时间 (18:00)

    public static void initialize() {
        // 监听世界加载事件
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                setEternalNight(world);
            }
        });
    }

    /**
     * 设置永恒黑夜
     * 
     * @param world 服务器世界
     */
    private static void setEternalNight(ServerWorld world) {
        // 设置时间为黑夜
        world.setTimeOfDay(NIGHT_TIME);

        // 关闭时间流动
        world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, world.getServer());

        // 关闭天气变化
        world.getGameRules().get(GameRules.DO_WEATHER_CYCLE).set(false, world.getServer());

        // 设置天气为晴朗（无雨无雷）
        world.setWeather(0, 0, false, false);

        System.out.println("[Photophobia] 永恒黑夜已激活 - 时间锁定在午夜，时间流动已关闭");
    }

    /**
     * 获取当前是否为黑夜
     * 
     * @param world 世界
     * @return 是否为黑夜
     */
    public static boolean isNight(World world) {
        long time = world.getTimeOfDay();
        return time >= 13000 && time < 23000;
    }

    /**
     * 强制设置黑夜时间
     * 
     * @param world 服务器世界
     */
    public static void forceNightTime(ServerWorld world) {
        world.setTimeOfDay(NIGHT_TIME);
    }
}
