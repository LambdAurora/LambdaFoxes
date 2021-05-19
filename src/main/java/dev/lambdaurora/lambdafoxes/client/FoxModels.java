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

package dev.lambdaurora.lambdafoxes.client;

import dev.lambdaurora.lambdafoxes.LambdaFoxes;
import dev.lambdaurora.lambdafoxes.registry.FoxType;
import dev.lambdaurora.lambdafoxes.mixin.client.EntityModelLayersAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class FoxModels {
    public static final EntityModelLayer FOX_MODEL_LAYER = new EntityModelLayer(LambdaFoxes.id("fox"), "main");

    static void register() {
        FoxType.stream().map(FoxType::getModel).distinct().forEach(EntityModelLayersAccessor.getLayers()::add);
        FoxType.stream().map(FoxType::getArmorModel).distinct().forEach(EntityModelLayersAccessor.getLayers()::add);
    }

    public static void registerModel(FoxType type, Function<Dilation, TexturedModelData> data) {
        EntityModelLayerRegistry.registerModelLayer(type.getModel(), () -> data.apply(Dilation.NONE));
        EntityModelLayerRegistry.registerModelLayer(type.getArmorModel(), () -> data.apply(new Dilation(.25f)));
    }
}
