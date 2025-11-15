package com.zzandbrokensnake.photophobia.world.gen;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;

public class PhotophobiaWorldGen {

      /**
       * 初始化世界生成
       * 世界生成暂时禁用，需要创建结构模板文件 (.nbt) 后才能启用
       */
      public static void initialize() {
            // 世界生成暂时禁用，避免注册表加载错误
            // 需要先创建结构模板文件 (.nbt) 才能启用世界生成
            PhotophobiaMod.LOGGER
                        .info("Photophobia world generation disabled - requires structure template files (.nbt)");
      }

      /**
       * 获取结构生成信息
       */
      public static String getStructureInfo() {
            return """
                        Photophobia World Generation Status:

                        World generation is currently disabled to prevent registry loading errors.
                        To enable world generation, complete structure files are required:

                        1. Haunted Ruins (被诅咒的废墟) - Structure files created
                        2. Spire of Equilibrium (平衡之塔) - Structure files created
                        3. Glowing Mushroom Grove (荧光蘑菇林) - Structure files created

                        Missing files:
                        - Placed feature configurations
                        - Configured feature definitions
                        - Structure template files

                        Current MOD focuses on core gameplay systems:
                        - Heart rate mechanics
                        - Ghost entities and AI
                        - Multiplayer systems
                        - Event and reward systems
                        """;
      }
}
