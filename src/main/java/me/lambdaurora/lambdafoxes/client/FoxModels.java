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

package me.lambdaurora.lambdafoxes.client;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.lambdaurora.lambdafoxes.LambdaFoxes;
import me.lambdaurora.lambdafoxes.client.mixin.EntityModelLayersAccessor;
import me.lambdaurora.lambdafoxes.registry.FoxType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.Dilation;

import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class FoxModels {
    public static final EntityModelLayer FOX_MODEL_LAYER = new EntityModelLayer(LambdaFoxes.id("fox"), "main");
    private static final Map<EntityModelLayer, TexturedModelData> FOX_MODELS = new Object2ObjectOpenHashMap<>();

    static void register() {
        FoxType.stream().map(FoxType::getModel).distinct().forEach(EntityModelLayersAccessor.getLayers()::add);
        FoxType.stream().map(FoxType::getArmorModel).distinct().forEach(EntityModelLayersAccessor.getLayers()::add);
    }

    public static void registerModel(FoxType type, Function<Dilation, TexturedModelData> data) {
        FOX_MODELS.put(type.getModel(), data.apply(Dilation.NONE));
        FOX_MODELS.put(type.getArmorModel(), data.apply(new Dilation(.25f)));
    }

    public static void registerModels(ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder) {
        FOX_MODELS.forEach(builder::put);
    }
}
