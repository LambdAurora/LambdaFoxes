/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin.traverse;

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
@Mixin(targets = "com.terraformersmc.traverse.biome.AutumnalWoodsBiomes", remap = false)
public class AutumnalWoodsBiomesMixin
{
    @Shadow
    @Final
    static Biome AUTUMNAL_WOODS;

    @Shadow
    @Final
    static Biome AUTUMNAL_WOODED_HILLS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onInit(CallbackInfo ci)
    {
        ((BiomeAccessor) AUTUMNAL_WOODS).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 5, 2, 4));
        ((BiomeAccessor) AUTUMNAL_WOODED_HILLS).lambdafoxes_addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 5, 2, 4));
    }
}
