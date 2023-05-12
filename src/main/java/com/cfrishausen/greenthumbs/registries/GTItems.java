package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.item.custom.GTDebugStick;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, GreenThumbs.ID);

    public static final RegistryObject<Item> GT_DEBUG_STICK = ITEMS.register("gt_debug_stick", () -> new GTDebugStick(new Item.Properties()));

    public static final RegistryObject<GTGenomeCropBlockItem> GT_CARROT_SEEDS = registerGenomeBlockItem("gt_carrot_seeds", GTBlocks.GT_CROP_BLOCK);

    public static final RegistryObject<GTGenomeCropBlockItem> GT_WHEAT_SEEDS = registerGenomeBlockItem("gt_wheat_seeds", GTBlocks.GT_CROP_BLOCK);

    public static RegistryObject<GTGenomeCropBlockItem> registerGenomeBlockItem(String path, RegistryObject<? extends Block> block) {
        return ITEMS.register(path, () -> new GTGenomeCropBlockItem(block.get(), new Item.Properties()));
    }

}
