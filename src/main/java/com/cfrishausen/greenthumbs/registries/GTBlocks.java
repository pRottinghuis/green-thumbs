package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.custom.SeedSplicingStationBlock;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.species.StemGrownCrop;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.swing.*;

public class GTBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, GreenThumbs.ID);

    public static final RegistryObject<Block> GT_CROP_BLOCK = BLOCKS.register("gt_crop_block",
            () -> new GTSimpleCropBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    public static final RegistryObject<Block> GT_COLOR_CROP_BLOCK = BLOCKS.register("gt_color_crop_block",
            () -> new GTSimpleCropBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    public static final RegistryObject<Block> GT_VEGETABLE_BLOCK = BLOCKS.register("gt_vegetable_block",
            () -> new GTSimpleCropBlock(BlockBehaviour.Properties.of(Material.VEGETABLE).randomTicks().strength(1.0F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> SEED_SPLICING_STATION = BLOCKS.register("seed_splicing_station",
            () -> new SeedSplicingStationBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));

}
