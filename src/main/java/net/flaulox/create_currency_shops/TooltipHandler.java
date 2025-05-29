package net.flaulox.create_currency_shops;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;


public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        // Check if this item has a value in the currencyValues map
        Integer value = Config.currencyValues.get(item);

        if (value != null) {
            event.getToolTip().add(Component.literal("§7Value: §e" + value));
        }
    }


}
