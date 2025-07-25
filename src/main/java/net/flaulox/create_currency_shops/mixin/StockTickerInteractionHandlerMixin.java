package net.flaulox.create_currency_shops.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.flaulox.create_currency_shops.common.registries.CreateCurrencyShopsItems;
import net.flaulox.create_currency_shops.CurrencyValues;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;



@Mixin(StockTickerInteractionHandler.class)
public abstract class StockTickerInteractionHandlerMixin {
    private static final ThreadLocal<Integer> DIFFERENCE = new ThreadLocal<>();

    @Inject(
            method = "interactWithShop",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/packager/InventorySummary;getStacksByCount()Ljava/util/List;",
                    ordinal = 1
            )
    )
    private static void modifyPaymentEntries(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem, CallbackInfo ci, @Local Couple bakeEntries, @Local(ordinal = 0) InventorySummary paymentEntries, @Local(ordinal = 0) InventorySummary orderEntries, @Local PackageOrder order) {
        int requiredValue = 0;
        for (BigItemStack stack : paymentEntries.getStacks()) {
            if (stack.stack.is(CreateCurrencyShopsItems.CURRENCY_ITEM)) {
                requiredValue += stack.count;
            }
        }
        int availableValue = CurrencyValues.countValueInInventory(player.getInventory());

        if (availableValue >= requiredValue) {
            for (BigItemStack stack : paymentEntries.getStacks()) {
                if (stack.stack.is(CreateCurrencyShopsItems.CURRENCY_ITEM)) {
                    paymentEntries.erase(stack.stack);
                }
            }



            Pair<InventorySummary, Integer> pair = CurrencyValues.processInventoryPayment(player, requiredValue);
            paymentEntries.add(pair.getFirst());
            DIFFERENCE.set(pair.getSecond());

        }
    }


    @Inject(
            method = "interactWithShop",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/stockTicker/StockTickerBlockEntity;broadcastPackageRequest(Lcom/simibubi/create/content/logistics/packagerLink/LogisticallyLinkedBehaviour$RequestType;Lcom/simibubi/create/content/logistics/stockTicker/PackageOrder;Lcom/simibubi/create/content/logistics/packager/IdentifiedInventory;Ljava/lang/String;)Z"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void processChange(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem, CallbackInfo ci, StockTickerBlockEntity tickerBE, ShoppingListItem.ShoppingList list, Couple bakeEntries, InventorySummary paymentEntries, InventorySummary orderEntries, PackageOrder order) {
        CurrencyValues.processChange(player, DIFFERENCE.get());
        DIFFERENCE.remove();
    }


}


