package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GTCropBlockEntity extends BlockEntity implements com.cfrishausen.greenthumbs.crop.ICrop {

    // See example @ https://www.mcjty.eu/docs/1.18/ep3

    private ICropSpecies cropSpecies = GTCropSpecies.GT_WHEAT.get();

    public static ModelProperty<Integer> AGE = new ModelProperty<>();
    public static ModelProperty<String> CROP_TYPE = new ModelProperty<>();

    int MAX_AGE = 7;


    private int age;

    private Genome genome = new Genome();

    public GTCropBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.GT_CROP_ENTITY.get(), pos, state);
        age = 0;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(AGE, age)
                .with(CROP_TYPE, GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString())
                .build();
    }

    /**
     * Save to nbt on world close / block unload
     */
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        saveStandardNBT(nbt);
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
        readStandardNBT(nbt);
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
        // saved on the server
        saveStandardNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            readStandardNBT(tag);
        }
    }

    private void saveStandardNBT(CompoundTag nbt) {
        CompoundTag saveTag = new CompoundTag();
        nbt.put(NBTTags.INFO_TAG, saveTag);
        saveTag.put(Genome.GENOME_TAG, genome.writeTag());
        saveTag.putInt(NBTTags.AGE_TAG, this.age);
        if (cropSpecies != null) {
            saveTag.putString(NBTTags.CROP_SPECIES_TAG, GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString());
        }
    }

    private void readStandardNBT(CompoundTag nbt) {
        if (nbt.contains(NBTTags.INFO_TAG)) {
            CompoundTag saveTag = nbt.getCompound(NBTTags.INFO_TAG);
            if (saveTag.contains(Genome.GENOME_TAG)) {
                this.genome.setGenomeFromTag(saveTag);
            }
            if (saveTag.contains(NBTTags.CROP_SPECIES_TAG)) {
                this.cropSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValue(new ResourceLocation(saveTag.getString(NBTTags.CROP_SPECIES_TAG)));
            }
            if (saveTag.contains(NBTTags.AGE_TAG)) {
                age = nbt.getInt(NBTTags.AGE_TAG);
            }
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
        // We need to reload the baked model so the default baked model is reset. This packet comes in after getModelData is called on the client side.
        requestModelDataUpdate();
        // If any of the values was changed we request a refresh of our model data and send a block update (this will cause
        // the baked model to be recreated)
        if (oldAge != age || !(oldGenome.equals(this.genome))) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public ICropSpecies getCropSpecies() {
        return cropSpecies;
    }

    public Genome getGenome() {
        return genome;
    }
}