package com.focamacho.hotfurnace.config;

import com.focamacho.hotfurnace.HotFurnace;
import com.focamacho.sealconfig.SealConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private static final SealConfig sealConfig = new SealConfig();
    public static HotFurnaceConfig config;

    public static Map<Item, Double> customValues = new HashMap<>();

    public ConfigHandler() {
        config = sealConfig.getConfig(new File(FabricLoader.getInstance().getConfigDir().toString(), "HotFurnace.json5"), HotFurnaceConfig.class);
        if(config.maxPercentage > 100) config.maxPercentage = 100;

        for (Map.Entry<String, Double> entry : config.customValues.entrySet())
            if(entry.getValue() > 100) entry.setValue(100d);

        sealConfig.save();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            customValues.clear();

            for(Map.Entry<String, Double> value : config.customValues.entrySet()) {
                String[] splitItem = value.getKey().split(":");

                if(splitItem.length < 2) {
                    HotFurnace.logger.error(value.getKey() + " is not a valid item.");
                    continue;
                }

                Item item = Registry.ITEM.get(new Identifier(splitItem[0], splitItem[1]));
                if(item == Items.AIR && !value.getKey().startsWith("tag:")) {
                    HotFurnace.logger.error(value.getKey() + " is not a valid item.");
                    continue;
                }

                if(value.getKey().startsWith("tag:")) TagRegistry.item(new Identifier(splitItem[1], splitItem[2])).values().forEach(tagItem -> customValues.put(tagItem, value.getValue()));
                else customValues.put(item, value.getValue());
            }
        });
    }

}
