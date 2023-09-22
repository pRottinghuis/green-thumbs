package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Species for crops that are similar to potatoes and carrots
 */
public class RootCrop extends BasicCrop{

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

    public RootCrop(String name, Supplier<GTGenomeCropBlockItem> seeds, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting, boolean doesFortune) {
        super(name, seeds, crop, cutting, doesFortune);
    }

    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, Map<Enchantment, Integer> enchantments, boolean quickReplant) {

        SimpleContainer drops = new SimpleContainer(1);
        RandomSource random = level.getRandom();
        int extraCount = 0;

        // root crop can drop more than 1 root when broken
        if (cropEntity.getCropSpecies().isMaxAge(cropEntity)) {
            for (int i = 0; i < 3; ++i) {
                if (random.nextFloat() < 0.5714286) {
                    ++extraCount;
                }
            }

            // Calculate fortune for extra root drops
            if (getDoesFortune() && enchantments.containsKey(Enchantments.BLOCK_FORTUNE)) {
                for (int i = 0; i < enchantments.get(Enchantments.BLOCK_FORTUNE); ++i) {
                    if (random.nextFloat() < 0.5714286) {
                        ++extraCount;
                    }
                }
            }
        }
        drops.addItem(new ItemStack(this.getCrop(), extraCount));
        Containers.dropContents(level, pos, drops);

        // Seeds are still dropped at the same rate as BasicCrop
        return super.drops(cropEntity, level, pos, enchantments, quickReplant);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[getAge(cropEntity)] != null) {
            return this.SHAPE_BY_AGE[getAge(cropEntity)];
        }
        GreenThumbs.LOGGER.warn("{} species does not have a voxel shape for age {}", this, getAge(cropEntity));
        return this.SHAPE_BY_AGE[0];
    }
}
