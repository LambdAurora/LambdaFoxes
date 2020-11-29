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
     * @return the tail model part
     */
    @NotNull ModelPart getTail();

    /**
     * Resets the tail pivot.
     */
    void resetTailPivot();

    /**
     * Animates the tail of the entity while being pet.
     *
     * @param entity the entity
     * @param tickDelta the tick delta
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
