package com.zzandbrokensnake.photophobia.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class CriticalVisionRenderer implements HudRenderCallback {
    private static long lastSideChangeTime = 0;
    private static int currentSide = 0; // 0:左, 1:右
    private static final long SIDE_CHANGE_INTERVAL = 2000; // 2秒切换一次

    private static final Identifier BLACK_TEXTURE = new Identifier("textures/misc/black.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        // 检查是否在5血以下
        if (client.player.getHealth() <= 5.0f) {
            // 随机切换左右黑屏
            if (System.currentTimeMillis() - lastSideChangeTime > SIDE_CHANGE_INTERVAL) {
                currentSide = client.player.getRandom().nextInt(2); // 0或1，只随机左右
                lastSideChangeTime = System.currentTimeMillis();
            }
            
            // 渲染黑屏效果
            renderBlackOverlay(drawContext, currentSide);
        }
    }

    private static void renderBlackOverlay(DrawContext drawContext, int side) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);

        switch (side) {
            case 0: // 左侧黑屏
                drawContext.fill(0, 0, width / 4, height, 0xFF000000);
                break;
            case 1: // 右侧黑屏
                drawContext.fill(width * 3 / 4, 0, width, height, 0xFF000000);
                break;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
