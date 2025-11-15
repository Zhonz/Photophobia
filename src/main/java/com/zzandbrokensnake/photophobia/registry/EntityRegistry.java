package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.entity.GhostEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityRegistry {
    // 幽灵实体类型
    public static final EntityType<GhostEntity> GHOST = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(PhotophobiaMod.MOD_ID, "ghost"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GhostEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                    .build());

    public static void register() {
        // 注册幽灵属性
        FabricDefaultAttributeRegistry.register(GHOST, GhostEntity.createGhostAttributes());

        PhotophobiaMod.LOGGER.info("Entity registry initialized");
    }
}
