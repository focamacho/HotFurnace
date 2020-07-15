package com.focamacho.hotfurnace.config;

import com.focamacho.hotfurnace.HotFurnace;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ConfigHolder {

    public static HotFurnaceConfig config;

    public static Map<Item, Integer> customValues = new HashMap<>();
    public static Map<Tag<Item>, Integer> valuesToAddLater = new HashMap<>();
    public static Map<Item, Integer> valuesToChange = new HashMap<>();
    public static Map<Tag<Item>, Integer> valuesToChangeTags = new HashMap<>();

    public static int divisorNumber;
    public static int maxPercentage;

    public static void initConfigs() {
        config = AutoConfig.getConfigHolder(HotFurnaceConfig.class).getConfig();

        divisorNumber = config.divisorNumber;
        maxPercentage = config.maxPercentage;

        String customValuesString = config.customValues;

        String[] split = customValuesString.split(";");

        for(String value : split) {
            String[] splitValue = value.split(",");

            if(splitValue.length == 3) {
                String[] splitItem = splitValue[0].split(":");

                if(splitItem.length < 2) {
                    HotFurnace.logger.error(splitValue[0] + " is not a valid item.");
                    HotFurnace.logger.error("Skipping config: " + value);
                    continue;
                }

                Item item = Registry.ITEM.get(new Identifier(splitItem[0], splitItem[1]));
                String tag = splitValue[0];

                if(item == Items.AIR && !tag.startsWith("tag:")) {
                    HotFurnace.logger.error(splitValue[0] + " is not a valid item.");
                    HotFurnace.logger.error("Skipping config: " + value);
                    continue;
                };

                int percentage;
                int tickTime;

                try { percentage = Integer.parseInt(splitValue[1]); } catch(NumberFormatException e) {
                    HotFurnace.logger.error(splitValue[1] + " is not a valid integer value.");
                    HotFurnace.logger.error("Skipping config: " + value);
                    continue;
                }
                try { tickTime = Integer.parseInt(splitValue[2]); } catch(NumberFormatException e) {
                    HotFurnace.logger.error(splitValue[1] + " is not a valid integer value.");
                    HotFurnace.logger.error("Skipping config: " + value);
                    continue;
                }

                if(tag.startsWith("tag:")) {
                    String[] splitTag = tag.split(":");
                    valuesToAddLater.put(TagRegistry.item(new Identifier(splitTag[1], splitTag[2])), percentage);
                    valuesToChangeTags.put(TagRegistry.item(new Identifier(splitTag[1], splitTag[2])), tickTime);
                } else {
                    customValues.put(item, percentage);
                    valuesToChange.put(item, tickTime);
                }
            } else {
                HotFurnace.logger.error("Invalid String Length!");
                HotFurnace.logger.error("Skipping config: " + value);
                continue;
            }
        }
    }

    public static void changeFuelValues(FuelRegistry fuelRegistry) {
        valuesToChange.forEach((item, tickTime) -> {
            fuelRegistry.remove(item);
            fuelRegistry.add(item, tickTime);
        });
        valuesToChange.clear();

        valuesToChangeTags.forEach((tag, tickTime) -> {
            fuelRegistry.remove(tag);
            fuelRegistry.add(tag, tickTime);
        });
        valuesToChangeTags.clear();
    }

    public static void addLateValues() {
        valuesToAddLater.forEach((tag, percentage) -> {
            tag.values().forEach(item -> {
                customValues.put(item.asItem(), percentage);
            });
        });
        valuesToAddLater.clear();
    }

}
