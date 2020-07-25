/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.render;

import me.lambdaurora.lambdafoxes.entity.PettableEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the model of an entity which can be pet.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PettableEntityModel
{
    /**
     * Returns the tail model part.
     *
     * @return The tail model part.
     */
    @NotNull ModelPart getTail();

    /**
     * Resets the tail pivot.
     */
    void resetTailPivot();

    /**
     * Animates the tail of the entity while being pet.
     *
     * @param entity    The entity.
     * @param tickDelta The tick delta.
     */
    default void animateTailWhilePet(PettableEntity entity, float tickDelta)
    {
        if (entity.canAnimatePet()) {
            final double tailAnimation = Math.sin((((Entity) entity).getEntityId() + ((Entity) entity).age + tickDelta) * 0.5) * 10.0;
            this.getTail().roll = (float) Math.toRadians(tailAnimation);
            this.resetTailPivot();
        } else {
            this.getTail().roll = 0;
        }
    }
}
