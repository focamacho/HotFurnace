package com.focamacho.hotfurnace.config;

import com.focamacho.hotfurnace.HotFurnace;
import com.focamacho.sealconfig.SealConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private static final SealConfig sealConfig = new SealConfig();
    public static HotFurnaceConfig config;

    public static Map<Item, Double> customValues = new HashMap<>();

    public ConfigHandler() {
        config = sealConfig.getConfig(new File(FMLPaths.CONFIGDIR.get().toFile(), "HotFurnace.json5"), HotFurnaceConfig.class);
        if(config.maxPercentage > 100) config.maxPercentage = 100;

        for (Map.Entry<String, Double> entry : config.customValues.entrySet())
            if(entry.getValue() > 100) entry.setValue(100d);

        sealConfig.save();
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        customValues.clear();

        for(Map.Entry<String, Double> value : config.customValues.entrySet()) {
            String[] splitItem = value.getKey().split(":");

            if(splitItem.length < 2) {
                HotFurnace.logger.error(value.getKey() + " is not a valid item.");
                continue;
            }

            Item item = Registry.ITEM.getOrDefault(new ResourceLocation(splitItem[0], splitItem[1]));
            if(item == Items.AIR && !value.getKey().startsWith("tag:")) {
                HotFurnace.logger.error(value.getKey() + " is not a valid item.");
                continue;
            }

            if(value.getKey().startsWith("tag:")) {
                ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(splitItem[1], splitItem[2]));
                if(tag != null) tag.getAllElements().forEach(tagItem -> customValues.put(tagItem, value.getValue()));
            } else customValues.put(item, value.getValue());
        }
    }

}
