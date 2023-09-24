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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BasicCrop implements ICropSpecies {

    public static final IntegerProperty AGE_7 = BlockStateProperties.AGE_7;
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

    private Supplier<GTGenomeCropBlockItem> seed;
    private final Supplier<Item> crop;
    private final Supplier<GTGenomeCropBlockItem> cutting;

    // name of model file to look in
    protected final String pathName;

    private boolean doesFortune;

    public BasicCrop(String pathName, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting, boolean doesFortune) {
        this.pathName = pathName;
        this.seed = seed;
        this.crop = crop;
        this.cutting = cutting;
        this.doesFortune = doesFortune;
        StateDefinition.Builder<ICropSpecies, CropState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        this.cropStateDef = builder.create(ICropSpecies::getDefaultCropState, CropState::new);
        this.registerDefaultState();
    }

    @Override
    public Genome defineGenome() {
        Map<String, String> genes = new HashMap<>();
        genes.put(Genome.LIGHT_TOLERANCE, "Ll");
        genes.put(Genome.GROWTH_SPEED, "Gg");
        genes.put(Genome.MUTATIVITY, "Mm");
        genes.put(Genome.CROP_YIELD, "Cc");
        genes.put(Genome.FERTILIZER_RESPONSE, "Ff");
        return new Genome(genes);
    }

    /**
     * Default crop (ex wheat) only have an age property
     */
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        if (getAgeProperty() != null) {
            builder.add(getAgeProperty());
        }
    }

    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(AGE_7, 0);
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (!level.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= cropEntity.getGenome().getLightTolerance()) {
            if (!isMaxAge(cropEntity)) {
                float f = cropEntity.getGenome().getGrowthSpeed(block, level, pos);
                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                    setAge(cropEntity, getAge(cropEntity) + 1);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, GTCropBlockEntity cropBlockEntity) {
        ItemStack handStack = player.getMainHandItem();

        // Do cutting with shears/shear-like items
        if (handStack.is(Tags.Items.SHEARS)) {
            if (cropBlockEntity.getCropSpecies().canTakeCutting(cropBlockEntity)) {
                SimpleContainer drops = new SimpleContainer(1);
                ItemStack cuttingStack = getStackWithCuttingTag(this, cropBlockEntity, getCutting(), level.getRandom());
                drops.addItem(cuttingStack);
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                setAge(cropBlockEntity, 0);
                Containers.dropContents(level, pos, drops);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

        } else {
            // Don't Quick Replant on debug stick
            if (!handStack.is(GTItems.GT_DEBUG_STICK.get())) {
                // quick replant from harvest implementation
                if (isMaxAge(cropBlockEntity)) {
                    cropBlockEntity.getCropSpecies().quickReplant(state, level, pos, handStack.getAllEnchantments(), cropBlockEntity);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, Map<Enchantment, Integer> enchantments, boolean quickReplant) {
        ItemStack returnStack = null;
        SimpleContainer drops;
        ICropSpecies cropSpecies = cropEntity.getCropSpecies();
        RandomSource random = level.getRandom();
        if (cropSpecies.isMaxAge(cropEntity)) {
            drops = new SimpleContainer(2);
            int seedCount = 1;

            for (int i = 0; i < 3; ++i) {
                if (random.nextFloat() < 0.5714286) {
                    ++seedCount;
                }
            }

            // Add extra seeds on fortune. Used for wheat
            if (getDoesFortune() && enchantments.containsKey(Enchantments.BLOCK_FORTUNE)) {
                // See minecraft:wheat.json loot table
                for (int i = 0; i < enchantments.get(Enchantments.BLOCK_FORTUNE); ++i) {
                    if (random.nextFloat() < 0.5714286) {
                        ++seedCount;
                    }
                }
            }
            drops.addItem(getStackWithReplantTag(this, cropEntity, getSeed(), seedCount, random));
            drops.addItem(new ItemStack(getCrop(), 1 + cropEntity.getGenome().getExtraCropYield()));

            if (quickReplant) {
                returnStack = drops.getItem(0);
                if (!returnStack.isEmpty()) {
                    CompoundTag returnTag = drops.getItem(0).getTag();
                    ItemStack reducedStack = new ItemStack(returnStack.getItem(), returnStack.getCount() - 1);
                    reducedStack.setTag(returnTag);
                    drops.setItem(0, reducedStack);
                    returnStack.setCount(1);
                }
            }
        } else {
            drops = new SimpleContainer(stackWithCopiedTag(this, cropEntity, getSeed()));
        }
        Containers.dropContents(level, pos, drops);
        return returnStack;
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, Map<Enchantment, Integer> enchantments, ICropEntity cropEntity) {
        if (!doesQuickReplant()) {
            return;
        }
        ItemStack replantStack = drops(cropEntity, pLevel, pPos, enchantments, true);
        if (replantStack != null) {
            CompoundTag replantTag = replantStack.getTag();
            if (replantTag != null && replantTag.contains(NBTTags.INFO_TAG)) {
                cropEntity.load(replantTag);
                cropEntity.getCropSpecies().setAge(cropEntity, 0);
                // sendBlockUpdated will reload model quads from baked model
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos, Block block) {
        BlockPos belowPos = pPos.below();
        if (pState.getBlock() == block) { //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            if (pLevel.getBlockEntity(pPos) instanceof GTCropBlockEntity cropEntity) {
                // Can survive brightness is 1 lower than light required to grow.
                int brightnessReq = cropEntity.getGenome().getLightTolerance() == 1 ? 0 : cropEntity.getGenome().getLightTolerance() - 1;
                // check valid brightness and if can survive on the block below
                return (pLevel.getRawBrightness(pPos, 0) >= brightnessReq || pLevel.canSeeSky(pPos)) && cropEntity.getCropSpecies().mayPlaceOn(pLevel.getBlockState(belowPos));
            }
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, GTCropBlockEntity cropEntity) {
        growCrops(level, cropEntity);
        // Grow crops again according to chance from genome fertilizer response trait
        if (level.getRandom().nextFloat() < cropEntity.getGenome().getFertilizerResponse()) {
            growCrops(level, cropEntity);
        }
    }

    public void growCrops(Level level, ICropEntity cropEntity) {
        int i = getAge(cropEntity) + getBonemealAgeIncrease(level);
        int j = getMaxAge();
        if (i > j) {
            i = j;
        }
        setAge(cropEntity, i);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos, Block block, GTCropBlockEntity cropBlockEntity) {
        if (!canSurvive(state, level, currentPos, block)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean doesQuickReplant() {
        return true;
    }

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        return isMaxAge(cropEntity);
    }

    @Override
    public boolean isMaxAge(ICropEntity cropEntity) {
        CropState entityCropState = cropEntity.getCropState();
        if (getAgeProperty() != null && entityCropState.hasProperty(getAgeProperty())) {
            return getAge(cropEntity) == getMaxAge();
        }
        return false;
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[getAge(cropEntity)] != null) {
            return this.SHAPE_BY_AGE[getAge(cropEntity)];
        }
        GreenThumbs.LOGGER.warn("{} species does not have a voxel shape for {} for state {}", this, cropEntity, cropEntity.getCropState());
        return this.SHAPE_BY_AGE[0];
    }

    public GTGenomeCropBlockItem getBaseItemId() {
        return this.seed.get();
    }

    @Override
    public int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 2, 5);
    }

    @Override
    public GTGenomeCropBlockItem getSeed() {
        return this.seed.get();
    }

    @Override
    public ItemLike getCrop() {
        return this.crop.get();
    }

    @Override
    public GTGenomeCropBlockItem getCutting() {
        return this.cutting.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return this.AGE_7;
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        for (int age = 0; age <= getMaxAge(); age++) {
            if (getAgeProperty() != null) {
                modelMap.put(defaultCropState.setValue(getAgeProperty(), age), ModelResourceLocation.vanilla(pathName, "age=" + age));
            }
        }
        return modelMap;
    }

    @Override
    public String getPath() {
        return this.pathName;
    }

    @Override
    public int getAge(ICropEntity cropEntity) {
        CropState entityCropState = cropEntity.getCropState();
        if (getAgeProperty() != null && entityCropState.hasProperty(getAgeProperty())) {
            return entityCropState.getValue(getAgeProperty());
        }
        return 0;
    }

    public final CropState getDefaultCropState() {
        return this.defaultCropState;
    }

    @Override
    public boolean getDoesFortune() {
        return this.doesFortune;
    }

    @Override
    public void setAge(ICropEntity cropEntity, int age) {
        if (getAgeProperty() == null) {
            return;
        }
        CropState entityCropState = cropEntity.getCropState();
        if (entityCropState.hasProperty(getAgeProperty())) {
            cropEntity.setCropState(entityCropState.setValue(getAgeProperty(), age));
        } else {
            GreenThumbs.LOGGER.warn("Trying to set age on {} species which does not have an age property", this);
        }
    }
}
