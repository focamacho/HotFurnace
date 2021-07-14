package com.focamacho.hotfurnace.mixin;

import com.focamacho.hotfurnace.config.ConfigHandler;
import com.focamacho.hotfurnace.lib.IHotFurnace;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends LockableContainerBlockEntity implements IHotFurnace {

    @Shadow int fuelTime;

    @Shadow int cookTimeTotal;

    @Shadow int cookTime;

    @Shadow protected abstract boolean isBurning();

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow protected abstract int getFuelTime(ItemStack fuel);

    @Shadow @Final private RecipeType<? extends AbstractCookingRecipe> recipeType;

    @Shadow
    private static int getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory) {
        return 0;
    }

    private double cachePercentage;

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    @Inject(method = "getCookTime", at = @At("RETURN"), cancellable = true)
    private static void getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory, CallbackInfoReturnable<Integer> info) {
        if(inventory instanceof IHotFurnace furnace) {
            if (!furnace.isBurningH() && furnace.getFuelH() <= 0) {
                furnace.setFuelTimeH(furnace.getFuelTimeH(furnace.getInventoryH().get(1)));
            }

            info.setReturnValue((int) ((info.getReturnValue() / 100) * (100 - furnace.getCachePercentageH())));
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void setStack(int slot, ItemStack stack, CallbackInfo info) {
        if(!this.isBurning()) {
            if(slot == 1) {
                this.fuelTime = getFuelTime(stack);
                if(this.cookTime <= 0 && this.world != null) this.cookTimeTotal = getCookTime(this.world, this.recipeType, this);
            }
        }
    }

    @Inject(method = "getFuelTime", at = @At("RETURN"))
    private void getFuelTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        Item fuelItem = stack.getItem();

        if(ConfigHandler.customValues.containsKey(fuelItem)) {
            this.cachePercentage = ConfigHandler.customValues.get(fuelItem);
        } else if(info.getReturnValue() > ConfigHandler.config.divisorNumber) {
            this.cachePercentage = (double) info.getReturnValue() / ConfigHandler.config.divisorNumber;
        }

        if(this.cachePercentage > ConfigHandler.config.maxPercentage) this.cachePercentage = ConfigHandler.config.maxPercentage;
        if(this.cachePercentage >= 99.8d) this.cachePercentage = 99d;
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void read(NbtCompound nbt, CallbackInfo ci) {
        this.cachePercentage = nbt.getDouble("cachePercentage");
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void write(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putDouble("cachePercentage", this.cachePercentage);
    }


    //IHotFurnace Imp
    @Override
    public boolean isBurningH() {
        return this.isBurning();
    }

    @Override
    public int getFuelH() {
        return this.fuelTime;
    }

    @Override
    public int getFuelTimeH(ItemStack stack) {
        return this.getFuelTime(stack);
    }

    @Override
    public void setFuelTimeH(int time) {
        this.fuelTime = time;
    }

    @Override
    public double getCachePercentageH() {
        return this.cachePercentage;
    }

    @Override
    public DefaultedList<ItemStack> getInventoryH() {
        return this.inventory;
    }

}
