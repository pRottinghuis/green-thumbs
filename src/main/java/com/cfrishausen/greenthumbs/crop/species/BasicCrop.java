package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BasicCrop implements ICropSpecies {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    protected final StateDefinition<ICropSpecies, CropState> cropStateDef;
    protected CropState defaultCropState;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    private GTGenomeCropBlockItem seed;
    private final Item crop;
    private GTGenomeCropBlockItem cutting;

    // name of model file to look in
    protected final String pathName;

    public BasicCrop(String pathName, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting) {
        this.pathName = pathName;
        this.seed = seed;
        this.crop = crop;
        this.cutting = cutting;
        StateDefinition.Builder<ICropSpecies, CropState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        this.cropStateDef = builder.create(ICropSpecies::defaultCropState, CropState::new);
        this.registerDefaultState();
    }

    public final CropState defaultCropState() {
        return this.defaultCropState;
    }

    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        builder.add(getAgeProperty());

    }

    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(AGE, 0);
    }

    /**
     * used for default age 7 crop
     */
    @Override
    public Genome defineGenome() {
        Map<String, String> genes = new HashMap<>();
        genes.put(Genome.GROWTH_SPEED, "Gg");
        genes.put(Genome.TEMPERATURE_PREFERENCE, "Tt");
        genes.put(Genome.MUTATIVITY, "Mm");
        return new Genome(genes);
    }

    @Override
    public ItemStack allAgeDrop(ICropEntity crop, RandomSource random) {
        return getStackWithReplantTag(crop, this.seed, random);
    }

    @Override
    public ItemStack maxAgeDrop(ICropEntity crop, RandomSource random) {
        return new ItemStack(this.crop);
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, ICropEntity crop) {
        if (!doesQuickReplant()) {
            return;
        }
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
    public boolean doesQuickReplant() {
        return true;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, GTCropBlockEntity cropBlockEntity) {
        if (!level.isClientSide()) {
            ItemStack handStack = player.getMainHandItem();

            if (handStack.is(Tags.Items.SHEARS)) {
                if (cropBlockEntity.getCropSpecies().canTakeCutting(cropBlockEntity)) {
                    SimpleContainer drops = new SimpleContainer(1);
                    ItemStack cuttingStack = getStackWithCuttingTag(cropBlockEntity, getCutting(), level.getRandom());
                    drops.addItem(cuttingStack);
                    setAge(cropBlockEntity, 0);
                    Containers.dropContents(level, pos, drops);
                }

            } else {
                // Don't Quick Replant on debug stick
                if (!handStack.is(GTItems.GT_DEBUG_STICK.get())) {
                    // quick replant from harvest implementation
                    if (isMaxAge(cropBlockEntity)) {
                        cropBlockEntity.getCropSpecies().quickReplant(state, level, pos, cropBlockEntity);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (!level.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            if (!isMaxAge(cropEntity)) {
                float f = cropEntity.getGenome().getGrowthSpeed(block, level, pos);
                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                    setAge(cropEntity, getAge(cropEntity) + 1);
                }
            }
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, GTCropBlockEntity cropEntity) {
        growCrops(level, cropEntity);
    }

    public void growCrops(Level level, ICropEntity cropEntity) {
        int i = getAge(cropEntity) + getBonemealAgeIncrease(level);
        int j = getMaxAge();
        if (i > j) {
            i = j;
        }
        setAge(cropEntity, i);
    }

    // TODO add fortune drops
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        // default item that all crops drop regardless of age
        ItemStack allAgeDropStack = allAgeDrop(cropEntity, level.getRandom());
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
        if (isMaxAge(cropEntity)) {
            drops.setItem(1, maxAgeDrop(cropEntity, level.getRandom()));
        }
        Containers.dropContents(level, pos, drops);
        return cropReplantStack;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[getAge(cropEntity)] != null) {
            return this.SHAPE_BY_AGE[getAge(cropEntity)];
        }
        GreenThumbs.LOGGER.warn("BasicCrop species does not have a voxel shape for {} for state {}", cropEntity, cropEntity.getCropState());
        return this.SHAPE_BY_AGE[0];
    }

    // TODO replace with access transformer IntegerProperty.java max field
    @Override
    public int getMaxAge() {
        return getAgeProperty().getPossibleValues().stream().max(Comparator.comparingInt(Integer::intValue)).get();
    }

    public GTGenomeCropBlockItem getBaseItemId() {
        return this.seed;
    }

    // TODO change so that dynamic with crops with different max ages
    @Override
    public int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 2, 5);
    }

    @Override
    public GTGenomeCropBlockItem getSeed() {
        return this.seed;
    }

    @Override
    public ItemLike getCrop() {
        return this.crop;
    }

    @Override
    public GTGenomeCropBlockItem getCutting() {
        return this.cutting;
    }

    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return this.AGE;
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        for (int age = 0; age <= getMaxAge(); age++) {
            modelMap.put(defaultCropState.setValue(getAgeProperty(), age), ModelResourceLocation.vanilla(pathName, "age=" + age));
        }
        return modelMap;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos, Block block, GTCropBlockEntity cropBlockEntity) {
        if (canSurvive(state, level, currentPos, block)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public String getPath() {
        return this.pathName;
    }

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        return isMaxAge(cropEntity);
    }

    @Override
    public int getAge(ICropEntity cropEntity) {
        CropState entityCropState = cropEntity.getCropState();
        if (entityCropState.hasProperty(getAgeProperty())) {
            return entityCropState.getValue(getAgeProperty());
        }
        GreenThumbs.LOGGER.warn("Trying to access crop species {} with no age property", this);
        return 0;
    }

    @Override
    public void setAge(ICropEntity cropEntity, int age) {
        CropState entityCropState = cropEntity.getCropState();
        if (entityCropState.hasProperty(getAgeProperty())) {
            cropEntity.setCropState(entityCropState.setValue(getAgeProperty(), age));
        } else {
            GreenThumbs.LOGGER.warn("Trying to set age on {} species which does not have an age property", this);
        }
    }

    @Override
    public boolean isMaxAge(ICropEntity cropEntity) {
        CropState entityCropState = cropEntity.getCropState();
        if (entityCropState.hasProperty(getAgeProperty())) {
            return getAge(cropEntity) == getMaxAge();
        }
        GreenThumbs.LOGGER.warn("Trying to check max age on {} which has no age property", this);
        return false;
    }
}
