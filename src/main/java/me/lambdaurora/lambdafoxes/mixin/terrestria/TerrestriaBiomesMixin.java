/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin.terrestria;

import me.lambdaurora.lambdafoxes.mixin.BiomeAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.terraformersmc.terrestria.init.TerrestriaBiomes", remap = false)
public class TerrestriaBiomesMixin
{
    @Shadow
    public static Biome CALDERA_FOOTHILLS;
    @Shadow
    public static Biome DUNES;
    @Shadow
    public static Biome DUNES_EDGE;
    @Shadow
    public static Biome OASIS;

    @Inject(method = "init", at = @At("TAIL"))
    private static void onInit(CallbackInfo ci)
    {
        ((BiomeAccessor) CALDERA_FOOTHILLS).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 5, 2, 4));
        ((BiomeAccessor) DUNES).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 6, 1, 2));
        ((BiomeAccessor) DUNES_EDGE).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 6, 1, 2));
        ((BiomeAccessor) OASIS).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 5, 1, 2));
    }
}
