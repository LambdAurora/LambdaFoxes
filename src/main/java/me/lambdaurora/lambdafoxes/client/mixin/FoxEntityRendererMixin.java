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

package me.lambdaurora.lambdafoxes.client.mixin;

import me.lambdaurora.lambdafoxes.client.render.FoxArmorFeatureRenderer;
import me.lambdaurora.lambdafoxes.client.render.LambdaFoxEntityModel;
import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntityRenderer.class)
public abstract class FoxEntityRendererMixin extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>>
{
    public FoxEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher, FoxEntityModel<FoxEntity> entityModel, float f)
    {
        super(entityRenderDispatcher, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(EntityRenderDispatcher dispatcher, CallbackInfo ci)
    {
        this.model = new LambdaFoxEntityModel<>(0.f);
        this.addFeature(new FoxArmorFeatureRenderer(this));
    }

    @Override
    public float getAnimationProgress(FoxEntity fox, float tickDelta)
    {
        return ((LambdaFoxEntity) fox).getTailAngle();
    }

    @Override
    public void scale(FoxEntity fox, MatrixStack matrices, float tickDelta)
    {
        float scaleFactor = ((LambdaFoxEntity) fox).getFoxType().scaleFactor;
        matrices.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    /*@Override
    public void render(FoxEntity fox, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(fox, yaw, tickDelta, matrices, vertexConsumers, light);
        long time = fox.getEntityWorld().getTime();
        BeaconBlockEntityRenderer.renderLightBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, 1.0f, time, 0, 256, DyeColor.ORANGE.getColorComponents(), 0.25f, 0.35f);
        BeaconBlockEntityRenderer.renderLightBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, 1.0f, time, 0, -256, DyeColor.ORANGE.getColorComponents(), 0.25f, 0.35f);
    }*/

    /**
     * @author LambdAurora
     * @reason Replace the get texture with one which allows custom fox types.
     */
    @Overwrite
    public Identifier getTexture(FoxEntity fox)
    {
        return ((LambdaFoxEntity) fox).getFoxType().getTextureId(fox);
    }
}
