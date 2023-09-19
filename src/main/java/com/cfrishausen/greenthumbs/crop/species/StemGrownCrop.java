package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Crops like melon and pumpkin. Each pumpkin that this crop grows gets a genome that is a reproduction mix of the stem genome and a "other" random genome.
 * The seeds that are dropped by the fruit are a copy of that of the block entity (can check with debug stick). Each fruit that a stem grows can have a different genome.
 */
public class StemGrownCrop extends BasicCrop{

    private Supplier<ICropSpecies> stemSpecies;
    private Supplier<ICropSpecies> attachedStemSpecies;

    public StemGrownCrop(String pathName, Supplier<GTGenomeCropBlockItem> seed, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting, Supplier<ICropSpecies> stemSpecies, Supplier<ICropSpecies> attachedStemSpecies, boolean doesFortune) {
        super(pathName, seed, crop, cutting, doesFortune);
        this.stemSpecies = stemSpecies;
        this.attachedStemSpecies = attachedStemSpecies;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        return;
    }

    @Override
    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, GTCropBlockEntity cropBlockEntity) {
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack drops(ICropEntity cropEntity, Level level, BlockPos pos, Map<Enchantment, Integer> enchantments, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        drops.addItem(new ItemStack(this.getCrop(), 1 + cropEntity.getGenome().getExtraCropYield()));
        drops.addItem(stackWithCopiedTag(this.getStemSpecies(), cropEntity, getSeed()));
        Containers.dropContents(level, pos, drops);
        return null;
    }

    @Override
    public boolean mayPlaceOn(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
    }

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        return false;
    }

    @Override
    public boolean doesQuickReplant() {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        return Shapes.block();
    }

    public StemCrop getStemSpecies() {
        return ((StemCrop) this.stemSpecies.get());
    }
    public AttachedStemCrop getAttachedStemSpecies() {
        return ((AttachedStemCrop) this.attachedStemSpecies.get());
    }

    // Can be placed on any block

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p50900, GTCropBlockEntity cropEntity) {
        return false;
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        modelMap.put(defaultCropState, ModelResourceLocation.vanilla(pathName, ""));
        return modelMap;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return null;
    }
}
