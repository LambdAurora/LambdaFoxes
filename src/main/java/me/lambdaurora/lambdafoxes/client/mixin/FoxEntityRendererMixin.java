/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.client.mixin;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoxEntityRenderer.class)
public class FoxEntityRendererMixin extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>>
{
    public FoxEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher, FoxEntityModel<FoxEntity> entityModel, float f)
    {
        super(entityRenderDispatcher, entityModel, f);
    }

    @Override
    public Identifier getTexture(FoxEntity entity)
    {
        return ((LambdaFoxEntity) entity).getFoxType().getTextureId(entity);
    }
}
