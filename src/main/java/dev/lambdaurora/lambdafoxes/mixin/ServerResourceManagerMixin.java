/*
 * Copyright (c) 2021 LambdAurora <aurora42lambda@gmail.com>
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

package dev.lambdaurora.lambdafoxes.mixin;

import dev.lambdaurora.lambdafoxes.LambdaFoxes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public abstract class ServerResourceManagerMixin {
    @Shadow
    public abstract TagManager getRegistryTagManager();

    @Shadow
    @Final
    private TagManagerLoader registryTagManager;

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Inject(method = "loadRegistryTags", at = @At("HEAD"))
    private void onLoadRegistryTags(CallbackInfo ci) {
        var registryManager = ((TagManagerLoaderAccessor) this.registryTagManager).getRegistryManager();
        registryManager.getOptional(Registry.BIOME_KEY).ifPresent(biomeRegistry -> {
            var requiredTagList = LambdaFoxes.REQUIRED_TAGS;
            var groupLoader = new TagGroupLoader<>(biomeRegistry::getOrEmpty, requiredTagList.getDataType());
            var tagGroup = groupLoader.load(this.resourceManager);
            var mutableMap = new Object2ObjectOpenHashMap<>(((TagManagerAccessor) this.getRegistryTagManager()).getTagGroups());
            mutableMap.put(Registry.BIOME_KEY, tagGroup);
            ((TagManagerAccessor) this.getRegistryTagManager()).setTagGroups(mutableMap);
            requiredTagList.updateTagManager(this.getRegistryTagManager());
        });
    }
}
