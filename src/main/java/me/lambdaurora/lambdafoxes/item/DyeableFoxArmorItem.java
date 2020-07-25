/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.item;

import net.minecraft.item.DyeableItem;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the fox armor item.
 */
public class DyeableFoxArmorItem extends FoxArmorItem implements DyeableItem
{
    public DyeableFoxArmorItem(@NotNull String name, int bonus, boolean fireproof)
    {
        super(name, bonus, fireproof);
    }
}
