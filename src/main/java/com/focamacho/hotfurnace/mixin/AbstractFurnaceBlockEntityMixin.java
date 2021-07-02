package com.focamacho.hotfurnace.mixin;

import com.focamacho.hotfurnace.config.ConfigHandler;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Shadow private int fuelTime;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow protected abstract boolean isBurning();

    @Shadow protected abstract int getFuelTime(ItemStack fuel);

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow private int cookTimeTotal;

    @Shadow protected abstract int getCookTime();

    @Shadow private int cookTime;

    private double cachePercentage;

    @Inject(method = "getCookTime", at = @At("RETURN"), cancellable = true)
    private void getCookTime(CallbackInfoReturnable<Integer> info) {
        if(!this.isBurning() && this.fuelTime <= 0) {
            this.fuelTime = getFuelTime(this.inventory.get(1));
        }

        info.setReturnValue((int) ((info.getReturnValue() / 100) * (100 - this.cachePercentage)));
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void setStack(int slot, ItemStack stack, CallbackInfo info) {
        if(!this.isBurning()) {
            if(slot == 1) {
                this.fuelTime = getFuelTime(stack);
                if(this.cookTime <= 0) this.cookTimeTotal = this.getCookTime();
            }
        }
    }

    @Inject(method = "getFuelTime", at = @At("RETURN"), cancellable = true)
    private void getFuelTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        Item fuelItem = stack.getItem();

        if(ConfigHandler.customValues.containsKey(fuelItem)) {
            this.cachePercentage = ConfigHandler.customValues.get(fuelItem);
        } else if(info.getReturnValue() > ConfigHandler.config.divisorNumber) {
            this.cachePercentage = (double) info.getReturnValue() / ConfigHandler.config.divisorNumber;
        }

        if(this.cachePercentage > ConfigHandler.config.maxPercentage) this.cachePercentage = ConfigHandler.config.maxPercentage;
    }

}
