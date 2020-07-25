/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxHeldItemFeatureRenderer.class)
public class FoxHeldItemFeatureRendererMixin
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, FoxEntity fox, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci)
    {
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrices.translate(0, -0.2, 0);
    }
}
