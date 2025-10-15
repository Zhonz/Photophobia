package com.ai.photophobia.registry;

import com.ai.photophobia.PhotophobiaMod;
import com.ai.photophobia.items.CalmCapsuleItem;
import com.ai.photophobia.items.CalmPowderItem;
import com.ai.photophobia.items.CalmSprayItem;
import com.ai.photophobia.items.GlowingMushroomItem;
import com.ai.photophobia.items.LanternItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 物品注册表
 * 
 * @author AI Developer
 * @version 1.1
 * @since 2024
 */
public class ItemRegistry {
        public static final Item LANTERN_ITEM = new LanternItem(new Item.Settings().maxCount(1));
        public static final Item CALM_CAPSULE_ITEM = new CalmCapsuleItem(new Item.Settings().maxCount(1));
        public static final Item CALM_POWDER_ITEM = new CalmPowderItem(new Item.Settings().maxCount(1));
        public static final Item CALM_SPRAY_ITEM = new CalmSprayItem(new Item.Settings().maxCount(1));

        /**
         * 注册所有物品
         */
        public static void register() {
                Registry.register(Registries.ITEM,
                                Identifier.of(PhotophobiaMod.MOD_ID, "lantern"),
                                LANTERN_ITEM);

                Registry.register(Registries.ITEM,
                                Identifier.of(PhotophobiaMod.MOD_ID, "calm_capsule"),
                                CALM_CAPSULE_ITEM);

                Registry.register(Registries.ITEM,
                                Identifier.of(PhotophobiaMod.MOD_ID, "calm_powder"),
                                CALM_POWDER_ITEM);

                Registry.register(Registries.ITEM,
                                Identifier.of(PhotophobiaMod.MOD_ID, "calm_spray"),
                                CALM_SPRAY_ITEM);

                PhotophobiaMod.LOGGER.info("物品注册完成");
        }
}
