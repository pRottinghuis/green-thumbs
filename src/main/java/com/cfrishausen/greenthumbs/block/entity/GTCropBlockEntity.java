package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.CropType;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GTCropBlockEntity extends BlockEntity {

    // See example @ https://www.mcjty.eu/docs/1.18/ep3

    public static final String INFO_TAG = GreenThumbs.ID + "." + "Info";
    public static final String AGE_TAG = GreenThumbs.ID + "." + "Age";

    private CropType cropType;

    public static ModelProperty<Integer> AGE = new ModelProperty<>();

    int MAX_AGE = 7;


    private int age;

    private Genome genome;

    public GTCropBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.GT_WHEAT.get(), pos, state);
        age = 0;
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
        CompoundTag saveTag = new CompoundTag();
        nbt.put(INFO_TAG, saveTag);
        saveTag.put(Genome.GENOME_TAG, genome.writeTag());
        saveTag.putInt(AGE_TAG, this.age);
        super.saveAdditional(nbt);
    }

    /**
     * Load data on world open
     * Will look for tags in higharchy
     * "modId".
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(INFO_TAG)) {
            CompoundTag saveTag = nbt.getCompound(INFO_TAG);
            this.genome = new Genome(saveTag.getCompound(Genome.GENOME_TAG));
            age = nbt.getInt(AGE_TAG);
        }
    }

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

    private int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, 2, 5);
    }

    public void markUpdated() {
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
        tag.put(INFO_TAG, infoTag);
        tag.putInt(AGE_TAG, age);

    }

    private void loadClientData(CompoundTag tag) {
        if (tag != null && tag.contains(INFO_TAG)) {
            CompoundTag infoTag = tag.getCompound(INFO_TAG);
            age = infoTag.getInt(AGE_TAG);
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
        Genome oldGenome = this.genome;

        CompoundTag tag = pkt.getTag();
        // This will call loadClientData()
        handleUpdateTag(tag);

        // If any of the values was changed we request a refresh of our model data and send a block update (this will cause
        // the baked model to be recreated)
        if (oldAge != age || !(oldGenome.equals(this.genome))) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public Genome getGenome() {
        return genome;
    }
}