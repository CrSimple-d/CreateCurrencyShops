package net.flaulox.create_currency_shops;

import com.simibubi.create.content.logistics.packager.InventorySummary;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.*;
import java.util.stream.Collectors;

public class CurrencyValues {

    public static final Map<Item, Integer> COIN_VALUES = Config.currencyValues;




    public static int getValue(ItemStack stack) {
        return COIN_VALUES.getOrDefault(stack.getItem(), -1);
    }

    public static int countValueInInventory(Inventory inventory) {
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
    public static void processChange(Player player, int totalAmount) {
        Inventory inventory = player.getInventory();
        List<Map.Entry<Item, Integer>> sortedCoinValues = sortCoinValues();
//        Item lowestDenomination = sortedCoinValues.getFirst().getKey();
        int index = sortedCoinValues.size() - 1;
        while (totalAmount > 0 && index >= 0) {


            int amount = totalAmount;
            for (int i = 0; i < amount / sortedCoinValues.get(index).getValue(); i++) {
                boolean changeAdded = false;
                for (int j = 0; j < inventory.getContainerSize(); j++) {
                    ItemStack stack = inventory.getItem(j);
                    if (stack.getItem() == sortedCoinValues.get(index).getKey() && stack.getCount() < stack.getMaxStackSize()) {
                        stack.setCount(stack.getCount() + 1);
                        changeAdded = true;
                        totalAmount -= sortedCoinValues.get(index).getValue();
                        break;
                    }
                }
                if (!changeAdded) {
                    if (!inventory.add(new ItemStack(sortedCoinValues.get(index).getKey()))) {
                        if (!player.level().isClientSide) {
                            player.level().addFreshEntity(new ItemEntity(
                                    player.level(),
                                    player.getX(),
                                    player.getY() + 1,
                                    player.getZ(),
                                    new ItemStack(sortedCoinValues.get(index).getKey())
                            ));
                        }
                    }
                    totalAmount -= sortedCoinValues.get(index).getValue();
                }
            }


            index--;

        }
    }



    public static boolean isValidCurrency(ItemStack stack) {
        return COIN_VALUES.containsKey(stack.getItem());
    }


}
