package com.zzandbrokensnake.photophobia.mixin.client;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.DrawContext;
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
    private void replaceTitleLogo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 获取屏幕尺寸
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

        // 计算标题位置（屏幕中央）
        int titleWidth = 1076; // 实际图片宽度
        int titleHeight = 496; // 实际图片高度
        int x = (screenWidth - titleWidth) / 2;
        int y = 30; // 距离顶部的距离

        // 使用DrawContext绘制纹理
        drawCustomTexture(context, x, y, titleWidth, titleHeight);
    }

    private void drawCustomTexture(DrawContext context, int x, int y, int width, int height) {
        // 使用DrawContext绘制纹理
        // 注意：需要确保图片文件存在：src/main/resources/assets/photophobia/textures/gui/title.png
        // 图片尺寸：1076x496像素
        context.drawTexture(CUSTOM_TITLE_TEXTURE, x, y, 0, 0, width, height, width, height);
    }
}
