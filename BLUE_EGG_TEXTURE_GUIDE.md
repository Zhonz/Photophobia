# 蓝之卵纹理文件指南

## 蓝之卵图片放置位置和名称

蓝之卵的纹理文件需要放置在以下位置：

```
src/main/resources/assets/photophobia/textures/item/blue_egg.png
```

## 文件要求

- **文件名**: `blue_egg.png`
- **位置**: `src/main/resources/assets/photophobia/textures/item/`
- **尺寸**: 64x64 像素（高分辨率纹理）
- **格式**: PNG格式，支持透明度

## 纹理设计建议

蓝之卵应该具有以下特征：
- 蓝色调的蛋形设计
- 可以添加一些发光效果或神秘图案
- 与模组的恐怖氛围保持一致
- 建议使用深蓝色调，带有一些浅蓝色高光

## 当前状态

- ✅ 蓝之卵模型文件已创建 (`blue_egg.json`)
- ✅ 蓝之卵本地化文本已添加
- ✅ 重蓝事件系统已实现
- ❌ 蓝之卵纹理文件需要创建

## 下一步操作

1. 创建 `blue_egg.png` 文件并放置在指定位置
2. 确保纹理文件符合Minecraft标准
3. 测试蓝之卵在游戏中的显示效果
4. 验证重蓝事件触发功能

## 相关文件

- 模型文件: `src/main/resources/assets/photophobia/models/item/blue_egg.json`
- 本地化文件: `src/main/resources/assets/photophobia/lang/zh_cn.json`
- 事件系统: `src/main/java/com/zzandbrokensnake/photophobia/system/HeavyBlueEventSystem.java`
- 物品类: `src/main/java/com/zzandbrokensnake/photophobia/items/BlueEggItem.java`

完成纹理文件创建后，蓝之卵将能够在游戏中正常显示并触发重蓝事件。
