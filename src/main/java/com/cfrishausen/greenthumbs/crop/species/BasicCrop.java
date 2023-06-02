package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicCrop implements ICropSpecies {

    private GTGenomeCropBlockItem seed;
    private Item crop;
    private int maxAge;

    public BasicCrop(GTGenomeCropBlockItem seeds, Item crop, int maxAge) {
        this.seed =seeds;
        this.crop = crop;
        if (maxAge > 0) {
            this.maxAge = maxAge;
        } else {
            this.maxAge = 7;
        }
    }

    /**
     * used for default age 7 crop
     */
    public BasicCrop(GTGenomeCropBlockItem seeds, Item crop) {
        this(seeds, crop, 7);
    }

    @Override
    public Genome defineGenome() {
        Map<String, String> genes = new HashMap<>();
        genes.put(Genome.GROWTH_SPEED, "Gg");
        genes.put(Genome.TEMPERATURE_PREFERENCE, "Tt");
        return new Genome(genes);
    }

    @Override
    public ItemStack allAgeDrop(ICropEntity crop) {
        return getStackWithReplantTag(crop, this.seed);
    }

    @Override
    public ItemStack maxAgeDrop(ICropEntity crop) {
        return getStackWithReplantTag(crop, this.crop);
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, ICropEntity crop) {
        ItemStack replantItem = drops(crop, pLevel, pPos, true);
        if (replantItem != null) {
            CompoundTag infoTag = replantItem.getTag();
            if (infoTag != null && infoTag.contains(NBTTags.INFO_TAG)) {
                infoTag.putInt(NBTTags.AGE_TAG, 0);
                crop.load(infoTag);
                // sendBlockUpdated will cause new baked model to be created for crop at age 0
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos, Block block) {
        BlockPos blockpos = pPos.below();
        if (pState.getBlock() == block) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return pLevel.getBlockState(blockpos).canSustainPlant(pLevel, blockpos, Direction.UP, (GTSimpleCropBlock) block);
        if (pLevel.getBlockEntity(pPos) instanceof GTCropBlockEntity cropEntity) {
            return pLevel.getRawBrightness(pPos, 0) >= 8 && cropEntity.getCropSpecies().mayPlaceOn(pLevel.getBlockState(blockpos));
        }
        return false;
    }



    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity crop) {
        if (!level.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = crop.getAge();
            if (i < crop.getMaxAge()) {
                float f = crop.getGenome().getGrowthSpeed(block, level, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    crop.setAge(crop.getAge() + 1);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
                }
            }
        }
    }

    // TODO add fortune drops
    public ItemStack drops(ICropEntity crop, Level level, BlockPos pos, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        // default item that all crops drop regardless of age
        ItemStack allAgeDropStack = allAgeDrop(crop);
        ItemStack cropReplantStack = null;
        if (quickReplant) {
            // Set crop for return and reduce drop stack
            cropReplantStack = allAgeDropStack.copy();
            // Take one item out of the drop stack and return it for a quickReplant
            cropReplantStack.setCount(1);
            allAgeDropStack.shrink(1);
        }
        drops.setItem(0, allAgeDropStack);
        // Add crop max age drop
        if (crop.isMaxAge()) {
            drops.setItem(1, maxAgeDrop(crop));
        }
        Containers.dropContents(level, pos, drops);
        return cropReplantStack;
    }

    @Override
    public VoxelShape[] getVoxelShapes() {
        return new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
    }

    @Override
    public int getMaxAge() {
        return this.maxAge;
    }

    public GTGenomeCropBlockItem getBaseItemId() {
        return this.seed;
    }
}
