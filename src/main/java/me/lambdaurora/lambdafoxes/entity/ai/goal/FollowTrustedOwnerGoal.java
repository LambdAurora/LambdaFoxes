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

package me.lambdaurora.lambdafoxes.entity.ai.goal;

import me.lambdaurora.lambdafoxes.entity.TrustEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Represents a goal which makes the entity follow its owner if the trust level is high enough.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FollowTrustedOwnerGoal extends Goal
{
    private final FoxEntity entity;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowTrustedOwnerGoal(FoxEntity entity, double speed, float minDistance, float maxDistance, boolean leavesAllowed)
    {
        this.entity = entity;
        this.world = entity.world;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(entity.getNavigation() instanceof MobNavigation) && !(entity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart()
    {
        Optional<LivingEntity> owner = ((TrustEntity) this.entity).getOwner();
        if (!owner.isPresent())
            return false;

        LivingEntity livingEntity = owner.get();
        if (livingEntity.isSpectator())
            return false;
        else if (!((TrustEntity) this.entity).isTamed())
            return false;
        else if (this.entity.isSitting())
            return false;
        else if (this.entity.squaredDistanceTo(livingEntity) < (double) (this.minDistance * this.minDistance))
            return false;
        else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean shouldContinue()
    {
        if (this.navigation.isIdle()) {
            return false;
        } else if (this.entity.isSitting()) {
            return false;
        } else {
            return this.entity.squaredDistanceTo(this.owner) > (double) (this.maxDistance * this.maxDistance);
        }
    }

    @Override
    public void start()
    {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.entity.getPathfindingPenalty(PathNodeType.WATER);
        this.entity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop()
    {
        this.owner = null;
        this.navigation.stop();
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick()
    {
        this.entity.getLookControl().lookAt(this.owner, 10.0F, (float) this.entity.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
                if (this.entity.squaredDistanceTo(this.owner) >= 144.0D)
                    this.tryTeleport();
                else
                    this.navigation.startMovingTo(this.owner, this.speed);
            }
        }
    }

    private void tryTeleport()
    {
        BlockPos blockPos = this.owner.getBlockPos();

        for (int i = 0; i < 10; ++i) {
            int offsetX = this.getRandomInt(-3, 3);
            int offsetY = this.getRandomInt(-1, 1);
            int offsetZ = this.getRandomInt(-3, 3);
            boolean teleported = this.tryTeleportTo(blockPos.getX() + offsetX, blockPos.getY() + offsetY, blockPos.getZ() + offsetZ);
            if (teleported)
                return;
        }
    }

    private boolean tryTeleportTo(int x, int y, int z)
    {
        if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D)
            return false;
        else if (!this.canTeleportTo(new BlockPos(x, y, z)))
            return false;
        else
            this.entity.refreshPositionAndAngles((double) x + 0.5D, y, (double) z + 0.5D, this.entity.yaw, this.entity.pitch);
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos)
    {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE)
            return false;
        else {
            BlockState blockState = this.world.getBlockState(pos.down());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock)
                return false;
            else {
                BlockPos blockPos = pos.subtract(this.entity.getBlockPos());
                return this.world.isSpaceEmpty(this.entity, this.entity.getBoundingBox().offset(blockPos));
            }
        }
    }

    private int getRandomInt(int min, int max)
    {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    }
}
