package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thedarkcolour.modkit.ModKit;
import thedarkcolour.modkit.data.DataHelper;
import thedarkcolour.modkit.data.MKBlockModelProvider;
import thedarkcolour.modkit.data.MKEnglishProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var output = gen.getPackOutput();
        var lookup = event.getLookupProvider();
        var helper = event.getExistingFileHelper();

        gen.addProvider(true, new GTRecipes(output));
        gen.addProvider(true, new GTItemModels(output, GreenThumbs.ID, helper));
        gen.addProvider(true, new GTLanguageProvider(output, GreenThumbs.ID, "en_us"));

        var dataHelper = new DataHelper(GreenThumbs.ID, event);

    }

}
