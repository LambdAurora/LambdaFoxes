/*
 * Copyright (c) 2021 LambdAurora <aurora42lambda@gmail.com>
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

package dev.lambdaurora.lambdafoxes.item;

import dev.lambdaurora.lambdafoxes.LambdaFoxes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the fox armor item.
 */
public class FoxArmorItem extends Item {
    public final int bonus;
    public final Identifier entityTexture;

    public FoxArmorItem(@NotNull String name, int bonus, boolean fireproof) {
        super(fireproof(new Item.Settings().maxCount(1).group(ItemGroup.MISC), fireproof));
        this.bonus = bonus;
        this.entityTexture = LambdaFoxes.id("textures/entity/fox/armor/" + name + "_fox_armor.png");
    }

    public int getBonus(@NotNull ItemStack stack) {
        return this.bonus;
    }

    private static Settings fireproof(Settings settings, boolean fireproof) {
        return fireproof ? settings.fireproof() : settings;
    }
}
