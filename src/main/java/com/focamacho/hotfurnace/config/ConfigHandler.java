package com.focamacho.hotfurnace.config;

import com.focamacho.hotfurnace.HotFurnace;
import com.focamacho.sealconfig.SealConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private static final SealConfig sealConfig = new SealConfig();
    public static HotFurnaceConfig config;

    public static Map<Item, Double> customValues = new HashMap<>();

    public void initConfigs() {
        config = sealConfig.getConfig(new File(FabricLoader.getInstance().getConfigDir().toString(), "HotFurnace.json"), HotFurnaceConfig.class);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for(HotFurnaceConfig.CustomValue value : config.customValues) {
                String[] splitItem = value.item.split(":");

                if(splitItem.length < 2) {
                    HotFurnace.logger.error(value.item + " is not a valid item.");
                    continue;
                }

                Item item = Registry.ITEM.get(new Identifier(splitItem[0], splitItem[1]));
                if(item == Items.AIR && !value.item.startsWith("tag:")) {
                    HotFurnace.logger.error(value.item + " is not a valid item.");
                    continue;
                }

                if(value.item.startsWith("tag:")) changeFuelValue(TagRegistry.item(new Identifier(splitItem[1], splitItem[2])), value.percentage, value.fuel);
                else changeFuelValue(item, value.percentage, value.fuel);
            }
        });
    }

    public void changeFuelValue(Item item, double percentageFaster, int fuelValue) {
        customValues.put(item, percentageFaster);

        FuelRegistry fuelRegistry = FuelRegistry.INSTANCE;
        fuelRegistry.remove(item);
        fuelRegistry.add(item, fuelValue);
    }

    public void changeFuelValue(Tag<Item> tag, double percentageFaster, int fuelValue) {
        tag.values().forEach(item -> customValues.put(item, percentageFaster));

        FuelRegistry fuelRegistry = FuelRegistry.INSTANCE;
        fuelRegistry.remove(tag);
        fuelRegistry.add(tag, fuelValue);
    }

}
