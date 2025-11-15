# 《畏光》MOD 世界结构文件创建指南

## 📋 完整的世界结构文件创建流程

### 1. 结构文件系统概述
一个完整的Minecraft结构需要以下文件：
- **结构定义文件** (`structure/*.json`)
- **模板池文件** (`template_pool/*/start_pool.json`) 
- **放置特征文件** (`worldgen/placed_feature/*.json`)
- **配置特征文件** (`worldgen/configured_feature/*.json`)
- **结构模板文件** (`structures/*.nbt`)

### 2. 创建被诅咒的废墟完整文件

#### 2.1 放置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/placed_feature/haunted_ruins_placed.json`

```json
{
  "feature": "photophobia:haunted_ruins",
  "placement": [
    {
      "type": "minecraft:rarity_filter",
      "chance": 100
    },
    {
      "type": "minecraft:in_square"
    },
    {
      "type": "minecraft:heightmap",
      "heightmap": "WORLD_SURFACE_WG"
    },
    {
      "type": "minecraft:biome"
    }
  ]
}
```

#### 2.2 配置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/configured_feature/haunted_ruins.json`

```json
{
  "type": "minecraft:jigsaw",
  "start_pool": "photophobia:haunted_ruins/start_pool",
  "size": 6,
  "max_distance_from_center": 80,
  "spawn_overrides": {
    "monster": {
      "bounding_box": "full",
      "spawns": [
        {
          "type": "photophobia:ghost",
          "weight": 1,
          "minCount": 1,
          "maxCount": 3
        }
      ]
    }
  }
}
```

### 3. 创建平衡之塔完整文件

#### 3.1 放置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/placed_feature/spire_of_equilibrium_placed.json`

```json
{
  "feature": "photophobia:spire_of_equilibrium",
  "placement": [
    {
      "type": "minecraft:rarity_filter",
      "chance": 200
    },
    {
      "type": "minecraft:in_square"
    },
    {
      "type": "minecraft:heightmap",
      "heightmap": "OCEAN_FLOOR_WG"
    },
    {
      "type": "minecraft:biome"
    }
  ]
}
```

#### 3.2 配置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/configured_feature/spire_of_equilibrium.json`

```json
{
  "type": "minecraft:jigsaw",
  "start_pool": "photophobia:spire_of_equilibrium/start_pool",
  "size": 8,
  "max_distance_from_center": 100,
  "spawn_overrides": {}
}
```

### 4. 创建荧光蘑菇林完整文件

#### 4.1 放置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/placed_feature/glowing_mushroom_grove_placed.json`

```json
{
  "feature": "photophobia:glowing_mushroom_grove",
  "placement": [
    {
      "type": "minecraft:rarity_filter",
      "chance": 5
    },
    {
      "type": "minecraft:in_square"
    },
    {
      "type": "minecraft:heightmap",
      "heightmap": "MOTION_BLOCKING"
    },
    {
      "type": "minecraft:biome"
    }
  ]
}
```

#### 4.2 配置特征文件
创建文件：`src/main/resources/data/photophobia/worldgen/configured_feature/glowing_mushroom_grove.json`

```json
{
  "type": "minecraft:random_patch",
  "tries": 32,
  "xz_spread": 7,
  "y_spread": 3,
  "feature": {
    "type": "minecraft:simple_block",
    "config": {
      "to_place": {
        "type": "minecraft:simple_state_provider",
        "state": {
          "Name": "minecraft:brown_mushroom"
        }
      }
    }
  }
}
```

### 5. 创建结构模板文件

#### 5.1 创建结构文件夹
```
src/main/resources/data/photophobia/structures/
├── haunted_ruins/
│   └── main.nbt
└── spire_of_equilibrium/
    └── main.nbt
```

#### 5.2 如何创建结构模板 (.nbt 文件)
1. **在游戏中建造结构**
   - 在创造模式中建造你想要的结构
   - 使用结构方块保存结构
   - 导出为 .nbt 文件

