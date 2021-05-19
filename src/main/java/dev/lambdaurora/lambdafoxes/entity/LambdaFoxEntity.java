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

package dev.lambdaurora.lambdafoxes.entity;

import dev.lambdaurora.lambdafoxes.registry.FoxType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public interface LambdaFoxEntity extends TrustEntity, PettableEntity {
    /**
     * Returns the type of this fox.
     *
     * @return the type of the fox
     */
    FoxType getFoxType();

    /**
     * Sets the type of this fox.
     *
     * @param type the type of the fox
     */
    void setFoxType(@NotNull FoxType type);

    /**
     * Sets whether this fox is sleeping.
     *
     * @param sleeping {@code true} if the fox is sleeping, else {@code false}
     */
    void setFoxSleeping(boolean sleeping);

    /**
     * Sets whether this fox is aggressive.
     *
     * @param aggressive {@code true} if the fox is aggressive, else {@code false}
     */
    void setFoxAggressive(boolean aggressive);

    /**
     * Returns this fox's armor item.
     *
     * @return this fox's armor item
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
     * @return the tail angle of the fox
     */
    @Environment(EnvType.CLIENT)
    float getTailAngle();

    /**
     * Returns the appreciation of this fox.
     *
     * @return the appreciation of the fox
     */
    float getAppreciation();

    /**
     * Sets the appreciation of this fox.
     *
     * @param appreciation the appreciation of the fox
     */
    void setAppreciation(float appreciation);

    static boolean canSpawn(EntityType<FoxEntity> fox, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        var state = world.getBlockState(pos.down());
        return (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.SNOW) || state.isOf(Blocks.SAND))
                && world.getBaseLightLevel(pos, 0) > 8;
    }
}
