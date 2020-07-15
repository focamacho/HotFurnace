package com.focamacho.hotfurnace.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "hotfurnace")
public class HotFurnaceConfig implements ConfigData {

    @Comment(value = "The number that will be used as a divisor to perform the speed calculation.\n" +
            "Example: A Lava Bucket has a fuel value of 20000\n" +
            "20000 / 300 = 66.6666...\n" +
            "So a Lava Bucket is 66% faster than the normal furnace speed.")
    int divisorNumber = 300;

    @Comment(value = "The maximum percentage of additional speed that a fuel can reach")
    int maxPercentage = 90;

    @Comment(value = "YOU CANNOT USE ITEMS THAT ARE NOT FUEL HERE! YOU CAN ONLY EDIT EXISTING FUEL VALUES\n" +
            "Syntax: itemID,percentageFaster,fuelValue\n" +
            "itemID = The item id. Example: minecraft:coal. To use item tags, start with \"tag:\". Example: tag:minecraft:coals\n" +
            "percentageFaster = How much faster it is compared to the normal furnace. It cannot exceed the value defined in 'maxPercentage'\n" +
            "fuelValue = The amount of ticks a fuel lasts. Check out https://minecraft.gamepedia.com/Furnace/table if you need examples\n" +
            "To separate the items use ';'\n" +
            "Example: \"tag:minecraft:coals,50,10000;minecraft:lava_bucket,90,5000\"")
    String customValues = "";
}
