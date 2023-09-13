package com.cfrishausen.greenthumbs.integration;

import com.cfrishausen.greenthumbs.GreenThumbs;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIGreenThumbsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GreenThumbs.ID, "jei_plugin");
    }
}
