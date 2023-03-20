package com.cfrishausen.greenthumbs.block.custom;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.CropType;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.genetics.genes.GrowthSpeedGene;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeBlockItem;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

import java.util.function.Supplier;

/**
 * Type of crop with only one harvestable item and no seeds. The crop is replanted from the one harvestable item. Ex. carrots, potatoes
 */
public class GTSimpleCropBlock extends Block implements IPlantable, BonemealableBlock {

    private final Supplier<GTGenomeBlockItem> CROP;

    public GTSimpleCropBlock(Properties pProperties, Supplier<GTGenomeBlockItem> crop) {
        super(pProperties);
        CROP = crop;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pLevel.getBlockEntity(pPos) instanceof GTCropBlockEntity cropBlock) {
            return SHAPE_BY_AGE[cropBlock.getAge()];
        }
        return SHAPE_BY_AGE[0];
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            boolean isDebugStick = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(GTItems.GT_DEBUG_STICK.get());
            // Allow debug stick to work on grown plant without quick replant
            if (!isDebugStick) {
                // quick replant from harvest implementation
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof GTCropBlockEntity cropEntity) {
                    if (cropEntity.isMaxAge()) {
                        quickReplant(pState, pLevel, pPos, cropEntity);
                    }
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        if (pState.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return pLevel.getBlockState(blockpos).canSustainPlant(pLevel, blockpos, Direction.UP, this);
        return this.mayPlaceOn(pLevel.getBlockState(blockpos));
    }

    /**
     * @return whether this block needs random ticking.
     */
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {

            if (!level.isAreaLoaded(pos, 1))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light
            if (level.getRawBrightness(pos, 0) >= 9) {
                int i = cropEntity.getAge();
                if (i < cropEntity.getMaxAge()) {
                    GrowthSpeedGene growthSpeedGene = (GrowthSpeedGene) cropEntity.getGenome().getGene(Genome.GROWTH_SPEED);
                    float f = growthSpeedGene.getGrowthSpeed(this, level, pos);
                    if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                        cropEntity.setAge(cropEntity.getAge() + 1);
                        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
                    }
                }
            }
        }

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof GTCropBlockEntity cropEntity) {
                drops(cropEntity, pLevel, pPos, false);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != this) return defaultBlockState();
        return state;
    }





}
