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

import me.lambdaurora.lambdafoxes.client.render.LambdaFoxEntityModel;
import me.lambdaurora.lambdafoxes.registry.FoxType;
import me.lambdaurora.lambdafoxes.registry.LambdaFoxesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.item.DyeableItem;

/**
 * Represents the LambdaFoxes client mod.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public class LambdaFoxesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem) stack.getItem()).getColor(stack), LambdaFoxesRegistry.LEATHER_FOX_ARMOR);
        FoxModels.register();
        FoxModels.registerModel(FoxType.RED, LambdaFoxEntityModel::getFoxModelData);
        FoxModels.registerModel(FoxType.FENNEC, LambdaFoxEntityModel::getFennecModelData);
    }
}