2. **使用结构方块命令**
```
# 放置结构方块
/setblock ~ ~ ~ minecraft:structure_block{mode:"SAVE"}

# 设置结构名称和大小
/data merge block ~ ~ ~ {name:"photophobia:haunted_ruins/main", sizeX:15, sizeY:10, sizeZ:15}

# 保存结构
/data merge block ~ ~ ~ {mode:"SAVE"}
```

3. **导出结构文件**
   - 结构文件会自动保存到 `saves/你的世界名字/structures/`
   - 复制到MOD的 `structures/` 文件夹

### 6. 更新世界生成系统

#### 6.1 启用世界生成
更新 `PhotophobiaWorldGen.java`：

```java
public static void initialize() {
    // 现在所有必要的文件都已创建，可以启用世界生成
    BiomeModifications.addFeature(
        BiomeSelectors.foundInOverworld(),
        GenerationStep.Feature.SURFACE_STRUCTURES,
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, 
            new Identifier(PhotophobiaMod.MOD_ID, "haunted_ruins_placed"))
    );
    
    BiomeModifications.addFeature(
        BiomeSelectors.foundInOverworld(),
        GenerationStep.Feature.SURFACE_STRUCTURES,
        RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            new Identifier(PhotophobiaMod.MOD_ID, "spire_of_equilibrium_placed"))
    );
    
    BiomeModifications.addFeature(
        BiomeSelectors.foundInOverworld(),
        GenerationStep.Feature.VEGETAL_DECORATION,
        RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            new Identifier(PhotophobiaMod.MOD_ID, "glowing_mushroom_grove_placed"))
    );
    
    PhotophobiaMod.LOGGER.info("Photophobia world generation enabled");
}
```

### 7. 测试结构生成

#### 7.1 生成结构命令
```
# 强制生成被诅咒的废墟
/place structure photophobia:haunted_ruins

# 强制生成平衡之塔
/place structure photophobia:spire_of_equilibrium

# 查找附近的结构
/locate structure photophobia:haunted_ruins
```

#### 7.2 调试技巧
- 使用 `/reload` 重新加载数据包
- 检查游戏日志中的错误信息
- 使用结构方块预览生成效果

### 8. 结构设计建议

#### 8.1 被诅咒的废墟设计
- **主题**：破败的石质建筑，苔藓覆盖
- **特征**：藏匿点、幽灵生成区域、战利品箱
- **氛围**：黑暗、潮湿、恐怖

#### 8.2 平衡之塔设计  
- **主题**：古老的神秘石塔，发光符文
- **特征**：心率稳定区域、特殊事件触发点
- **氛围**：神秘、宁静、神圣

#### 8.3 荧光蘑菇林设计
- **主题**：发光的蘑菇森林，柔和光线
- **特征**：各种荧光蘑菇、安全区域
- **氛围**：梦幻、安全、美丽

### 9. 文件结构总结
```
src/main/resources/data/photophobia/
├── worldgen/
│   ├── structure/                    # 已有
│   │   ├── haunted_ruins.json
│   │   └── spire_of_equilibrium.json
│   ├── configured_feature/           # 需要创建
│   │   ├── haunted_ruins.json
│   │   ├── spire_of_equilibrium.json
│   │   └── glowing_mushroom_grove.json
│   └── placed_feature/               # 需要创建
│       ├── haunted_ruins_placed.json
│       ├── spire_of_equilibrium_placed.json
│       └── glowing_mushroom_grove_placed.json
├── template_pool/                    # 已有
│   ├── haunted_ruins/
│   │   └── start_pool.json
│   └── spire_of_equilibrium/
│       └── start_pool.json
└── structures/                       # 需要创建
    ├── haunted_ruins/
    │   └── main.nbt
    └── spire_of_equilibrium/
        └── main.nbt
```

### 10. 下一步操作
1. 创建缺失的文件夹和配置文件
2. 在游戏中建造结构并导出为 .nbt 文件
3. 更新世界生成系统启用功能
4. 测试结构生成和功能

按照这个指南，你可以创建完整的世界结构文件系统，让MOD的结构在游戏中正常生成！
