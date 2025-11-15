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
    private static long lastTriggerTime = 0;
    private static long effectEndTime = 0;
    private static int currentSide = 0; // 0:左, 1:右, 2:上, 3:下

    private static final Identifier BLACK_TEXTURE = new Identifier("textures/misc/black.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        // 检查触发条件
        if (shouldTriggerEffect(client.player)) {
            triggerEffect(client.player.getRandom());
        }

        // 渲染黑屏效果
        if (System.currentTimeMillis() < effectEndTime) {
            renderBlackOverlay(drawContext, currentSide);
        }
    }

    private static boolean shouldTriggerEffect(PlayerEntity player) {
        return player.getHealth() <= 5.0f &&
                System.currentTimeMillis() - lastTriggerTime > 30000; // 30秒冷却
    }

    private static void triggerEffect(Random random) {
        lastTriggerTime = System.currentTimeMillis();
        effectEndTime = lastTriggerTime + (3000 + random.nextInt(2000)); // 3-5秒
        currentSide = random.nextInt(4);

        // 播放耳鸣音效
        MinecraftClient.getInstance().player.playSound(
                net.minecraft.sound.SoundEvents.BLOCK_CONDUIT_AMBIENT,
                1.0f, 1.0f);
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
            case 2: // 上方黑屏
                drawContext.fill(0, 0, width, height / 4, 0xFF000000);
                break;
            case 3: // 下方黑屏
                drawContext.fill(0, height * 3 / 4, width, height, 0xFF000000);
                break;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
