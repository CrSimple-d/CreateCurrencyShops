package net.flaulox.create_currency_shops.common.registries;


import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.flaulox.create_currency_shops.common.gui.menu.ExchangerMenu;
import net.flaulox.create_currency_shops.common.gui.screen.ExchangerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CreateCurrencyShopsMenus {
    private static final CreateCurrencyShopsRegistrate REGISTRATE = CreateCurrencyShops.registrate();

    public static final MenuEntry<ExchangerMenu> EXCHANGER_MENU = register("exchanger_menu", ExchangerMenu::new,() -> ExchangerScreen::new);

    public static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> MenuEntry<T> register(
            String id, MenuBuilder.ForgeMenuFactory<T> factory, NonNullSupplier<MenuBuilder.ScreenFactory<T,S>> screenFactory) {
        return REGISTRATE.menu(id,factory,screenFactory).register();
    }
    public static void register() {

    }
}
