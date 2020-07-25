/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity;

import me.lambdaurora.lambdafoxes.registry.FoxType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Represents the enhanced fox entity.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LambdaFoxEntity extends TrustEntity, PettableEntity
{
    /**
     * Returns the type of this fox.
     *
     * @return The type of the fox.
     */
    FoxType getFoxType();

    /**
     * Sets the type of this fox.
     *
     * @param type The type of the fox.
     */
    void setFoxType(@NotNull FoxType type);

    /**
     * Sets whether this fox is sleeping.
     *
     * @param sleeping True if the fox is sleeping, else false.
     */
    void setFoxSleeping(boolean sleeping);

    /**
     * Sets whether this fox is aggressive.
     *
     * @param aggressive True if the fox is aggressive, else false.
     */
    void setFoxAggressive(boolean aggressive);

    /**
     * Returns this fox's armor item.
     *
     * @return This fox's armor item.
     */
    @NotNull ItemStack getFoxArmor();

    void setFoxArmor(@NotNull ItemStack stack);

    /**
     * Stops this fox actions.
     */
    void stopFoxActions();

    /**
     * Returns the tail angle of this fox.
     *
     * @return The tail angle of the fox.
     */
    @Environment(EnvType.CLIENT)
    float getTailAngle();

    /**
     * Returns the appreciation of this fox.
     *
     * @return The appreciation of the fox.
     */
    @Environment(EnvType.SERVER)
    float getAppreciation();

    /**
     * Sets the appreciation of this fox.
     *
     * @param appreciation The appreciation of the fox.
     */
    @Environment(EnvType.SERVER)
    void setAppreciation(float appreciation);

    static boolean canSpawn(EntityType<FoxEntity> fox, WorldAccess world, SpawnReason reason, BlockPos pos, Random random)
    {
        BlockState state = world.getBlockState(pos.down());
        return (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.SNOW) || state.isOf(Blocks.SAND)) && world.getBaseLightLevel(pos, 0) > 8;
    }
}
