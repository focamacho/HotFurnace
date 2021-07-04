package com.focamacho.hotfurnace;

import com.focamacho.hotfurnace.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hotfurnace")
public class HotFurnace {

    public static final Logger logger = LogManager.getLogger();

    public HotFurnace() {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
    }

}
