/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.item;

import me.lambdaurora.lambdafoxes.LambdaFoxes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the fox armor item.
 */
public class FoxArmorItem extends Item
{
    public final int        bonus;
    public final Identifier entityTexture;

    public FoxArmorItem(@NotNull String name, int bonus, boolean fireproof)
    {
        super(fireproof(new Item.Settings().maxCount(1).group(ItemGroup.MISC), fireproof));
        this.bonus = bonus;
        this.entityTexture = LambdaFoxes.mc("textures/entity/fox/armor/" + name + "_fox_armor.png");
    }

    public int getBonus(@NotNull ItemStack stack) {
        return this.bonus;
    }

    private static Settings fireproof(Settings settings, boolean fireproof)
    {
        return fireproof ? settings.fireproof() : settings;
    }
}
