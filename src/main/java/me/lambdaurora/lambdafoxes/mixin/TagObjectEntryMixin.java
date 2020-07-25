/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a mixin to Tag$ObjectEntry to fix Mojang's stupid tag handling.
 * <p>
 * PLEASE, MAKE SO THAT MINECRAFT JUST DOESN'T GIVE UP ON A TAG JUST BECAUSE ONE OF THE ENTRY WAS NOT FOUND.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
@Mixin(Tag.ObjectEntry.class)
public abstract class TagObjectEntryMixin
{
    @Inject(method = "resolve", at = @At("RETURN"), cancellable = true)
    private <T> void onResolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(true); // Who the fuck at Mojang thought it was a good idea to reject the entire tag if it doesn't find one of its object entry?
    }
}
