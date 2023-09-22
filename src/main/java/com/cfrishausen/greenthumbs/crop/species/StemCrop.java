package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Supplier;

public class StemCrop extends BasicCrop {
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D),
    };

    private final Supplier<ICropSpecies> fruit;

    public StemCrop(String name, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting, Supplier<ICropSpecies> fruit, boolean doesFortune) {
        super(name, seed, crop, cutting, doesFortune);
        this.fruit = fruit;
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= cropEntity.getGenome().getLightTolerance()) {
            float f = cropEntity.getGenome().getGrowthSpeed(block, level, pos);
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                int i = cropEntity.getCropState().getValue(AGE_7);
                if (i < getMaxAge()) {
                    setAge(cropEntity, cropEntity.getCropSpecies().getAge(cropEntity) + 1);
                } else {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos fruitPos = pos.relative(direction);
                    BlockState belowFruitPosState = level.getBlockState(fruitPos.below());

                    if (level.isEmptyBlock(fruitPos) && this.getFruit().mayPlaceOn(belowFruitPosState)) {
                        // set block in targeted position to GTCropBlock and then set species to fruit
                        level.setBlockAndUpdate(fruitPos, GTBlocks.GT_VEGETABLE_BLOCK.get().defaultBlockState());
                        GTCropBlockEntity fruitCropBlockEntity = ((GTCropBlockEntity) level.getBlockEntity(fruitPos));

                        // create a nbt file to load onto the fruit crop that will transfer over the reproduction genome
                        CompoundTag fruitLoadTag = standardGenomelessTag(this);
                        fruitLoadTag.getCompound(NBTTags.INFO_TAG).put(NBTTags.GENOME_TAG, cropEntity.getGenome().writeReproductionTag(level.getRandom()));
                        fruitCropBlockEntity.load(fruitLoadTag);

                        // set the crop species because the nbt load will contain the stem species not the fruit species.
                        fruitCropBlockEntity.setCropSpecies(this.fruit.get());
                        fruitCropBlockEntity.markUpdated();

                        // change the old stem to be an attached stem connected to the fruit
                        cropEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getAttachedStemSpecies());
                        cropEntity.setCropState(cropEntity.getCropState().setValue(AttachedStemCrop.FACING, direction));
                    }
                }
            }
        }
    }

    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, Map<Enchantment, Integer> enchantments, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        drops.addItem(stackWithCopiedTag(this, cropEntity, getSeed()));
        Containers.dropContents(level, pos, drops);
        return null;
    }

    @Override
    public boolean doesQuickReplant() {
        return false;
    }

    // TODO stems cannot be bonemealed once they have reached max age
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, GTCropBlockEntity cropEntity) {
        super.performBonemeal(level, random, pos, state, cropEntity);
        // Ensure that there is a chance of growing a fruit
        state.randomTick(level, pos, random);
        // Extra chance to grow fruit when boneheaded
        if (level.getRandom().nextFloat() < cropEntity.getGenome().getFertilizerResponse()) {
            state.randomTick(level, pos, random);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[getAge(cropEntity)] != null) {
            return this.SHAPE_BY_AGE[getAge(cropEntity)];
        }
        GreenThumbs.LOGGER.warn("{} species does not have a voxel shape for {} for state {}", this, cropEntity, cropEntity.getCropState());
        return this.SHAPE_BY_AGE[0];
    }

    public StemGrownCrop getFruit() {
        return ((StemGrownCrop) this.fruit.get());
    }
}
