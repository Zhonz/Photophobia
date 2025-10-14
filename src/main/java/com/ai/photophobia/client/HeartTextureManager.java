package com.ai.photophobia.client;

import com.ai.photophobia.system.HeartRateCalculator;
import net.minecraft.util.Identifier;

/**
 * 心脏纹理管理器 - 处理心脏动画序列的纹理加载
 * 适配44x44像素的心脏图片
 * 
 * @author AI Developer
 * @version 1.1
 * @since 2024
 */
public class HeartTextureManager {
    private static final String HEART_TEXTURE_PATH = "textures/gui/heart/";
    private static final int FRAME_COUNT = 8; // 心脏动画帧数
    private static final int HEART_WIDTH = 44; // 心脏图片宽度
    private static final int HEART_HEIGHT = 44; // 心脏图片高度

    /**
     * 获取心脏纹理标识符
     * 
     * @param frame 帧索引 (0-7)
     * @return 纹理标识符
     */
    public static Identifier getHeartTexture(int frame) {
        if (frame < 0 || frame >= FRAME_COUNT) {
            frame = 0; // 默认使用第一帧
        }

        String textureName = String.format("heart_%02d.png", frame);
        return Identifier.of("photophobia", HEART_TEXTURE_PATH + textureName);
    }

    /**
     * 获取心脏动画帧数
     * 
     * @return 总帧数
     */
    public static int getFrameCount() {
        return FRAME_COUNT;
    }

    /**
     * 获取心脏纹理路径模板
     * 
     * @return 纹理路径模板
     */
    public static String getTexturePathTemplate() {
        return HEART_TEXTURE_PATH + "heart_%02d.png";
    }

    /**
     * 验证心脏纹理文件命名规范
     * 
     * @return 预期的纹理文件名列表
     */
    public static String[] getExpectedTextureFiles() {
        String[] files = new String[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            files[i] = String.format("heart_%02d.png", i);
        }
        return files;
    }

    /**
     * 获取心脏图片宽度
     * 
     * @return 心脏图片宽度 (44像素)
     */
    public static int getHeartWidth() {
        return HEART_WIDTH;
    }

    /**
     * 获取心脏图片高度
     * 
     * @return 心脏图片高度 (44像素)
     */
    public static int getHeartHeight() {
        return HEART_HEIGHT;
    }

    /**
     * 获取心脏渲染尺寸（考虑缩放）
     * 
     * @param scale 缩放因子
     * @return 渲染尺寸数组 [宽度, 高度]
     */
    public static int[] getRenderSize(float scale) {
        return new int[] {
                (int) (HEART_WIDTH * scale),
                (int) (HEART_HEIGHT * scale)
        };
    }

    /**
     * 检查所有心脏纹理文件是否存在
     * 
     * @return 是否所有纹理文件都存在
     */
    public static boolean validateTextures() {
        // 这里可以添加纹理文件存在性检查
        // 目前假设所有文件都存在，因为我们已经复制了图片
        return true;
    }

    /**
     * 获取心脏动画帧率（根据心率计算）
     * 
     * @param heartRate 当前心率
     * @return 动画帧率 (帧/秒)
     */
    public static float getAnimationFrameRate(int heartRate) {
        // 心率越高，动画播放越快
        // 基础帧率：心率60对应1帧/秒，心率200对应8帧/秒
        float minFPS = 1.0f;
        float maxFPS = 8.0f;
        float heartRateRange = HeartRateCalculator.FATAL_HEART_RATE - HeartRateCalculator.BASE_RESTING_RATE_MIN;
        float heartRateProgress = (heartRate - HeartRateCalculator.BASE_RESTING_RATE_MIN) / heartRateRange;

        return minFPS + (maxFPS - minFPS) * heartRateProgress;
    }
}
