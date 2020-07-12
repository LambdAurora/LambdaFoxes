/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity;

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
}
