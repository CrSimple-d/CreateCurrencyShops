package net.flaulox.create_currency_shops.common.registries;

import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.AbstractRegistrate;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CreateCurrencyShopsRegistrate extends AbstractRegistrate<CreateCurrencyShopsRegistrate> {
    @Nullable
    protected Function<Item, TooltipModifier> currentTooltipModifierFactory;


    protected CreateCurrencyShopsRegistrate(String modid) {
        super(modid);
    }

    public static CreateCurrencyShopsRegistrate create(String modid) {
        return new CreateCurrencyShopsRegistrate(modid);
    }

    public CreateCurrencyShopsRegistrate setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
        currentTooltipModifierFactory = factory;
        return self();
    }
}
