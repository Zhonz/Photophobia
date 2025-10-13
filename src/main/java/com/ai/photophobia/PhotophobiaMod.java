package com.ai.photophobia;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.photophobia.system.HeartRateManager;
import com.ai.photophobia.registry.StatusEffectRegistry;
import com.ai.photophobia.registry.ItemRegistry;
import com.ai.photophobia.registry.EntityRegistry;
import com.ai.photophobia.registry.NetworkRegistry;
import com.ai.photophobia.registry.ConfigRegistry;

/**
 * 畏光模组主类
 * 一个心理恐怖生存模组，玩家需要管理心率并躲避趋光怪物
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class PhotophobiaMod implements ModInitializer {
    public static final String MOD_ID = "photophobia";
    public static final String MOD_NAME = "畏光";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("{} 模组正在初始化...", MOD_NAME);

        // 注册心率管理器
        HeartRateManager.initialize();

        // 注册状态效果
        StatusEffectRegistry.register();

        // 注册物品
        ItemRegistry.register();

        // 注册实体
        EntityRegistry.register();

        // 注册网络包
        NetworkRegistry.register();

        // 注册配置系统
        ConfigRegistry.register();

        LOGGER.info("{} 模组初始化完成！", MOD_NAME);
    }
}
