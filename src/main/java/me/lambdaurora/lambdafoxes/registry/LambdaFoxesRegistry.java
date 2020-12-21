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

package me.lambdaurora.lambdafoxes.registry;

import me.lambdaurora.lambdafoxes.LambdaFoxes;
import me.lambdaurora.lambdafoxes.item.DyeableFoxArmorItem;
import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import me.lambdaurora.lambdafoxes.item.FoxsicleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the mod registry.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class LambdaFoxesRegistry
{
    public static final TrackedData<Integer> FOX_TRUST_LEVEL = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> FOX_PET_STATUS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> FOX_PET_COOLDOWN = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final DyeableFoxArmorItem LEATHER_FOX_ARMOR = register("leather_fox_armor", new DyeableFoxArmorItem("leather", 3, false));
    public static final FoxArmorItem IRON_FOX_ARMOR = register("iron_fox_armor", new FoxArmorItem("iron", 5, false));
    public static final FoxArmorItem GOLDEN_FOX_ARMOR = register("golden_fox_armor", new FoxArmorItem("golden", 7, false));
    public static final FoxArmorItem DIAMOND_FOX_ARMOR = register("diamond_fox_armor", new FoxArmorItem("diamond", 11, false));
    public static final FoxArmorItem NETHERITE_FOX_ARMOR = register("netherite_fox_armor", new FoxArmorItem("netherite", 15, true));

    public static final FoxsicleItem CATSICLE_ITEM = register("catsicle", new FoxsicleItem(new FabricItemSettings().group(ItemGroup.FOOD)));
    public static final FoxsicleItem FOXSICLE_ITEM = register("foxsicle", new FoxsicleItem(new FabricItemSettings().group(ItemGroup.FOOD)));

    public static void init()
    {
        FoxType.fromNumericId(0); // Triggers initialization.
    }

    private static <T extends Item> T register(@NotNull String name, @NotNull T item)
    {
        return Registry.register(Registry.ITEM, LambdaFoxes.id(name), item);
    }
}
