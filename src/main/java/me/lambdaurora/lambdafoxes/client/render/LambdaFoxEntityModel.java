/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.render;

import com.google.common.collect.ImmutableList;
import me.lambdaurora.lambdafoxes.client.mixin.ModelPartAccessor;
import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.entity.PettableEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the enhanced fox entity model.
 *
 * @param <T> The type of entity.
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public class LambdaFoxEntityModel<T extends FoxEntity> extends FoxEntityModel<T> implements PettableEntityModel
{
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart leftBigEar;
    private final ModelPart rightBigEar;
    private final ModelPart nose;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart tailCuboid;
    private       float     walkingAnimation;

    public LambdaFoxEntityModel(float extra)
    {
        // Clear because super constructor.
        ((ModelPartAccessor) this.head).lambdafoxes_getChildren().clear();
        ((ModelPartAccessor) this.head).lambdafoxes_getCuboids().clear();
        this.head.addCuboid(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F, extra);

        // Yeet everything else, time to make my own thing.

        // Ears
        this.leftEar = new ModelPart(this, 15, 1);
        this.leftEar.addCuboid(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F, extra);
        this.rightEar = new ModelPart(this, 8, 1);
        this.rightEar.addCuboid(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F, extra);

        // Big ears
        this.leftBigEar = new ModelPart(this, 15, 0);
        this.leftBigEar.visible = false;
        this.leftBigEar.yaw = 0.25f;
        this.leftBigEar.setPivot(1.f, 0, 1.f);
        this.leftBigEar.addCuboid(3.f, -5.f, -4.f, 3.f, 4.f, 1.f, extra);

        this.rightBigEar = new ModelPart(this, 7, 0);
        this.rightBigEar.visible = false;
        this.rightBigEar.yaw = -0.25f;
        this.rightBigEar.setPivot(-.9f, 0, 0.50f);
        this.rightBigEar.addCuboid(-4.f, -5.f, -4.f, 3.f, 4.f, 1.f, extra);

        // Nose to boop
        this.nose = new ModelPart(this, 6, 18);
        this.nose.addCuboid(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F, extra);

        this.head.addChild(this.leftEar);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.leftBigEar);
        this.head.addChild(this.rightBigEar);
        this.head.addChild(this.nose);

        // Torso
        this.torso = new ModelPart(this, 24, 15);
        this.torso.addCuboid(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F, extra);
        this.torso.setPivot(0.0F, 16.0F, -6.0F);

        // Legs
        this.rightBackLeg = new ModelPart(this, 13, 24);
        this.rightBackLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, extra + 0.001F);
        this.rightBackLeg.setPivot(-5.0F, 17.5F, 7.0F);
        this.leftBackLeg = new ModelPart(this, 4, 24);
        this.leftBackLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, extra + 0.001F);
        this.leftBackLeg.setPivot(-1.0F, 17.5F, 7.0F);
        this.rightFrontLeg = new ModelPart(this, 13, 24);
        this.rightFrontLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, extra + 0.001F);
        this.rightFrontLeg.setPivot(-5.0F, 17.5F, 0.0F);
        this.leftFrontLeg = new ModelPart(this, 4, 24);
        this.leftFrontLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, extra + 0.001F);
        this.leftFrontLeg.setPivot(-1.0F, 17.5F, 0.0F);

        // Tail
        this.tail = new ModelPart(this, 24, 24);
        this.tailCuboid = new ModelPart(this, 30, 0);
        this.resetTailPivot();
        this.tailCuboid.addCuboid(0.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, extra);
        this.tail.addChild(this.tailCuboid);
        this.torso.addChild(this.tail);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts()
    {
        return ImmutableList.of(this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public @NotNull ModelPart getTail()
    {
        return this.tail;
    }

    /**
     * Resets the tail pivot.
     */
    @Override
    public void resetTailPivot()
    {
        this.tail.setPivot(0.f, 15.f, -2.f);
        this.tailCuboid.setPivot(-2.f, 0.f, 0.f);
    }

    @Override
    public void animateModel(T fox, float limbAngle, float limbDistance, float tickDelta)
    {
        this.torso.pitch = 1.5707964F;
        this.tail.pitch = -0.05235988F;
        this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.head.setPivot(-1.0F, 16.5F, -3.0F);
        this.head.yaw = 0.0F;
        this.head.roll = fox.getHeadRoll(tickDelta);
        this.rightBackLeg.visible = true;
        this.leftBackLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        this.torso.setPivot(0.0F, 16.0F, -6.0F);
        this.torso.roll = 0.0F;
        this.rightBackLeg.setPivot(-5.0F, 17.5F, 7.0F);
        this.leftBackLeg.setPivot(-1.0F, 17.5F, 7.0F);
        if (fox.isInSneakingPose()) {
            this.torso.pitch = 1.6755161F;
            float i = fox.getBodyRotationHeightOffset(tickDelta);
            this.torso.setPivot(0.0F, 16.0F + fox.getBodyRotationHeightOffset(tickDelta), -6.0F);
            this.head.setPivot(-1.0F, 16.5F + i, -3.0F);
            this.head.yaw = 0.0F;
        } else if (fox.isSleeping()) {
            this.torso.roll = -1.5707964F;
            this.torso.setPivot(0.0F, 21.0F, -6.0F);
            this.tail.pitch = -2.6179938F;
            if (this.child) {
                this.tail.pitch = -2.1816616F;
                this.torso.setPivot(0.0F, 21.0F, -2.0F);
            }

            this.head.setPivot(1.0F, 19.49F, -3.0F);
            this.head.pitch = 0.0F;
            this.head.yaw = -2.0943952F;
            this.head.roll = 0.0F;
            this.rightBackLeg.visible = false;
            this.leftBackLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (fox.isSitting()) {
            this.torso.pitch = 0.5235988F;
            this.torso.setPivot(0.0F, 9.0F, -3.0F);
            this.tail.pitch = 0.7853982F;
            this.head.setPivot(-1.0F, 10.0F, -0.25F);
            this.head.pitch = 0.0F;
            this.head.yaw = 0.0F;
            if (this.child) {
                this.head.setPivot(-1.0F, 13.0F, -3.75F);
            }

            this.rightBackLeg.pitch = -1.3089969F;
            this.rightBackLeg.setPivot(-5.0F, 21.5F, 6.75F);
            this.leftBackLeg.pitch = -1.3089969F;
            this.leftBackLeg.setPivot(-1.0F, 21.5F, 6.75F);
            this.rightFrontLeg.pitch = -0.2617994F;
            this.leftFrontLeg.pitch = -0.2617994F;
        }

        this.animateTailWhilePet((PettableEntity) fox, tickDelta);

        // Ears.
        boolean bigEars = ((LambdaFoxEntity) fox).getFoxType().bigEars;
        this.leftBigEar.visible = bigEars;
        this.leftEar.visible = !bigEars;

        this.rightBigEar.visible = bigEars;
        this.rightEar.visible = !bigEars;
    }

    @Override
    public void setAngles(T fox, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
    {
        if (!fox.isSleeping() && !fox.isWalking() && !fox.isInSneakingPose()) {
            this.head.pitch = headPitch * 0.017453292F;
            this.head.yaw = headYaw * 0.017453292F;
        }

        if (fox.isSleeping()) {
            this.head.pitch = 0.0F;
            this.head.yaw = -2.0943952F;
            this.head.roll = MathHelper.cos(animationProgress * 0.027F) / 22.0F;
        }

        float l;
        if (fox.isInSneakingPose()) {
            l = MathHelper.cos(animationProgress) * 0.01F;
            this.torso.yaw = l;
            this.rightBackLeg.roll = l;
            this.leftBackLeg.roll = l;
            this.rightFrontLeg.roll = l / 2.0F;
            this.leftFrontLeg.roll = l / 2.0F;
        }

        if (fox.isWalking()) {
            this.walkingAnimation += 0.67F;
            this.rightBackLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662F) * 0.1F;
            this.leftBackLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662F + 3.1415927F) * 0.1F;
            this.rightFrontLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662F + 3.1415927F) * 0.1F;
            this.leftFrontLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662F) * 0.1F;
        }

        this.tail.pitch = animationProgress;
    }
}
