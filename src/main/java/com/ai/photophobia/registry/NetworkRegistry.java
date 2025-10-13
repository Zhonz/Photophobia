package com.ai.photophobia.registry;

import com.ai.photophobia.PhotophobiaMod;

/**
 * 网络注册表
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class NetworkRegistry {
    /**
     * 注册所有网络包
     */
    public static void register() {
        // 这里将注册心率同步包等网络包
        PhotophobiaMod.LOGGER.info("网络包注册完成");
    }

    /**
     * 注册客户端网络包
     */
    public static void registerClient() {
        // 这里将注册客户端网络包
        PhotophobiaMod.LOGGER.info("客户端网络包注册完成");
    }
}
