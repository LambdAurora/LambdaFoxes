/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface ModelPartAccessor
{
    @Accessor("cuboids")
    ObjectList<ModelPart.Cuboid> lambdafoxes_getCuboids();

    @Accessor("children")
    ObjectList<ModelPart> lambdafoxes_getChildren();
}
