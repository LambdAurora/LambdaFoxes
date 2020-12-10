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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.util.math.Dilation;
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
public class LambdaFoxEntityModel<T extends FoxEntity> extends FoxEntityModel<T> implements PettableEntityModel {
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private float walkingAnimation;

    public LambdaFoxEntityModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static TexturedModelData getFoxModelData(Dilation extra) {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(1, 5)
                        .cuboid(-3.f, -2.f, -5.f, 8.f, 6.f, 6.f, extra),
                ModelTransform.pivot(-1.f, 16.5f, -3.f));
        head.addChild("right_ear", ModelPartBuilder.create()
                        .uv(8, 1)
                        .cuboid(-3.f, -4.f, -4.f, 2.f, 2.f, 1.f, extra),
                ModelTransform.NONE);
        head.addChild("left_ear", ModelPartBuilder.create()
                        .uv(15, 1)
                        .cuboid(3.f, -4.f, -4.f, 2.f, 2.f, 1.f, extra),
                ModelTransform.NONE);
        head.addChild("nose", ModelPartBuilder.create()
                        .uv(6, 18)
                        .cuboid(-1.f, 2.01f, -8.f, 4.f, 2.f, 3.f, extra),
                ModelTransform.NONE);
        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                        .uv(24, 15)
                        .cuboid(-3.f, 3.999f, -3.5f, 6.f, 11.f, 6.f, extra),
                ModelTransform.of(0, 16.f, -6.f, 1.5707964f, 0, 0));
        Dilation dilation = extra.add(0.001f);
        ModelPartBuilder left_leg = ModelPartBuilder.create()
                .uv(4, 24)
                .cuboid(2.f, 0.5f, -1.f, 2.f, 6.f, 2.f, dilation);
        ModelPartBuilder right_leg = ModelPartBuilder.create()
                .uv(13, 24)
                .cuboid(2.f, 0.5f, -1.f, 2.f, 6.f, 2.f, dilation);
        root.addChild("right_hind_leg", right_leg, ModelTransform.pivot(-5.f, 17.5f, 7.f));
        root.addChild("left_hind_leg", left_leg, ModelTransform.pivot(-1.f, 17.5f, 7.f));
        root.addChild("right_front_leg", right_leg, ModelTransform.pivot(-5.f, 17.5f, 0));
        root.addChild("left_front_leg", left_leg, ModelTransform.pivot(-1.f, 17.5f, 0));
        ModelPartData tail = body.addChild("tail", ModelPartBuilder.create()
                        .uv(30, 0),
                ModelTransform.of(0.f, 15.f, -2.f, -0.05235988f, 0, 0));
        tail.addChild("tail_data", ModelPartBuilder.create()
                        .uv(30, 0)
                        .cuboid(0.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, extra),
                ModelTransform.pivot(-2.f, 0, 0));
        return TexturedModelData.of(data, 48, 32);
    }

    public static TexturedModelData getFennecModelData(Dilation extra) {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(1, 5)
                        .cuboid(-3.f, -2.f, -5.f, 8.f, 6.f, 6.f, extra),
                ModelTransform.pivot(-1.f, 16.5f, -3.f));
        head.addChild("right_ear", ModelPartBuilder.create()
                        .uv(8, 1)
                        .cuboid(-4.f, -5.f, -4.f, 3.f, 4.f, 1.f, extra),
                ModelTransform.of(-.9f, 0, .5f, 0, -.25f, 0));
        head.addChild("left_ear", ModelPartBuilder.create()
                        .uv(15, 1)
                        .cuboid(3.f, -5.f, -4.f, 3.f, 4.f, 1.f, extra),
                ModelTransform.of(1.f, 0, 1.f, 0, .25f, 0));
        head.addChild("nose", ModelPartBuilder.create()
                        .uv(6, 18)
                        .cuboid(-1.f, 2.01f, -8.f, 4.f, 2.f, 3.f, extra),
                ModelTransform.NONE);
        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                        .uv(24, 15)
                        .cuboid(-3.f, 3.999f, -3.5f, 6.f, 11.f, 6.f, extra),
                ModelTransform.of(0, 16.f, -6.f, 1.5707964f, 0, 0));
        Dilation dilation = extra.add(0.001f);
        ModelPartBuilder left_leg = ModelPartBuilder.create()
                .uv(4, 24)
                .cuboid(2.f, 0.5f, -1.f, 2.f, 6.f, 2.f, dilation);
        ModelPartBuilder right_leg = ModelPartBuilder.create()
                .uv(13, 24)
                .cuboid(2.f, 0.5f, -1.f, 2.f, 6.f, 2.f, dilation);
        root.addChild("right_hind_leg", right_leg, ModelTransform.pivot(-5.f, 17.5f, 7.f));
        root.addChild("left_hind_leg", left_leg, ModelTransform.pivot(-1.f, 17.5f, 7.f));
        root.addChild("right_front_leg", right_leg, ModelTransform.pivot(-5.f, 17.5f, 0));
        root.addChild("left_front_leg", left_leg, ModelTransform.pivot(-1.f, 17.5f, 0));
        ModelPartData tail = body.addChild("tail", ModelPartBuilder.create()
                        .uv(30, 0),
                ModelTransform.of(0.f, 15.f, -2.f, -0.05235988f, 0, 0));
        tail.addChild("tail_data", ModelPartBuilder.create()
                        .uv(30, 0)
                        .cuboid(0.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, extra),
                ModelTransform.pivot(-2.f, 0, 0));
        return TexturedModelData.of(data, 48, 32);
    }

    @Override
    public @NotNull ModelPart getTail() {
        return this.tail;
    }

    @Override
    public void animateModel(T fox, float limbAngle, float limbDistance, float tickDelta) {
        this.body.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.head.setPivot(-1.f, 16.5f, -3.f);
        this.head.yaw = 0;
        this.head.roll = fox.getHeadRoll(tickDelta);
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        this.body.setPivot(0, 16.f, -6.f);
        this.body.roll = 0;
        this.rightHindLeg.setPivot(-5.f, 17.5f, 7.f);
        this.leftHindLeg.setPivot(-1.f, 17.5f, 7.f);
        if (fox.isInSneakingPose()) {
            this.body.pitch = 1.6755161f;
            float i = fox.getBodyRotationHeightOffset(tickDelta);
            this.body.setPivot(0, 16.f + fox.getBodyRotationHeightOffset(tickDelta), -6.f);
            this.head.setPivot(-1.f, 16.5f + i, -3.f);
            this.head.yaw = 0;
        } else if (fox.isSleeping()) {
            this.body.roll = -1.5707964f;
            this.body.setPivot(0, 21.f, -6.f);
            this.tail.pitch = -2.6179938f;
            if (this.child) {
                this.tail.pitch = -2.1816616f;
                this.body.setPivot(0, 21.f, -2.f);
            }

            this.head.setPivot(1.f, 19.49f, -3.f);
            this.head.pitch = 0;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (fox.isSitting()) {
            this.body.pitch = 0.5235988f;
            this.body.setPivot(0, 9.f, -3.f);
            this.tail.pitch = 0.7853982f;
            this.head.setPivot(-1.f, 10, -0.25f);
            this.head.pitch = 0;
            this.head.yaw = 0;
            if (this.child) {
                this.head.setPivot(-1.f, 13.f, -3.75f);
            }

            this.rightHindLeg.pitch = -1.3089969f;
            this.rightHindLeg.setPivot(-5.f, 21.5f, 6.75f);
            this.leftHindLeg.pitch = -1.3089969f;
            this.leftHindLeg.setPivot(-1.f, 21.5f, 6.75f);
            this.rightFrontLeg.pitch = -0.2617994f;
            this.leftFrontLeg.pitch = -0.2617994f;
        }

        this.animateTailWhilePet((PettableEntity) fox, tickDelta);

        // Ears.
        /*boolean bigEars = ((LambdaFoxEntity) fox).getFoxType().bigEars;
        this.leftBigEar.visible = bigEars;
        this.leftEar.visible = !bigEars;

        this.rightBigEar.visible = bigEars;
        this.rightEar.visible = !bigEars;*/
    }

    @Override
    public void setAngles(T fox, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        if (!fox.isSleeping() && !fox.isWalking() && !fox.isInSneakingPose()) {
            this.head.pitch = headPitch * 0.017453292f;
            this.head.yaw = headYaw * 0.017453292f;
        }

        if (fox.isSleeping()) {
            this.head.pitch = 0;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(animationProgress * 0.027f) / 22.f;
        }

        float l;
        if (fox.isInSneakingPose()) {
            l = MathHelper.cos(animationProgress) * 0.01f;
            this.body.yaw = l;
            this.rightHindLeg.roll = l;
            this.leftHindLeg.roll = l;
            this.rightFrontLeg.roll = l / 2.f;
            this.leftFrontLeg.roll = l / 2.f;
        }

        if (fox.isWalking()) {
            this.walkingAnimation += 0.67f;
            this.rightHindLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662f) * 0.1f;
            this.leftHindLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662f + 3.1415927f) * 0.1f;
            this.rightFrontLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662f + 3.1415927f) * 0.1f;
            this.leftFrontLeg.pitch = MathHelper.cos(this.walkingAnimation * 0.4662f) * 0.1f;
        }

        this.tail.pitch = animationProgress;
    }
}
