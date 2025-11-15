package com.zzandbrokensnake.photophobia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzandbrokensnake.photophobia.client.renderer.CriticalVisionRenderer;
import com.zzandbrokensnake.photophobia.client.renderer.HeartRateHudRenderer;
import com.zzandbrokensnake.photophobia.client.renderer.GhostEntityRenderer;
import com.zzandbrokensnake.photophobia.registry.EntityRegistry;

public class PhotophobiaModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("photophobia-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Photophobia Mod Client initialized");

        // 注册HUD渲染器
        HudRenderCallback.EVENT.register(new HeartRateHudRenderer());
        HudRenderCallback.EVENT.register(new CriticalVisionRenderer());

        // 注册幽灵实体渲染器
        EntityRendererRegistry.register(EntityRegistry.GHOST, GhostEntityRenderer::new);

        LOGGER.info("HUD renderers and entity renderers registered");
    }
}
