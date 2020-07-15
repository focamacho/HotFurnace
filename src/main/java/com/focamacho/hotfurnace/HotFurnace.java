package com.focamacho.hotfurnace;

import com.focamacho.hotfurnace.config.ConfigHolder;
import com.focamacho.hotfurnace.config.HotFurnaceConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotFurnace implements ModInitializer {

	public static final Logger logger = LogManager.getLogger();

	@Override
	public void onInitialize() {
		AutoConfig.register(HotFurnaceConfig.class, JanksonConfigSerializer::new);
		ConfigHolder.initConfigs();
		ServerStartCallback.EVENT.register(server -> ConfigHolder.addLateValues());
	}


}
