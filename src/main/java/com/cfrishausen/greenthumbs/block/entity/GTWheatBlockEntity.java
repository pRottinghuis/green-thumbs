package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.genetics.Gene;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTWheatSeeds;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.data.MultipartModelData;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GTWheatBlockEntity extends BlockEntity{

    // See example @ https://www.mcjty.eu/docs/1.18/ep3

    public static final int MAX_AGE = 7;

    // Properties for communicating to baked model
    public static final ModelProperty<Integer> AGE = new ModelProperty<>();

    private int age;

    private Genome genome;

    public GTWheatBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.GT_WHEAT.get(), pos, state);
        age = 0;
    }

    public GTWheatBlockEntity(BlockPos pos, BlockState state, Genome genome) {
        super(GTBlockEntities.GT_WHEAT.get(), pos, state);
        this.age = 0;
        this.genome = genome;
    }

    // Used for initializing genome once entity has been added to level
    @Override
    public void onLoad() {
        if (this.genome == null) {
            this.genome = new Genome(level.random);
        }
        super.onLoad();
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(AGE, age)
                .build();
    }

    /**
     * Save to nbt on world close
     */
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putString(GreenThumbs.ID + ".Genome", genome.toString());
        nbt.putInt(GreenThumbs.ID + ".Age", this.age);
        super.saveAdditional(nbt);
    }

    /**
     * Load data on world open
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(GreenThumbs.ID + ".Genome")) {
            this.genome = new Genome(nbt.getString(GreenThumbs.ID + ".Genome"));
        }
        if (nbt.contains(GreenThumbs.ID + ".Age")) {
            age = nbt.getInt(GreenThumbs.ID + ".Age");
        }

    }

    public static <E extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, E e) {
        return;
    }

    // Crop Block

    public int getMaxAge() {
        return MAX_AGE;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMaxAge() {
        return age >= this.getMaxAge();
    }

    public void growCrops(Level pLevel) {
        int i = this.getAge() + this.getBonemealAgeIncrease(pLevel);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }
        this.age = i;
        markUpdated();
    }

    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, 2, 5);
    }

    public static float getGrowthSpeed(Block pBlock, BlockGetter pLevel, BlockPos pPos) {
        float f = 1.0F;
        BlockPos blockpos = pPos.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState blockstate = pLevel.getBlockState(blockpos.offset(i, 0, j));
                if (blockstate.canSustainPlant(pLevel, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, (net.minecraftforge.common.IPlantable) pBlock)) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(pLevel, pPos.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pPos.north();
        BlockPos blockpos2 = pPos.south();
        BlockPos blockpos3 = pPos.west();
        BlockPos blockpos4 = pPos.east();
        boolean flag = pLevel.getBlockState(blockpos3).is(pBlock) || pLevel.getBlockState(blockpos4).is(pBlock);
        boolean flag1 = pLevel.getBlockState(blockpos1).is(pBlock) || pLevel.getBlockState(blockpos2).is(pBlock);
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = pLevel.getBlockState(blockpos3.north()).is(pBlock) || pLevel.getBlockState(blockpos4.north()).is(pBlock) || pLevel.getBlockState(blockpos4.south()).is(pBlock) || pLevel.getBlockState(blockpos3.south()).is(pBlock);
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    // Indicate that entity needs to be reloaded. Used for reloading BakedModel
    private void markUpdated() {
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }


    // The getUpdateTag()/handleUpdateTag() pair is called whenever the client receives a new chunk
    // it hasn't seen before. i.e. the chunk is loaded
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveClientData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            loadClientData(tag);
        }
    }

    private void saveClientData(CompoundTag tag) {
        CompoundTag infoTag = new CompoundTag();
        tag.put("Info", infoTag);
        infoTag.putInt(GreenThumbs.ID + ".Age", age);

    }

    private void loadClientData(CompoundTag tag) {
        if (tag != null && tag.contains("Info")) {
            CompoundTag infoTag = tag.getCompound("Info");
            age = infoTag.getInt(GreenThumbs.ID + ".Age");
        }
    }

    // The getUpdatePacket()/onDataPacket() pair is used when a block update happens on the client
    // (a blockstate change or an explicit notificiation of a block update from the server). It's
    // easiest to implement them based on getUpdateTag()/handleUpdateTag()


    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        // This is called client side: remember the current state of the values that we're interested in
        int oldAge = this.age;
        String oldGenome = this.genome.toString();

        CompoundTag tag = pkt.getTag();
        // This will call loadClientData()
        handleUpdateTag(tag);

        // If any of the values was changed we request a refresh of our model data and send a block update (this will cause
        // the baked model to be recreated)
        if (oldAge != age || oldGenome != genome.toString()) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }




    /**
     * @param placementSwap should be a placementSwap for a quick harvest and plant
     * @return seed for quick swap. Null if there is no quick swap
     */

    // TODO add fortune and remove drops in creative
    public ItemStack drops(boolean placementSwap) {
        SimpleContainer drops = new SimpleContainer(2);
        ItemStack seedDropStack = getSeedsStack();
        // used to return seed with proper genome for quick replant
        ItemStack seedReplant = null;
        if (placementSwap) {
            // Set seed for return and reduce drop stack
            seedReplant = new ItemStack(seedDropStack.getItem());
            seedReplant.getOrCreateTag().putString(GreenThumbs.ID + ".Genome", genome.toString());
            seedDropStack.shrink(1);
        }
        drops.setItem(0, seedDropStack);
        if (isMaxAge()) {
            drops.setItem(1, new ItemStack(Items.WHEAT));
        }
        Containers.dropContents(this.level, this.worldPosition, drops);
        return seedReplant;
    }

    /**
     *
     * @return Item stack of seeds for drop
     */
    private ItemStack getSeedsStack() {
        ItemStack seedsStack = new ItemStack(GTItems.GT_WHEAT_SEEDS.get());
        seedsStack.getOrCreateTag().putString(GreenThumbs.ID + ".Genome", genome.toString());
        return seedsStack;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }
}