package com.ai.photophobia;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.photophobia.registry.NetworkRegistry;

/**
 * 畏光模组客户端类
 * 处理客户端渲染和效果
 * 
 * @author AI Developer
 * @version 1.1
 * @since 2024
 */
public class PhotophobiaModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("畏光-客户端");

    @Override
    public void onInitializeClient() {
        LOGGER.info("{} 客户端模组正在初始化...", PhotophobiaMod.MOD_NAME);

        // HUD渲染暂时注释掉，需要找到正确的API
        /*
         * HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
         * HeartHudRenderer.renderHeartHUD(drawContext, tickDelta);
         * });
         */

        // 注册客户端网络包
        NetworkRegistry.registerClient();

        LOGGER.info("{} 客户端模组初始化完成！", PhotophobiaMod.MOD_NAME);
    }
}
