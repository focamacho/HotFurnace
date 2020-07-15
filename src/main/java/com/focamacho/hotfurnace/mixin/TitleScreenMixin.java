package com.focamacho.hotfurnace.mixin;

import com.focamacho.hotfurnace.config.ConfigHolder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        ConfigHolder.changeFuelValues(FuelRegistry.INSTANCE);
    }

}
