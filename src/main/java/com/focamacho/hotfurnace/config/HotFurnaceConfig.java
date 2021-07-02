package com.focamacho.hotfurnace.config;

import com.focamacho.sealconfig.relocated.blue.endless.jankson.Comment;

public class HotFurnaceConfig {

    @Comment(value = "The number that will be used as a divisor to perform the speed calculation.\n" +
            "Example: A Lava Bucket has a fuel value of 20000\n" +
            "20000 / 300 = 66.6666...\n" +
            "So a Lava Bucket is 66% faster than the normal furnace speed.")
    public int divisorNumber = 300;

    @Comment(value = "The maximum percentage of additional speed that a fuel can reach")
    public int maxPercentage = 90;

    @Comment(value = "Custom Fuels Values\n" +
            "item = The item id. Example: minecraft:coal. To use item tags, start with \"tag:\". Example: tag:minecraft:coals\n" +
            "percentage = How much faster it is compared to the normal furnace. It cannot exceed the value defined in 'maxPercentage'\n" +
            "fuel = The amount of ticks a fuel lasts. Check out https://minecraft.gamepedia.com/Furnace/table if you need examples\n")
    public CustomValue[] customValues = {};

    @SuppressWarnings("unused")
    public static class CustomValue {

        public CustomValue() {}
        public CustomValue(String item, double percentageFaster, int fuelValue) {
            this.item = item;
            this.percentage = percentageFaster;
            this.fuel = fuelValue;
        }

        public String item = "";
        public double percentage = 0;
        public int fuel = 0;

    }

}
