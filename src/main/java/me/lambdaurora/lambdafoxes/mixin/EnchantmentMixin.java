/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import net.minecraft.enchantment.BindingCurseEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Enchantment.class})
public class EnchantmentMixin
{
    @Inject(method = "isAcceptableItem", at = @At(value = "RETURN", shift = At.Shift.AFTER))
    private void onIsAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof FoxArmorItem) {
            if (((Object) this) instanceof ProtectionEnchantment || ((Object) this) instanceof BindingCurseEnchantment || ((Object) this) instanceof ThornsEnchantment) {
                cir.setReturnValue(true);
            }
        }
    }
}
