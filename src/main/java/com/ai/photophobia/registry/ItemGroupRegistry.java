package com.ai.photophobia.registry;

import com.ai.photophobia.PhotophobiaMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 物品组注册器 - 创建创造模式物品栏标签页
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class ItemGroupRegistry {

    /**
     * 畏光模组物品组
     */
    public static final ItemGroup PHOTOPHOBIA_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ItemRegistry.CALM_POWDER_ITEM))
            .displayName(Text.translatable("itemGroup.photophobia.main"))
            .entries((displayContext, entries) -> {
                // 添加所有畏光模组的物品
                entries.add(ItemRegistry.LANTERN_ITEM);
                entries.add(ItemRegistry.CALM_CAPSULE_ITEM);
                entries.add(ItemRegistry.CALM_POWDER_ITEM);
                entries.add(ItemRegistry.CALM_SPRAY_ITEM);
            })
            .build();

    /**
     * 注册物品组
     */
    public static void register() {
        Registry.register(Registries.ITEM_GROUP,
                Identifier.of(PhotophobiaMod.MOD_ID, "main"),
                PHOTOPHOBIA_GROUP);

        PhotophobiaMod.LOGGER.info("物品组注册完成");
    }
}
