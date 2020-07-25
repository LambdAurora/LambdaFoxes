/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.registry;

import me.lambdaurora.lambdafoxes.LambdaFoxes;
import me.lambdaurora.lambdafoxes.item.DyeableFoxArmorItem;
import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.Item;
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
    public static final TrackedData<Integer> FOX_TRUST_LEVEL  = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> FOX_PET_STATUS   = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> FOX_PET_COOLDOWN = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final DyeableFoxArmorItem LEATHER_FOX_ARMOR   = register("leather_fox_armor", new DyeableFoxArmorItem("leather", 3, false));
    public static final FoxArmorItem        IRON_FOX_ARMOR      = register("iron_fox_armor", new FoxArmorItem("iron", 5, false));
    public static final FoxArmorItem        GOLDEN_FOX_ARMOR    = register("golden_fox_armor", new FoxArmorItem("golden", 7, false));
    public static final FoxArmorItem        DIAMOND_FOX_ARMOR   = register("diamond_fox_armor", new FoxArmorItem("diamond", 11, false));
    public static final FoxArmorItem        NETHERITE_FOX_ARMOR = register("netherite_fox_armor", new FoxArmorItem("netherite", 15, true));

    public static void init()
    {
        FoxType.fromNumericId(0); // Triggers initialization.
    }

    private static <T extends Item> T register(@NotNull String name, @NotNull T item)
    {
        return Registry.register(Registry.ITEM, LambdaFoxes.mc(name), item);
    }
}
