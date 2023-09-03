package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.item.custom.GTDebugStick;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

public class GTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, GreenThumbs.ID);

    public static final RegistryObject<Item> GT_DEBUG_STICK = ITEMS.register("gt_debug_stick", () -> new GTDebugStick(new Item.Properties()));

    public static final RegistryObject<GTGenomeCropBlockItem> CARROT_SEEDS = registerGenomeBlockItem("carrot_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> CARROT_CUTTING = registerGenomeBlockItem("carrot_cutting", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> WHEAT_SEEDS = registerGenomeBlockItem("wheat_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> WHEAT_CUTTING = registerGenomeBlockItem("wheat_cutting", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> POTATO_SEEDS = registerGenomeBlockItem("potato_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> POTATO_CUTTING = registerGenomeBlockItem("potato_cutting", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> BEETROOT_SEEDS = registerGenomeBlockItem("beetroot_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> BEETROOT_CUTTING = registerGenomeBlockItem("beetroot_cutting", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> SWEET_BERRY_SEEDS = registerGenomeBlockItem("sweet_berry_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> SWEET_BERRY_CUTTING = registerGenomeBlockItem("sweet_berry_cutting", GTBlocks.GT_CROP_BLOCK);


    public static final RegistryObject<GTGenomeCropBlockItem> PUMPKIN_SEEDS = registerGenomeBlockItem("pumpkin_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> PUMPKIN_CUTTING = registerGenomeBlockItem("pumpkin_cutting", GTBlocks.GT_CROP_BLOCK);

    public static final RegistryObject<GTGenomeCropBlockItem> MELON_SEEDS = registerGenomeBlockItem("melon_seeds", GTBlocks.GT_CROP_BLOCK);
    public static final RegistryObject<GTGenomeCropBlockItem> MELON_CUTTING = registerGenomeBlockItem("melon_cutting", GTBlocks.GT_CROP_BLOCK);

    public static final RegistryObject<BlockItem> SEED_SPLICING_STATION = ITEMS.register("seed_splicing_station", () -> new BlockItem(GTBlocks.SEED_SPLICING_STATION.get(), new Item.Properties()));

    public static RegistryObject<GTGenomeCropBlockItem> registerGenomeBlockItem(String path, RegistryObject<? extends Block> block) {
        return ITEMS.register(path, () -> new GTGenomeCropBlockItem(block.get(), new Item.Properties()));
    }

}
