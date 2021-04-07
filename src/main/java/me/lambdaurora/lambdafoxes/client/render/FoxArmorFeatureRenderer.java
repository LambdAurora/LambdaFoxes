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

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import me.lambdaurora.lambdafoxes.registry.FoxType;
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
public class FoxArmorFeatureRenderer extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    @SuppressWarnings("unchecked")
    private final FoxType type;
    private final LambdaFoxEntityModel<FoxEntity> model;// = new LambdaFoxEntityModel(.25f);

    public FoxArmorFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> context,
                                   FoxType type, LambdaFoxEntityModel<FoxEntity> model) {
        super(context);
        this.type = type;
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, FoxEntity fox,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!((LambdaFoxEntity) fox).getFoxType().equals(this.type))
            return;
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

            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers,
                    RenderLayer.getArmorCutoutNoCull(armor.entityTexture), false, stack.hasEnchantments());
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1.f);
        }
    }
}
