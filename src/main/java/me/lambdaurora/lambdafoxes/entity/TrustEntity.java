/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents an entity which has a trust level.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TrustEntity
{
    /**
     * Returns the trust level of this entity.
     *
     * @return The trust level of this entity.
     */
    int getTrustLevel();

    /**
     * Sets the trust level of this entity.
     *
     * @param trustLevel Trust level of this entity.
     */
    void setTrustLevel(int trustLevel);

    /**
     * Returns the maximum trust level of this entity.
     *
     * @return The maximum trust level.
     */
    int getMaxTrustLevel();

    /**
     * Returns whether this entity is wild or not.
     *
     * @return True if this entity is wild, else false.
     */
    default boolean isWild()
    {
        return this.getTrustLevel() <= 1;
    }

    /**
     * Returns whether this entity is tamed or not.
     *
     * @return True if this entity is tamed, else false.
     */
    default boolean isTamed()
    {
        return this.getTrustLevel() >= this.getMaxTrustLevel();
    }

    void setWaiting(boolean waiting);

    boolean isWaiting();

    boolean canAttackWithOwner(@NotNull LivingEntity target, @NotNull LivingEntity owner);

    /**
     * Returns the owner UUID.
     *
     * @return The owner UUID.
     */
    @NotNull Optional<UUID> getOwnerUuid();

    /**
     * Returns the owner.
     *
     * @return The owner.
     */
    @NotNull Optional<LivingEntity> getOwner();

    /**
     * Returns whether the specified entity is this entity's owner or not.
     *
     * @param entity The entity to check.
     * @return True if the specified entity is the owner, else false.
     */
    default boolean isOwner(@NotNull LivingEntity entity)
    {
        Optional<UUID> owner = this.getOwnerUuid();
        return owner.isPresent() && owner.get().equals(entity.getUuid());
    }
}
