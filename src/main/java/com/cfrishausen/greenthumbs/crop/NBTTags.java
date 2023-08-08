package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class NBTTags {
    public static final String INFO_TAG = getPrefix() + "Info";
    public static final String CROP_STATE_TAG = getPrefix() + "CropState";
    public static final String AGE_TAG = BlockStateProperties.AGE_7.getName();
    public static final String FACING_TAG = BlockStateProperties.FACING.getName();
    public static final String CROP_SPECIES_TAG = getPrefix() + "CropSpecies";

    public static final String GENOME_TAG = getPrefix() + "Genome";

    public static String getPrefix() {
        return GreenThumbs.ID + ".";
    }

    public static int getNbtType(Property<?> property) {
        var klass = property.getValueClass();

        if (klass == Integer.class) {
            return Tag.TAG_INT;
        } else if (klass == Boolean.class) {
            return Tag.TAG_BYTE;
        } else if (StringRepresentable.class.isAssignableFrom(klass)) {
            return Tag.TAG_STRING;
        } else {
            throw new IllegalArgumentException("Unknown property type " + property.getValueClass().getName());
        }
    }

    public static <T> Tag encodeNbt(Codec<T> codec, T object) {
        return codec.encodeStart(NbtOps.INSTANCE, object).result().get();
    }

    public static <T> T decodeNbt(Codec<T> codec, Tag json) {
        return codec.parse(NbtOps.INSTANCE, json).result().get();
    }

}
