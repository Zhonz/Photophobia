package com.zzandbrokensnake.photophobia.integration;

import com.zzandbrokensnake.photophobia.registry.PhotophobiaConfig;
import me.shedaniel.autoconfig.AutoConfig;

/**
 * ModMenu集成类
 * 提供在游戏内配置界面的支持
 * 使用条件编译处理可选依赖
 */
public class ModMenuIntegration {

    /**
     * 获取配置屏幕工厂
     * 使用反射来避免直接依赖modmenu
     */
    public static Object getModConfigScreenFactory() {
        try {
            // 使用反射检查modmenu是否可用
            Class<?> modMenuApiClass = Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
            Class<?> configScreenFactoryClass = Class.forName("com.terraformersmc.modmenu.api.ConfigScreenFactory");

            // 创建配置屏幕工厂
            return (java.util.function.Function<Object, Object>) parent -> AutoConfig
                    .getConfigScreen(PhotophobiaConfig.class, (net.minecraft.client.gui.screen.Screen) parent).get();

        } catch (ClassNotFoundException e) {
            // modmenu不可用，返回null
            return null;
        }
    }

    /**
     * 检查modmenu是否可用
     */
    public static boolean isModMenuAvailable() {
        try {
            Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
