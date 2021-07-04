package com.focamacho.hotfurnace.mixin;

import com.focamacho.hotfurnace.config.ConfigHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Shadow private int burnTimeTotal;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow protected abstract boolean isBurning();

    @Shadow protected abstract int getBurnTime(ItemStack fuel);

    @Shadow private int cookTimeTotal;

    @Shadow protected abstract int getCookTime();

    @Shadow private int cookTime;

    @Shadow protected NonNullList<ItemStack> items;

    private double cachePercentage;

    @Inject(method = "getCookTime", at = @At("RETURN"), cancellable = true)
    private void getCookTime(CallbackInfoReturnable<Integer> info) {
        if(!this.isBurning() && this.burnTimeTotal <= 0) {
            this.burnTimeTotal = getBurnTime(this.items.get(1));
        }

        info.setReturnValue((int) ((info.getReturnValue() / 100) * (100 - this.cachePercentage)));
    }

    @Inject(method = "setInventorySlotContents", at = @At("HEAD"))
    private void setStack(int slot, ItemStack stack, CallbackInfo info) {
        if(!this.isBurning()) {
            if(slot == 1) {
                this.burnTimeTotal = getBurnTime(stack);
                if(this.cookTime <= 0) this.cookTimeTotal = this.getCookTime();
            }
        }
    }

    @Inject(method = "getBurnTime", at = @At("RETURN"))
    private void getBurnTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        Item fuelItem = stack.getItem();

        if(ConfigHandler.customValues.containsKey(fuelItem)) {
            this.cachePercentage = ConfigHandler.customValues.get(fuelItem);
        } else if(info.getReturnValue() > ConfigHandler.config.divisorNumber) {
            this.cachePercentage = (double) info.getReturnValue() / ConfigHandler.config.divisorNumber;
        }

        if(this.cachePercentage > ConfigHandler.config.maxPercentage) this.cachePercentage = ConfigHandler.config.maxPercentage;
        if(this.cachePercentage >= 99.8d) this.cachePercentage = 99d;
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(BlockState state, CompoundNBT nbt, CallbackInfo ci) {
        this.cachePercentage = nbt.getDouble("cachePercentage");
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(CompoundNBT compound, CallbackInfoReturnable<CompoundNBT> cir) {
        compound.putDouble("cachePercentage", this.cachePercentage);
    }

}
