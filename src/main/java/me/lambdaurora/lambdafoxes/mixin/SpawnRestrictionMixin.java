/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SpawnRestriction.class)
public abstract class SpawnRestrictionMixin
{
    @Shadow
    @Final
    private static Map<EntityType<?>, ?> RESTRICTIONS;

    @Shadow
    private static <T extends MobEntity> void register(EntityType<T> type, SpawnRestriction.Location location, Heightmap.Type heightmapType, SpawnRestriction.SpawnPredicate<T> predicate)
    {
        throw new IllegalStateException("Dummy method body invoked. A critical mixin failure has occurred.");
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onInit(CallbackInfo ci)
    {
        RESTRICTIONS.remove(EntityType.FOX);
        register(EntityType.FOX, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LambdaFoxEntity::canSpawn);
    }
}
