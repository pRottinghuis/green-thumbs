package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.screen.SeedSplicingStationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GreenThumbs.ID);

    public static final RegistryObject<MenuType<SeedSplicingStationMenu>> SEED_SPLICING_STATION_MENU = MENUS.register("seed_splicing_station_menu", () -> IForgeMenuType.create(SeedSplicingStationMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

}
