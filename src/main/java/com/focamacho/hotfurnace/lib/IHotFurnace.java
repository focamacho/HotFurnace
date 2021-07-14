package com.focamacho.hotfurnace.lib;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface IHotFurnace {

    boolean isBurningH();
    int getFuelH();
    int getFuelTimeH(ItemStack stack);
    void setFuelTimeH(int time);
    double getCachePercentageH();
    DefaultedList<ItemStack> getInventoryH();

}
