package net.flaulox.create_currency_shops;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.*;
import java.util.stream.Collectors;

public class CurrencyValues {
    public static final Map<Item, Integer> COIN_VALUES = Map.of(
            Items.GOLD_INGOT, 10,
            Items.IRON_INGOT, 5,
            Items.COPPER_INGOT, 1
    );

    public static int getValue(InventorySummary paymentEntries) {
        int result = 0;
        for (BigItemStack stack : paymentEntries.getStacks()) {
            result += COIN_VALUES.getOrDefault(stack.stack.getItem(), 0) * stack.count;
        }
        System.out.println(result);
        return result;
    }

    public static int getValue(ItemStack stack) {
        return COIN_VALUES.getOrDefault(stack.getItem(), -1);
    }

    public static int countValueInInventory(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty())
                continue;

            int valuePerItem = getValue(stack);
            if (valuePerItem > 0) {
                total += valuePerItem * stack.getCount();
            }
        }
        return total;
    }

    public static List<Map.Entry<Item, Integer>> sortCoinValues() {
        return  COIN_VALUES.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
    }

    public static Pair<InventorySummary, Integer> processInventoryPayment(Player player, int targetValue) {
        InventorySummary paymentEntries = new InventorySummary();
        Inventory inventory = player.getInventory();

        // create coinsAvailable List from Coins in players Inventory
        List<Map.Entry<Item, Integer>> coinsAvailable = new ArrayList<>();
        for (Map.Entry<Item, Integer> coinValue : sortCoinValues()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() == coinValue.getKey()) {
                    for (int j = 0; j < stack.getCount(); j++) {
                        coinsAvailable.add(coinValue);
                    }
                }
            }
        }

        // move forward in coinsAvailable until iterated Value is greater than targetValue
        int iteratedValue = 0;
        int index = -1;
        while (iteratedValue < targetValue) {
            index++;
            iteratedValue += coinsAvailable.get(index).getValue();
        }

        // move backwards in coinsAvailable from index
        iteratedValue = 0;
        while (iteratedValue < targetValue) {
            iteratedValue += coinsAvailable.get(index).getValue();
            paymentEntries.add(new ItemStack(coinsAvailable.get(index).getKey(), 1));
            index--;
        }
        int difference = iteratedValue - targetValue;

        return Pair.of(paymentEntries, difference);
    }

    //places Change directly into Players Inventory
    public static void processChange(Inventory inventory, int amount) {
        Item lowestDenomination = sortCoinValues().getFirst().getKey();
        while (amount > 0) {
            boolean changeAdded = false;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() == lowestDenomination && stack.getCount() < inventory.getMaxStackSize()) {
                    stack.setCount(stack.getCount() + 1);
                    changeAdded = true;
                    amount--;
                    break;
                }
            }
            if (!changeAdded) {
                inventory.add(new ItemStack(lowestDenomination));
                amount--;
            }
        }
    }

    public static boolean removeStackFromPaymentEntries(BigItemStack stack) {
        return COIN_VALUES.containsKey(stack.stack.getItem());
    }


}
