/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity.ai.goal;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

public class FoxSitGoal extends Goal
{
    private final FoxEntity       fox;
    private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;
    private       boolean         sleep = false;

    public FoxSitGoal(@NotNull FoxEntity fox)
    {
        this.fox = fox;
        this.WORRIABLE_ENTITY_PREDICATE = (new TargetPredicate()).setBaseMaxDistance(12.0D).includeHidden().setPredicate(fox.new WorriableEntityFilter());
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean shouldContinue()
    {
        return ((LambdaFoxEntity) this.fox).isWaiting();
    }

    @Override
    public boolean canStart()
    {
        if (!((LambdaFoxEntity) this.fox).isTamed())
            return false;
        else if (this.fox.isInsideWaterOrBubbleColumn())
            return false;
        else if (!this.fox.isOnGround())
            return false;
        else if (this.cannotCalmDown())
            return false;
        else {
            Optional<LivingEntity> owner = ((LambdaFoxEntity) this.fox).getOwner();
            return owner.map(livingEntity -> (!(this.fox.squaredDistanceTo(livingEntity) < 144.0D) || livingEntity.getAttacker() == null) && ((LambdaFoxEntity) this.fox).isWaiting())
                    .orElse(true);
        }
    }

    @Override
    public void tick()
    {
        this.updateWaitPosition();
    }

    @Override
    public void start()
    {
        ((LambdaFoxEntity) this.fox).stopFoxActions();

        this.updateWaitPosition();
        this.fox.getNavigation().stop();
        this.fox.forwardSpeed = 0.0f;
    }

    @Override
    public void stop()
    {
        ((LambdaFoxEntity) this.fox).stopFoxActions();
    }

    protected void updateWaitPosition()
    {
        this.sleep = this.fox.world.isDay() && this.isAtFavoredLocation();
        this.fox.setSitting(!this.sleep);
        ((LambdaFoxEntity) this.fox).setFoxSleeping(this.sleep);
    }

    protected boolean isAtFavoredLocation()
    {
        BlockPos blockPos = new BlockPos(this.fox.getX(), this.fox.getBoundingBox().maxY, this.fox.getZ());
        return !this.fox.world.isSkyVisible(blockPos) && this.fox.getPathfindingFavor(blockPos) >= 0.0F;
    }

    protected boolean cannotCalmDown()
    {
        return !this.fox.world.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, this.fox, this.fox.getBoundingBox().expand(12.0D, 6.0D, 12.0D)).isEmpty();
    }
}
