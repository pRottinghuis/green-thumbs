package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.function.Supplier;

public class MelonLikeCrop extends StemGrownCrop{

    private Supplier<Item> cropBlock;

    public MelonLikeCrop(String pathName, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> slice, Supplier<Item> cropBlock, Supplier<GTGenomeCropBlockItem> cutting, Supplier<ICropSpecies> stemSpecies, Supplier<ICropSpecies> attachedStemSpecies, boolean doesFortune) {
        super(pathName, seed, slice, cutting, stemSpecies, attachedStemSpecies, doesFortune);
        this.cropBlock = cropBlock;
    }

    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, Map<Enchantment, Integer> enchantments, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        ICropSpecies cropSpecies = cropEntity.getCropSpecies();
        RandomSource random = level.getRandom();
        int seedCount = 1;
        drops.setItem(0, new ItemStack(this.getCrop(), random.nextInt(3, 8) + cropEntity.getGenome().getExtraCropYield()));
        if (getDoesFortune() && enchantments.containsKey(Enchantments.BLOCK_FORTUNE)) {
            // Increase seed count on fortune
            for (int i = 0; i < enchantments.get(Enchantments.BLOCK_FORTUNE) + 3; ++i) {
                if (random.nextFloat() < 0.5714286) {
                    ++seedCount;
                }
            }

            // Increase stack size based on fortune level
            ItemStack cropDropStack = new ItemStack(drops.getItem(0).getItem(), drops.getItem(0).getCount() + enchantments.get(Enchantments.BLOCK_FORTUNE));

            // appropriately cap stack size
            int maxStackSize = 9 + cropEntity.getGenome().getExtraCropYield();
            if (cropDropStack.getCount() > maxStackSize) {
                cropDropStack.setCount(maxStackSize);
            }
        }
        if (enchantments.containsKey(Enchantments.SILK_TOUCH)) {
            drops.clearContent();
            drops.addItem(new ItemStack(this.cropBlock.get(), 0));
        }
        // Add seeds to drop container
        drops.setItem(1, new ItemStack(this.getSeed(), seedCount));

        Containers.dropContents(level, pos, drops);
        return null;
    }
}
