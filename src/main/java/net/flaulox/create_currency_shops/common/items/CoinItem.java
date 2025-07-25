package net.flaulox.create_currency_shops.common.items;

import net.minecraft.world.item.Item;

public class CoinItem extends Item {
    public CoinItem(Properties properties) {
        super(properties.stacksTo(99));
    }
}
