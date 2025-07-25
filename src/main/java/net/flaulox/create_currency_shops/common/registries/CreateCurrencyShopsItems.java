package net.flaulox.create_currency_shops.common.registries;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.flaulox.create_currency_shops.common.items.CoinItem;
import net.flaulox.create_currency_shops.common.items.CurrencyItem;

public class CreateCurrencyShopsItems {
    private static final CreateCurrencyShopsRegistrate REGISTRATE = CreateCurrencyShops.registrate();

    public static final ItemEntry<CurrencyItem> CURRENCY_ITEM =
            REGISTRATE.item("currency_item", CurrencyItem::new)
                    .lang("Coins")
                    .register();

    public static final ItemEntry<CoinItem> BRASS_COIN =
            REGISTRATE.item("brass_coin", CoinItem::new)
                    .lang("Brass Coin")
                    .register();

    public static final ItemEntry<CoinItem> COPPER_COIN =
            REGISTRATE.item("copper_coin", CoinItem::new)
                    .lang("Copper Coin")
                    .register();

    public static final ItemEntry<CoinItem> GOLD_COIN =
            REGISTRATE.item("gold_coin", CoinItem::new)
                    .lang("Gold Coin")
                    .register();

    public static final ItemEntry<CoinItem> IRON_COIN =
            REGISTRATE.item("iron_coin", CoinItem::new)
                    .lang("Iron Coin")
                    .register();

    public static final ItemEntry<CoinItem> NETHERITE_COIN =
            REGISTRATE.item("netherite_coin", CoinItem::new)
                    .lang("Netherite Coin")
                    .register();

    public static final ItemEntry<CoinItem> ZINC_COIN =
            REGISTRATE.item("zinc_coin", CoinItem::new)
                    .lang("Zinc Coin")
                    .register();




    public static void register() {}
}
