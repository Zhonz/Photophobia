package com.zzandbrokensnake.photophobia.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import com.zzandbrokensnake.photophobia.system.HeartRateManager;

public class HeartRateHudRenderer implements HudRenderCallback {
    private static final Identifier[] HEART_TEXTURES = {
            new Identifier("photophobia", "textures/gui/heart/heart_00.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_01.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_02.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_03.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_04.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_05.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_06.png"),
            new Identifier("photophobia", "textures/gui/heart/heart_07.png")
    };

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        int heartRate = HeartRateManager.getHeartRate(client.player.getUuid());
        if (heartRate <= 0)
            return;

        renderHeartRateHud(drawContext, heartRate);
    }

    private void renderHeartRateHud(DrawContext drawContext, int heartRate) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // 计算心脏图标位置（屏幕左上角）
        int x = 10; // 左侧边距
        int y = 10; // 顶部边距

        // 计算心脏大小变化（基础大小2.0倍，最大1.3倍变化）
        float baseScale = 2.0f; // 基础大小放大2倍
        float sizeMultiplier = 1.0f + ((heartRate - 60) / 140.0f) * 0.3f; // 60-200映射到1.0-1.3倍
        float scale = baseScale * sizeMultiplier; // 最终大小 = 基础大小 × 变化倍数
        
        float alpha = 1.0f; // 固定透明度

        // 根据心率选择心脏纹理（提高跳动速度下限）
        long currentTime = System.currentTimeMillis();
        int animationSpeed = Math.max(3, heartRate / 15); // 提高下限到3，心率越高动画越快
        int textureIndex = (int) ((currentTime / (1000 / animationSpeed)) % HEART_TEXTURES.length);

        // 简化颜色变化：心率越高越红
        // 心率60-200映射到红色分量1.0-1.0，绿色和蓝色分量1.0-0.0
        float redTint = 1.0f; // 红色始终保持最大
        float greenTint = Math.max(0.0f, 1.0f - ((heartRate - 60) / 140.0f)); // 心率越高绿色越少
        float blueTint = Math.max(0.0f, 1.0f - ((heartRate - 60) / 140.0f)); // 心率越高蓝色越少

        // 渲染心脏图标（带颜色变化）
        renderHeartIcon(drawContext, x, y, scale, alpha, textureIndex, redTint, greenTint, blueTint);
    }

    private void renderHeartIcon(DrawContext drawContext, int x, int y, float scale, float alpha, int textureIndex,
            float redTint, float greenTint, float blueTint) {
        // 确保纹理索引在有效范围内
        if (textureIndex < 0 || textureIndex >= HEART_TEXTURES.length) {
            textureIndex = 0; // 使用默认纹理
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(redTint, greenTint, blueTint, alpha);

        int size = (int) (20 * scale);
        drawContext.drawTexture(HEART_TEXTURES[textureIndex], x, y, 0, 0, size, size, size, size);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
