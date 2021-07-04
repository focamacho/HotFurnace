package com.focamacho.hotfurnace.config;

import com.focamacho.sealconfig.relocated.blue.endless.jankson.Comment;

import java.util.HashMap;
import java.util.Map;

public class HotFurnaceConfig {

    @Comment(value = "The number that will be used as a divisor to perform the speed calculation.\n" +
            "Example: A Lava Bucket has a fuel value of 20000\n" +
            "20000 / 300 = 66.6666...\n" +
            "So a Lava Bucket is 66% faster than the normal furnace speed.")
    public int divisorNumber = 300;

    @Comment(value = "The maximum percentage of additional speed that a fuel can reach.\n" +
            "DO NOT SET THIS HIGHER THAN 100")
    public int maxPercentage = 90;

    @Comment(value = "Custom Fuels Values - Format: \"item\": percentage,\n" +
            "item = The item id. Example: minecraft:coal. To use item tags, start with \"tag:\". Example: tag:minecraft:coals\n" +
            "percentage = How much faster it is compared to the normal furnace. It cannot exceed the value set in 'maxPercentage'")
    public Map<String, Double> customValues = new HashMap<>();
    { customValues.put("minecraft:lava_bucket", 80d); }

}