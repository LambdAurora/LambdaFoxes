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

package dev.lambdaurora.lambdafoxes.mixin.client;

import dev.lambdaurora.lambdafoxes.client.render.LambdaFoxEntityModel;
import dev.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import dev.lambdaurora.lambdafoxes.registry.FoxType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import dev.lambdaurora.lambdafoxes.client.FoxModels;
import dev.lambdaurora.lambdafoxes.client.render.FoxArmorFeatureRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
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

import java.util.Map;

@Mixin(FoxEntityRenderer.class)
public abstract class FoxEntityRendererMixin extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    private final Map<FoxType, LambdaFoxEntityModel<FoxEntity>> lambdafoxes$models = new Object2ObjectOpenHashMap<>();

    public FoxEntityRendererMixin(EntityRendererFactory.Context context, FoxEntityModel<FoxEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.model = new LambdaFoxEntityModel<>(context.getPart(FoxModels.FOX_MODEL_LAYER));
        FoxType.stream().forEach(type -> {
            this.lambdafoxes$models.put(type, new LambdaFoxEntityModel<>(context.getPart(type.getModel())));
            this.features.add(new FoxArmorFeatureRenderer(this,
                    type, new LambdaFoxEntityModel<>(context.getPart(type.getArmorModel()))));
        });
    }

    @Override
    public float getAnimationProgress(FoxEntity fox, float tickDelta) {
        return ((LambdaFoxEntity) fox).getTailAngle();
    }

    @Override
    public void scale(FoxEntity fox, MatrixStack matrices, float tickDelta) {
        float scaleFactor = ((LambdaFoxEntity) fox).getFoxType().getScaleFactor();
        matrices.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    @Override
    public void render(FoxEntity fox, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        this.model = this.lambdafoxes$models.get(((LambdaFoxEntity) fox).getFoxType());
        super.render(fox, yaw, tickDelta, matrices, vertexConsumers, light);
        /*long time = fox.getEntityWorld().getTime();
        BeaconBlockEntityRenderer.renderLightBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, 1.0f, time, 0, 256, DyeColor.ORANGE.getColorComponents(), 0.25f, 0.35f);
        BeaconBlockEntityRenderer.renderLightBeam(matrices, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, 1.0f, time, 0, -256, DyeColor.ORANGE.getColorComponents(), 0.25f, 0.35f);*/
    }

    /**
     * @author LambdAurora
     * @reason Replace the get texture with one which allows custom fox types.
     */
    @Overwrite
    public Identifier getTexture(FoxEntity fox) {
        return ((LambdaFoxEntity) fox).getFoxType().getTextureId(fox);
    }
}
