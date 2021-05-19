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

package dev.lambdaurora.lambdafoxes.entity.ai.goal;

import dev.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class FoxSitGoal extends Goal {
    private final FoxEntity fox;
    private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;

    public FoxSitGoal(@NotNull FoxEntity fox) {
        this.fox = fox;
        this.WORRIABLE_ENTITY_PREDICATE = TargetPredicate.method_36625()
                .setBaseMaxDistance(12.0D)
                .method_36627()
                .setPredicate(fox.new WorriableEntityFilter());
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean shouldContinue() {
        return ((LambdaFoxEntity) this.fox).isWaiting();
    }

    @Override
    public boolean canStart() {
        if (!((LambdaFoxEntity) this.fox).isTamed())
            return false;
        else if (this.fox.isInsideWaterOrBubbleColumn())
            return false;
        else if (!this.fox.isOnGround())
            return false;
        else if (this.cannotCalmDown())
            return false;
        else {
            return ((LambdaFoxEntity) this.fox).getOwner()
                    .map(livingEntity -> (!(this.fox.squaredDistanceTo(livingEntity) < 144.0D) || livingEntity.getAttacker() == null)
                            && ((LambdaFoxEntity) this.fox).isWaiting())
                    .orElse(true);
        }
    }

    @Override
    public void tick() {
        this.updateWaitPosition();
    }

    @Override
    public void start() {
        ((LambdaFoxEntity) this.fox).stopFoxActions();

        this.updateWaitPosition();
        this.fox.getNavigation().stop();
        this.fox.forwardSpeed = 0.0f;
    }

    @Override
    public void stop() {
        ((LambdaFoxEntity) this.fox).stopFoxActions();
    }

    protected void updateWaitPosition() {
        boolean sleep = this.fox.getEntityWorld().isDay() && this.isAtFavoredLocation();
        this.fox.setSitting(!sleep);
        ((LambdaFoxEntity) this.fox).setFoxSleeping(sleep);
    }

    protected boolean isAtFavoredLocation() {
        var pos = new BlockPos(this.fox.getX(), this.fox.getBoundingBox().maxY, this.fox.getZ());
        return !this.fox.getEntityWorld().isSkyVisible(pos) && this.fox.getPathfindingFavor(pos) >= 0.0F;
    }

    protected boolean cannotCalmDown() {
        return !this.fox.world.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, this.fox,
                this.fox.getBoundingBox().expand(12.0D, 6.0D, 12.0D)).isEmpty();
    }
}
