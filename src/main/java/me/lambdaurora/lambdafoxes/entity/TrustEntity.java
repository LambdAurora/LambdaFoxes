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
public interface TrustEntity {
    /**
     * Returns the trust level of this entity.
     *
     * @return the trust level of this entity
     */
    int getTrustLevel();

    /**
     * Sets the trust level of this entity.
     *
     * @param trustLevel trust level of this entity
     */
    void setTrustLevel(int trustLevel);

    /**
     * Returns the maximum trust level of this entity.
     *
     * @return the maximum trust level
     */
    int getMaxTrustLevel();

    /**
     * Returns whether this entity is wild or not.
     *
     * @return {@code true} if this entity is wild, else {@code false}
     */
    default boolean isWild() {
        return this.getTrustLevel() <= 1;
    }

    /**
     * Returns whether this entity is tamed or not.
     *
     * @return {@code true} if this entity is tamed, else {@code false}
     */
    default boolean isTamed() {
        return this.getTrustLevel() >= this.getMaxTrustLevel();
    }

    void setWaiting(boolean waiting);

    boolean isWaiting();

    boolean canAttackWithOwner(@NotNull LivingEntity target, @NotNull LivingEntity owner);

    /**
     * Returns the owner UUID.
     *
     * @return the owner UUID
     */
    @NotNull Optional<UUID> getOwnerUuid();

    /**
     * Returns the owner.
     *
     * @return the owner
     */
    @NotNull Optional<LivingEntity> getOwner();

    /**
     * Returns whether the specified entity is this entity's owner or not.
     *
     * @param entity the entity to check
     * @return {@code true} if the specified entity is the owner, else {@code false}
     */
    default boolean isOwner(@NotNull LivingEntity entity) {
        Optional<UUID> owner = this.getOwnerUuid();
        return owner.isPresent() && owner.get().equals(entity.getUuid());
    }
}
