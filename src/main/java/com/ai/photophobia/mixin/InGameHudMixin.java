package com.ai.photophobia.mixin;

import com.ai.photophobia.system.HeartRateManager;
import com.ai.photophobia.system.PlayerStatusManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 游戏HUD注入 - 添加心率显示
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    private static final int HEART_X = 10;
    private static final int HEART_Y = 10;
    private static final int HEART_SIZE = 44;
    private static final int TEXT_OFFSET_X = 50;
    private static final int TEXT_OFFSET_Y = 15;
    private static float totalTickDelta = 0;

    /**
     * 在渲染HUD时注入心率显示
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderHeartHUD(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        PlayerEntity player = client.player;

        // 获取心率数据
        int heartRate = HeartRateManager.getHeartRate(player);
        boolean isCalmEffect = PlayerStatusManager.isCalmEffectActive(player);

        // 使用tickDelta实现平滑动画
        totalTickDelta += tickDelta;

        // 获取心脏动画帧 - 使用tickDelta实现平滑动画
        int heartFrame = HeartRateManager.getHeartAnimationFrame(heartRate, totalTickDelta);

        // 获取心脏颜色
        int heartColor = HeartRateManager.getHeartColor(heartRate, isCalmEffect);

        // 获取心脏缩放
        float heartScale = HeartRateManager.getHeartScale(heartRate);

        // 渲染心脏图标
        renderHeartIcon(context, heartFrame, heartColor, heartScale);

        // 渲染心率文本
        renderHeartRateText(context, heartRate, isCalmEffect);

        // 渲染状态文本
        renderStatusText(context, heartRate, isCalmEffect);
    }

    /**
     * 渲染心脏图标
     */
    private void renderHeartIcon(DrawContext context, int frame, int color, float scale) {
        // 获取心脏纹理
        Identifier heartTexture = getHeartTexture(frame);

        // 计算渲染尺寸
        int renderSize = (int) (HEART_SIZE * scale);

        // 渲染心脏图标
        context.drawTexture(
                heartTexture,
                HEART_X,
                HEART_Y,
                0, 0,
                renderSize, renderSize,
                renderSize, renderSize);
    }

    /**
     * 渲染心率文本
     */
    private void renderHeartRateText(DrawContext context, int heartRate, boolean isCalmEffect) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 心率文本
        String heartRateText = heartRate + " BPM";
        Text text = Text.literal(heartRateText);

        // 根据心率状态设置颜色
        int textColor = getTextColor(heartRate, isCalmEffect);

        // 渲染心率文本
        context.drawText(
                client.textRenderer,
                text,
                HEART_X + TEXT_OFFSET_X,
                HEART_Y + TEXT_OFFSET_Y,
                textColor,
                true);
    }

    /**
     * 渲染状态文本
     */
    private void renderStatusText(DrawContext context, int heartRate, boolean isCalmEffect) {
        MinecraftClient client = MinecraftClient.getInstance();

        String statusText;
        int statusColor;

        if (isCalmEffect) {
            statusText = "宁神粉尘生效中";
            statusColor = 0xFFFFFF55; // 黄色
        } else if (heartRate >= 180) {
            statusText = "危险！心率过高";
            statusColor = 0xFFFF5555; // 红色
        } else if (heartRate >= 160) {
            statusText = "警告：心率上升";
            statusColor = 0xFFFFAA00; // 橙色
        } else {
            statusText = "心率正常";
            statusColor = 0xFF55FF55; // 绿色
        }

        // 渲染状态文本
        context.drawText(
                client.textRenderer,
                Text.literal(statusText),
                HEART_X + TEXT_OFFSET_X,
                HEART_Y + TEXT_OFFSET_Y + 12,
                statusColor,
                true);
    }

    /**
     * 获取心脏纹理
     */
    private Identifier getHeartTexture(int frame) {
        return Identifier.of("photophobia", "textures/gui/heart/heart_" + String.format("%02d", frame) + ".png");
    }

    /**
     * 获取文本颜色
     */
    private int getTextColor(int heartRate, boolean isCalmEffect) {
        if (isCalmEffect) {
            return 0xFFFFFF55; // 黄色
        } else if (heartRate >= 180) {
            return 0xFFFF5555; // 红色
        } else if (heartRate >= 160) {
            return 0xFFFFAA00; // 橙色
        } else if (heartRate >= 120) {
            return 0xFFFFFF55; // 黄色
        } else {
            return 0xFF55FF55; // 绿色
        }
    }
}
