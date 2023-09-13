package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.data.loot.GTLootFunction;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.BiConsumer;

public class GTBlockLootTables extends BlockLootSubProvider {

    public GTBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(GTBlocks.SEED_SPLICING_STATION.get());

        this.add(GTBlocks.GT_CROP_BLOCK.get(), block -> {
            return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(GTItems.WHEAT_SEEDS.get()))).apply(GTLootFunction.gtLoot());
        });

        this.add(GTBlocks.GT_VEGETABLE_BLOCK.get(), block -> {
            return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(GTItems.WHEAT_SEEDS.get()))).apply(GTLootFunction.gtLoot());
        });
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // Generate loot tables for all blocks in the mod
        return GTBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
