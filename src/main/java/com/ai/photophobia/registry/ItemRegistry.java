package com.ai.photophobia.registry;

import com.ai.photophobia.PhotophobiaMod;
import com.ai.photophobia.items.LanternItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 物品注册表
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class ItemRegistry {
    public static final Item LANTERN_ITEM = new LanternItem(new Item.Settings().maxCount(1));

    /**
     * 注册所有物品
     */
    public static void register() {
        Registry.register(Registries.ITEM,
                Identifier.of(PhotophobiaMod.MOD_ID, "lantern"),
                LANTERN_ITEM);

        PhotophobiaMod.LOGGER.info("物品注册完成");
    }
}
