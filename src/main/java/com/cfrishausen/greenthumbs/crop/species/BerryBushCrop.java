package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class BerryBushCrop extends BasicCrop{

    public static final IntegerProperty AGE_3 = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BerryBushCrop(String pathName, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting) {
        super(pathName, seed, crop, cutting);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        builder.add(AGE_3);
    }

    @Override
    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(AGE_3, 0);
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        int age = getAge(cropEntity);
        if (age < 3 && level.getRawBrightness(pos.above(), 0) >= 9) {
            cropEntity.setCropState(cropEntity.getCropState().setValue(AGE_3, getAge(cropEntity) + 1));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, GTCropBlockEntity cropBlockEntity) {
        int age = getAge(cropBlockEntity);
        boolean flag = age == 3;
        if (!flag && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else if (player.getItemInHand(hand).is(Tags.Items.SHEARS) && cropBlockEntity.getCropSpecies().canTakeCutting(cropBlockEntity)) {
            SimpleContainer drops = new SimpleContainer(1);
            ItemStack cuttingStack = getStackWithCuttingTag(this, cropBlockEntity, getCutting(), level.getRandom());
            drops.addItem(cuttingStack);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            setAge(cropBlockEntity, 0);
            Containers.dropContents(level, pos, drops);
        } else if (age > 1) {
            // berry harvest is needed
            int berryCount = 1 + level.random.nextInt(2);
            // Drop berries
            Block.popResource(level, pos, new ItemStack(this.getCrop(), berryCount + (flag ? 1 : 0)));
            // drop seeds
            Block.popResource(level, pos, getStackWithReplantTag(this, cropBlockEntity, this.getSeed(), level.getRandom()));
            level.playSound((Player)null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            cropBlockEntity.setCropState(cropBlockEntity.getCropState().setValue(AGE_3, 1));
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    /**
     *  Berries will drop their berry and a seed when harvested or destroyed. The seed that comes from harvesting will have a pollination reproduction
     *  genome while the seed from breaking the crop will have a genome identical to that of the parent plant. When the bush is broken
     *  when there are berries grown on it those berries will also drop.
     */
    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, boolean quickReplant) {
        SimpleContainer drops;
        if (getAge(cropEntity) > 1 ) {
            drops = stateSpecificDrop(cropEntity, level.getRandom());
        } else {
            drops = stateNonSpecificDrop(cropEntity, level.getRandom());
        }
        Containers.dropContents(level, pos, drops);
        return null;
    }

    @Override
    public @NotNull SimpleContainer stateNonSpecificDrop(ICropEntity cropEntity, RandomSource random) {
        SimpleContainer dropContainer = new SimpleContainer(1);
        dropContainer.addItem(stackWithCopiedTag(this, cropEntity, getSeed()));
        return dropContainer;
    }

    @Override
    public @NonNull SimpleContainer stateSpecificDrop(ICropEntity cropEntity, RandomSource random) {
        SimpleContainer dropContainer = new SimpleContainer(2);
        // Drop a seed
        dropContainer.addItem(stackWithCopiedTag(this, cropEntity, getSeed()));
        // drop berries
        int berryCount = 1 + random.nextInt(2);
        dropContainer.addItem(new ItemStack(this.getCrop(), berryCount + (cropEntity.getCropState().getValue(AGE_3) == 3 ?  1 : 0)));
        return dropContainer;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity collidingEntity, GTCropBlockEntity cropBlockEntity) {
        if (collidingEntity instanceof LivingEntity && collidingEntity.getType() != EntityType.FOX && collidingEntity.getType() != EntityType.BEE) {
            collidingEntity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));
            if (!level.isClientSide && getAge(cropBlockEntity) > 0 && (collidingEntity.xOld != collidingEntity.getX() || collidingEntity.zOld != collidingEntity.getZ())) {
                double d0 = Math.abs(collidingEntity.getX() - collidingEntity.xOld);
                double d1 = Math.abs(collidingEntity.getZ() - collidingEntity.zOld);
                if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
                    collidingEntity.hurt(level.damageSources().sweetBerryBush(), 1.0F);
                }
            }
        }
    }

    @Override
    public boolean mayPlaceOn(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p50900, GTCropBlockEntity cropEntity) {
        if (cropEntity.getCropState().hasProperty(AGE_3)) {
            return cropEntity.getCropState().getValue(AGE_3) < 3;
        }
        GreenThumbs.LOGGER.warn("Using invalid crop state property on {} species", this);
        return false;
    }

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        if (cropEntity.getCropState().hasProperty(AGE_3)) {
            return cropEntity.getCropState().getValue(AGE_3) == 1;
        }
        return false;
    }

    @Override
    public boolean doesQuickReplant() {
        return false;
    }

    @Override
    public int getBonemealAgeIncrease(Level level) {
        return 1;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (getAge(cropEntity) == 0) {
            return SAPLING_SHAPE;
        } else {
            return getAge(cropEntity) < 3 ? MID_GROWTH_SHAPE : Shapes.block();
        }
    }

    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return AGE_3;
    }
}
