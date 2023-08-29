package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class AttachedStemCrop extends BasicCrop{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
    private final Supplier<ICropSpecies> fruit;



    public AttachedStemCrop(String name, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting, Supplier<ICropSpecies> fruit) {
        super(name, seed, crop, cutting);
        this.fruit = fruit;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        builder.add(FACING);
    }

    @Override
    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(this.FACING, Direction.NORTH);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, GTCropBlockEntity cropBlockEntity) {
        if (!level.isClientSide()) {
            ItemStack handStack = player.getMainHandItem();

            // Do cutting with shears/shear-like items
            if (handStack.is(Tags.Items.SHEARS)) {
                if (cropBlockEntity.getCropSpecies().canTakeCutting(cropBlockEntity)) {
                    SimpleContainer drops = new SimpleContainer(1);
                    ItemStack cuttingStack = getStackWithCuttingTag(this, cropBlockEntity, getCutting(), level.getRandom());
                    drops.addItem(cuttingStack);

                    cropBlockEntity.load(cuttingStack.getTag());
                    cropBlockEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getStemSpecies());
                    setAge(cropBlockEntity, 0);
                    Containers.dropContents(level, pos, drops);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(1);
        ItemStack dropStack = stackWithCopiedTag(((StemGrownCrop) this.fruit.get()).getStemSpecies(), cropEntity, getSeed());
        drops.addItem(dropStack);
        Containers.dropContents(level, pos, drops);
        return null;
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, ICropEntity cropEntity) {
        if (!doesQuickReplant()) {
            return;
        }
        ItemStack replantStack = drops(cropEntity, pLevel, pPos, true);
        if (replantStack != null) {
            CompoundTag replantTag = replantStack.getTag();
            if (replantTag != null && replantTag.contains(NBTTags.INFO_TAG)) {
                // load the genome from the cutting tag into the genome into the crop entity
                cropEntity.load(replantTag);
                // Species needs to be set first to make sure that the crop state is reloaded and we can adjust the age to 0
                cropEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getStemSpecies());
                cropEntity.getCropSpecies().setAge(cropEntity, 0);
                // sendBlockUpdated will reload model quads from baked model
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos, Block block, GTCropBlockEntity cropBlockEntity) {
        BlockEntity neighborEntity = level.getBlockEntity(facingPos);
        // only check in direction the block is facing
        if (facing == cropBlockEntity.getCropState().getValue(FACING)) {
            // is neighbor a crop entity
            if (neighborEntity instanceof GTCropBlockEntity neighborCropEntity) {
                // is neighbor a StemGrownCropBlock
                if (neighborCropEntity.getCropSpecies() instanceof StemGrownCrop neighborStemCrop) {
                    // is the StemGrownBlock the correct fruit
                    if (neighborStemCrop.getPath() == this.fruit.get().getPath()) {
                        return state;
                    }
                }
            }
            // Revert this AttachedStemCrop to a StemCrop
            cropBlockEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getStemSpecies());
            setAge(cropBlockEntity, cropBlockEntity.getCropSpecies().getMaxAge());
        }

        return state;
    }

    @Override
    public boolean doesQuickReplant() {
        return false;
    }

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        return AABBS.get(cropEntity.getCropState().getValue(this.FACING));
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        Direction.Plane.HORIZONTAL.stream().forEach(dir -> {
            modelMap.put(defaultCropState.setValue(FACING, dir), ModelResourceLocation.vanilla(pathName, "facing=" + dir.getName()));
        });

        return modelMap;
    }
}
