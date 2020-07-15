package com.focamacho.hotfurnace.mixin;

import com.focamacho.hotfurnace.config.ConfigHolder;
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

    @Shadow protected abstract boolean isBurning();

    @Shadow protected abstract int getFuelTime(ItemStack fuel);

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow private int cookTimeTotal;

    @Shadow protected abstract int getCookTime();

    @Shadow private int cookTime;

    private Item cacheFuelItem;

    @Inject(method = "getCookTime", at = @At("RETURN"), cancellable = true)
    private void getCookTime(CallbackInfoReturnable<Integer> info) {
        if(!this.isBurning() && fuelTime <= 0) {
            this.fuelTime = getFuelTime(this.inventory.get(1));
        }

        int percentage = 0;

        if(ConfigHolder.customValues.containsKey(cacheFuelItem)) {
            percentage = ConfigHolder.customValues.get(cacheFuelItem);
        } else if(this.fuelTime > ConfigHolder.divisorNumber) {
            percentage = this.fuelTime / ConfigHolder.divisorNumber;
        }

        if(percentage > ConfigHolder.maxPercentage) percentage = ConfigHolder.maxPercentage;

        info.setReturnValue((info.getReturnValue() / 100) * (100 - percentage));
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

    @Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true)
    private void getFuelTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        this.cacheFuelItem = stack.getItem();
    }

}
