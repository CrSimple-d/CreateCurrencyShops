package net.flaulox.create_currency_shops.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CurrencyItem extends Item {

    public CurrencyItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("This Item is a Placeholder and has no functionality. It is not supposed to be acquired in survival!").withStyle(ChatFormatting.GRAY));
    }
}
