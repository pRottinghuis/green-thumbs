package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, GreenThumbs.ID);

    public static final RegistryObject<Item> GT_WHEAT_SEEDS = registerBlockItem(GTBlocks.GT_WHEAT, "gt_wheat_seeds");

    // Assume same name as block
    public static RegistryObject<Item> registerBlockItem(RegistryObject<? extends Block> block) {
        return registerBlockItem(block, block.getId().getPath());
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<? extends Block> block, String path) {
        return ITEMS.register(path, () -> new BlockItem(block.get(), new Item.Properties()));
    }

}
