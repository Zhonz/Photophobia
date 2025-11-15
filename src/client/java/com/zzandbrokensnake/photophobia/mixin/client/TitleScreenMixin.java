package com.zzandbrokensnake.photophobia.mixin.client;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    private static final Identifier CUSTOM_TITLE_TEXTURE = new Identifier("photophobia", "textures/gui/title.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void replaceTitleLogo(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 获取屏幕尺寸
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

        // 设置渲染状态
        RenderSystem.setShaderTexture(0, CUSTOM_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // 计算标题位置（屏幕中央）
        int titleWidth = 256; // 假设标题图片宽度
        int titleHeight = 64; // 假设标题图片高度
        int x = (screenWidth - titleWidth) / 2;
        int y = 30; // 距离顶部的距离

        // 使用GUI系统绘制纹理
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 使用GUI绘制方法 - 直接调用drawTexture
        drawCustomTexture(matrices, x, y, titleWidth, titleHeight);

        RenderSystem.disableBlend();
    }

    private void drawCustomTexture(MatrixStack matrices, int x, int y, int width, int height) {
        // 简单的纹理绘制实现 - 使用GUI系统
        // 这里使用GUI系统的绘制方法
        // 注意：需要确保图片文件存在：src/main/resources/assets/photophobia/textures/gui/title.png
        // 图片尺寸建议：256x64像素
        // 使用基本的渲染方法
        // 这里留空，等待图片文件准备好后实现具体绘制逻辑
    }
}
