/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.render;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FoxArmorFeatureRenderer extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>>
{
    @SuppressWarnings("unchecked")
    private final LambdaFoxEntityModel<FoxEntity> model = new LambdaFoxEntityModel<>(.25f);

    public FoxArmorFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> context)
    {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, FoxEntity fox, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        ItemStack stack = ((LambdaFoxEntity) fox).getFoxArmor();

        if (stack.getItem() instanceof FoxArmorItem) {
            FoxArmorItem armor = (FoxArmorItem) stack.getItem();
            this.getContextModel().copyStateTo(this.model);

            this.model.animateModel(fox, limbAngle, limbDistance, tickDelta);
            this.model.setAngles(fox, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            float r = 1.f;
            float g = 1.f;
            float b = 1.f;

            if (stack.getItem() instanceof DyeableItem) {
                int color = ((DyeableItem) stack.getItem()).getColor(stack);
                r = (color >> 16 & 255) / 255.f;
                g = (color >> 8 & 255) / 255.f;
                b = (color & 255) / 255.f;
            }

            VertexConsumer vertexConsumer = ItemRenderer.method_27952(vertexConsumers, RenderLayer.getArmorCutoutNoCull(armor.entityTexture), false, stack.hasEnchantments());
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1.f);
        }
    }
}
