package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.client.ICropSpeciesExtensions;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * All assumed functionality for a crop species
 */
public interface ICropSpecies extends ICropSpeciesExtensions {

    /**
     *  Add which genes this crop type should have
     */
    Genome defineGenome();

    /**
     *  Build the CropState definition
     */
    void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder);

    /**
     * Register what the default crop state is for the species
     */
    void registerDefaultState();

    /**
     * What does crop need to do on a tick
     */
    void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity);

    /**
     * What needs to happen when species is right-clicked
     */
    InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, GTCropBlockEntity cropBlockEntity);

    /**
     *  What does a species do when it is removed. Do not include functionality found in BlockBehavior.onRemove().
     */
    void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving, ICropEntity cropEntity);

    /**
     * Drop items when crop is broken
     * @param quickReplant should the drops
     * @return item stack for what should remain in the location that crop was harvested from. Used for quick replant for crop that should be replanted
     */
    ItemStack drops(ICropEntity crop, Level level, BlockPos pos, boolean quickReplant);

    /**
     * @return Container with items that need to drop when crop is destroyed at non-specific crop states. Make sure index 0 of container contains plantable item because it will be subtracted from for quick plant.
     */
    @NonNull
    SimpleContainer stateNonSpecificDrop(ICropEntity crop, RandomSource random);

    /**
     * @return Container with items that need to drop when crop is destroyed at a specific crop state. Make sure index 0 of container contains plantable item because it will be subtracted from for quick plant.
     */
    @NonNull
    SimpleContainer stateSpecificDrop(ICropEntity crop, RandomSource random);

    /**
     * What does a crop do when it is quick replanted
     */
    void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, ICropEntity crop);

    /**
     * Override for crops that need functionality when there is entity colliding with them. For example berries hurt entities that move into them.
     */
    default void entityInside(BlockState state, Level level, BlockPos pos, Entity collidingEntity, GTCropBlockEntity cropBlockEntity) {}

    /**
     * Get a stack that has a reproduction tag on it. Helper for when a crop is broken the standard vanilla way
     */
    default ItemStack getStackWithReplantTag(ICropSpecies cropSpecies, ICropEntity cropEntity, Item item, RandomSource random) {
        ItemStack cropStack = new ItemStack(item, 1);
        cropStack.setTag(standardGenomelessTag(cropSpecies));
        cropStack.getTag().getCompound(NBTTags.INFO_TAG).put(NBTTags.GENOME_TAG, cropEntity.getGenome().writeReproductionTag(random));
        return cropStack;
    }

    /**
     *  Get a stack that has a cutting tag on it. Used for when cutting is taken from a crop.
     */
    default ItemStack getStackWithCuttingTag(ICropSpecies cropSpecies, ICropEntity cropEntity, Item item, RandomSource random) {
        ItemStack cropStack = new ItemStack(item, 1);
        cropStack.setTag(standardGenomelessTag(cropSpecies));
        cropStack.getTag().getCompound(NBTTags.INFO_TAG).put(NBTTags.GENOME_TAG, cropEntity.getGenome().writeCuttingTag(random));
        return cropStack;
    }

    default ItemStack stackWithCopiedTag(ICropSpecies cropSpecies, ICropEntity cropEntity, Item item) {
        ItemStack stack = new ItemStack(item, 1);
        stack.setTag(standardGenomelessTag(cropSpecies));
        stack.getTag().getCompound(NBTTags.INFO_TAG).put(NBTTags.GENOME_TAG, cropEntity.getGenome().writeTag());
        return stack;
    }

    /**
     *  Helper to format the nbt tag for a stack properly
     */
    default CompoundTag standardGenomelessTag(ICropSpecies cropSpecies) {
        CompoundTag tag = new CompoundTag();
        CompoundTag infoTag = new CompoundTag();
        tag.put(NBTTags.INFO_TAG, infoTag);

        // Species
        infoTag.putString(NBTTags.CROP_SPECIES_TAG, GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString());

        // Crop State
        CompoundTag stateTag = new CompoundTag();
        CropState defaultCropState = cropSpecies.getDefaultCropState();
        for (Property property : defaultCropState.getProperties()) {
            stateTag.put(property.getName(), NBTTags.encodeNbt(property.codec(), defaultCropState.getValue(property)));
        }
        infoTag.put(NBTTags.CROP_STATE_TAG, stateTag);

        return tag;
    }

    /**
     * @return is a crop able to survive.
     */
    boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos, Block block);

    /**
     * @param state The blockstate of the block that the crop is trying to be placed on top of
     * @return can a crop be placed on specified block?
     */
    default boolean mayPlaceOn(BlockState state) {
        return state.is(Blocks.FARMLAND);
    }

    void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, GTCropBlockEntity cropEntity);

    /**
     * What needs to happen when a crop needs to grow. Ie change age, set shape, update nearby entities, ect
     */
    void growCrops(Level level, ICropEntity cropEntity);

    /**
     *  Update when surrounding blocks change. For example revert attached stem to regular stem when vegetable block is broken. See AttachedStemBlock.java
     */
    @NonNull
    BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos, Block block, GTCropBlockEntity cropBlockEntity);

    /**
     * @return Does the species support right-clicked for an instant harvest and replant
     */
    boolean doesQuickReplant();

    /**
     * @return Is the crop responsive to bonemeal
     */
    default boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p50900, GTCropBlockEntity cropEntity) {return true;}

    /**
     * @return Are you able to take a clone cutting from this plant
     */
    boolean canTakeCutting(ICropEntity cropEntity);

    boolean isMaxAge(ICropEntity cropEntity);

    /**
     * Set the voxel shape for the crop
     * Number of shapes in the array must match the max age + 1 of the crop
     * @return
     */
    VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity);

    /**
     * @return how much the plant should age when bonemeal is used on it
     */
    int getBonemealAgeIncrease(Level level);

    /**
     * @return get the max age of a crop. If there is no age property return 0
     */
    default int getMaxAge() {
        return getAgeProperty() != null ? getAgeProperty().getPossibleValues().stream().max(Comparator.comparingInt(Integer::intValue)).get() : 0;
    }

    /**
     * Used when figuring out clone block in creative ie middle mouse click
     * @return what item needs to be cloned into the inventory
     */
    GTGenomeCropBlockItem getBaseItemId();

    /**
     * @return The seed that is used to grow the crop
     */
    GTGenomeCropBlockItem getSeed();

    /**
     * @return The item that the crop yields
     */
    ItemLike getCrop();

    /**
     * @return Resulting item from taking cutting/clone
     */
    GTGenomeCropBlockItem getCutting();

    /**
     * @return Age CropState property of species. If the species has no age property default to age property seven
     */
    @NotNull
    IntegerProperty getAgeProperty();

    int getAge(ICropEntity cropEntity);

    /**
     * @return Path name for the crop. Used for resource files
     */
    String getPath();

    /**
     * Get the default CropState for the species. Should be state that represents crop species for initial placement.
     */
    CropState getDefaultCropState();

    void setAge(ICropEntity cropEntity, int age);
}
