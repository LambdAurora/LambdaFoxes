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

package dev.lambdaurora.lambdafoxes.tag;

import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.HashSet;
import java.util.Set;

public class UhOhRequiredTagListRegistry {
    private static final Set<RequiredTagList<?>> fabricRequiredTags = new HashSet<>();

    public static <T> RequiredTagList<T> register(RegistryKey<? extends Registry<T>> registryKey, String path) {
        var tagList = RequiredTagListRegistry.register(registryKey, path);
        fabricRequiredTags.add(tagList);
        return tagList;
    }

    public static Set<RequiredTagList<?>> getFabricRequiredTags() {
        return fabricRequiredTags;
    }
}
