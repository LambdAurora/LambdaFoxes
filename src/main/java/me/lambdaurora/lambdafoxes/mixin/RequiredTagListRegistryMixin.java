/*
 * Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.tag.UhOhRequiredTagListRegistry;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(RequiredTagListRegistry.class)
public class RequiredTagListRegistryMixin {
    @Inject(method = "getRequiredTags", at = @At("RETURN"), cancellable = true)
    private static void addFabricTags(CallbackInfoReturnable<Set<RequiredTagList<?>>> cir) {
        Set<RequiredTagList<?>> set = new HashSet<>(cir.getReturnValue());
        set.addAll(UhOhRequiredTagListRegistry.getFabricRequiredTags());
        cir.setReturnValue(set);
    }
}
