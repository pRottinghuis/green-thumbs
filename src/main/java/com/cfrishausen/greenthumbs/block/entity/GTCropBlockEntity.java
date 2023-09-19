package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.crop.state.CropState;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GTCropBlockEntity extends BlockEntity implements ICropEntity {

    // See example @ https://www.mcjty.eu/docs/1.18/ep3

    private CropState cropState;

    private ICropSpecies cropSpecies = null;

    public static ModelProperty<CropState> CROP_STATE = new ModelProperty<>();

    private Genome genome;

    public GTCropBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.GT_CROP_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(CROP_STATE, cropState)
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

    public void markUpdated() {
        setChanged();
        // server sends packet to client. client recieves entity data from getUpdateTag
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
            requestModelDataUpdate();
        }
    }

    private void saveStandardNBT(CompoundTag nbt) {
        CompoundTag saveTag = new CompoundTag();
        nbt.put(NBTTags.INFO_TAG, saveTag);
        if (genome != null) {
            saveTag.put(NBTTags.GENOME_TAG, genome.writeTag());
        }
        if (cropSpecies != null) {
            saveTag.putString(NBTTags.CROP_SPECIES_TAG, GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString());
        }
        if (cropState != null) {
            CompoundTag stateTag = new CompoundTag();
            for (Property property : this.cropState.getProperties()) {
                stateTag.put(property.getName(), NBTTags.encodeNbt(property.codec(), cropState.getValue(property)));
            }
            saveTag.put(NBTTags.CROP_STATE_TAG, stateTag);
        }
    }

    private void readStandardNBT(CompoundTag nbt) {
        if (nbt.contains(NBTTags.INFO_TAG)) {
            CompoundTag saveTag = nbt.getCompound(NBTTags.INFO_TAG);
            if (saveTag.contains(NBTTags.GENOME_TAG)) {
                this.genome = new Genome(saveTag.getCompound(NBTTags.GENOME_TAG));
            }
            if (saveTag.contains(NBTTags.CROP_SPECIES_TAG)) {
                this.cropSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValue(new ResourceLocation(saveTag.getString(NBTTags.CROP_SPECIES_TAG)));
            } else {
                // default to wheat species if nbt does not contain
                cropSpecies = GTCropSpecies.GT_WHEAT.get();
            }
            this.cropState = cropSpecies.getDefaultCropState();
            if (saveTag.contains(NBTTags.CROP_STATE_TAG)) {
                CompoundTag stateTag = saveTag.getCompound(NBTTags.CROP_STATE_TAG);
                for (Property property : cropState.getProperties()) {
                    // See how nbt key names are formatted in NBTTags.java
                    String nbtPropertyName = property.getName();
                    // Check that property name exists in nbt and is the correct type
                    if (stateTag.contains(nbtPropertyName)) {
                        cropState = setValue(cropState, property, NBTTags.decodeNbt(property.codec(), stateTag.get(nbtPropertyName)));
                    }
                }
            }
        }
    }

    // Work around to ignore generics that don't exist on NBT but still be able to serialize properties of different types
    public static <T extends Comparable<T>> CropState setValue(CropState oldState, Property<T> property, Object value) {
        return oldState.setValue(property, (T)value);
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
        CropState oldState = getCropState();
        Genome oldGenome = this.genome;
        String oldSpecies = cropSpecies == null ? null : GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString();

        CompoundTag tag = pkt.getTag();
        // This will call loadClientData()
        handleUpdateTag(tag);

        // If any of the values was changed we request a refresh of our model data and send a block update (this will cause
        // the baked model to be recreated)
        String newSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString();
        if (oldState != getCropState() || !(oldGenome.equals(this.genome)) || oldSpecies != newSpecies) {
            markUpdated();
        }
    }

    public ICropSpecies getCropSpecies() {
        if (cropSpecies != null) {
            return cropSpecies;
        }
        GreenThumbs.LOGGER.warn("Entity at {} crop species is null", this.getBlockPos());
        return GTCropSpecies.GT_WHEAT.get();
    }

    public Genome getGenome() {
        if (this.genome != null) {
            return genome;
        }
        GreenThumbs.LOGGER.warn("Entity at {} genome is null", this.getBlockPos());
        return GTCropSpecies.GT_WHEAT.get().defineGenome();
    }

    @Override
    public String toString() {
        String result = "CropBlockEntity at : " + this.getBlockPos() + " ";
        return result + this.getUpdateTag().toString();
    }

    @Override
    public CropState getCropState() {
        if (this.cropState != null) {
            return this.cropState;
        }
        GreenThumbs.LOGGER.warn("Entity at {} cropState is null", this.getBlockPos());
        return GTCropSpecies.GT_WHEAT.get().getDefaultCropState();
    }

    public void setCropState(CropState cropState) {
        this.cropState = cropState;
        requestModelDataUpdate();
        markUpdated();
    }

    // When crop species is reset make sure to set cropState because it will set to the default state from the species. Not all crop species have the same crop state properties
    @Override
    public void setCropSpecies(ICropSpecies cropSpecies) {
        this.cropSpecies = cropSpecies;
        refreshCropState();
        markUpdated();
    }

    @Override
    public void refreshCropState() {
        this.cropState = this.cropSpecies.getDefaultCropState();
    }

    @Override
    public void setGenome(Genome genome) {
        this.genome = genome;
        markUpdated();
    }


}