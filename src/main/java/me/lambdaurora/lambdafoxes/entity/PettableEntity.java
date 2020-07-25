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
 * Represents an entity which can be pet.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PettableEntity
{
    /**
     * Returns whether this entity should animate the pet or not.
     *
     * @return True if this entity should animate the pet, else false.
     */
    boolean canAnimatePet();

    /**
     * Returns whether this entity is being pet or not.
     *
     * @return True if this entity is being pet, else false.
     */
    boolean isBeingPet();
}
