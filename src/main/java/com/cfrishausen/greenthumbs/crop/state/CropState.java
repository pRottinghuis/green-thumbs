package com.cfrishausen.greenthumbs.crop.state;

import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class CropState extends StateHolder<ICropSpecies, CropState> {
    public CropState(ICropSpecies pOwner, ImmutableMap<Property<?>, Comparable<?>> pValues, MapCodec<CropState> pPropertiesCodec) {
        super(pOwner, pValues, pPropertiesCodec);
    }

    public static CompoundTag getDefaultStateTag(ICropSpecies cropSpecies) {
        CompoundTag stateTag = new CompoundTag();
        CropState speciesDefaultState = cropSpecies.getDefaultCropState();
        for (Property property : speciesDefaultState.getProperties()) {
            stateTag.put(property.getName(), NBTTags.encodeNbt(property.codec(), speciesDefaultState.getValue(property)));
        }
        return stateTag;
    }
}
