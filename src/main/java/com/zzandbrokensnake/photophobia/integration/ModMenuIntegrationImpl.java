package com.zzandbrokensnake.photophobia.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.zzandbrokensnake.photophobia.registry.PhotophobiaConfig;
import me.shedaniel.autoconfig.AutoConfig;

/**
 * ModMenu集成实现类
 * 当modmenu存在时提供配置界面支持
 */
public class ModMenuIntegrationImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(PhotophobiaConfig.class, parent).get();
    }
}
