package com.focamacho.hotfurnace;

import com.focamacho.hotfurnace.config.ConfigHandler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotFurnace implements ModInitializer {

	public static final Logger logger = LogManager.getLogger();

	@Override
	@SuppressWarnings("")
	public void onInitialize() {
		new ConfigHandler();
	}

}
