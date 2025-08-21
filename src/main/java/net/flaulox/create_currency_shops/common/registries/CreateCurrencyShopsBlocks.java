package net.flaulox.create_currency_shops.common.registries;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.flaulox.create_currency_shops.common.blocks.Exchanger;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class CreateCurrencyShopsBlocks {
    private static final CreateCurrencyShopsRegistrate REGISTRATE = CreateCurrencyShops.registrate();

    public static final BlockEntry<Exchanger> EXCHANGER = REGISTRATE.block("exchanger", Exchanger::new)
            .properties(p -> p.requiresCorrectToolForDrops()
                    .mapColor(MapColor.WOOD)
                    .strength(2f,3f)
                    .sound(SoundType.WOOD)
                    .noOcclusion())
            .blockstate((ctx,prov) -> prov.horizontalBlock(ctx.getEntry(),prov.models().getExistingFile(ctx.getId())))
            .item((e,p) -> {
                p.rarity(Rarity.UNCOMMON);
                return new BlockItem(e,p);
            }).build()
            .register();

    public static void register() {}
}
