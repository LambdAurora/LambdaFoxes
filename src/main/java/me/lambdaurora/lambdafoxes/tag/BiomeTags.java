/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.tag;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.world.biome.Biome;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BiomeTags
{
    private static       TagContainer<Biome> CONTAINER = new TagContainer<>((identifier) -> {
        return Optional.empty();
    }, "", "");
    private static final List<CachedTag>     tags      = new ArrayList<>();

    public static @NotNull TagContainer<Biome> getContainer()
    {
        return CONTAINER;
    }

    public static Tag.Identified<Biome> register(@NotNull Identifier identifier)
    {
        CachedTag cachedTag = new CachedTag(new net.minecraft.util.Identifier(identifier.toString()));
        tags.add(cachedTag);
        return cachedTag;
    }

    static void setContainer(@NotNull TagContainer<Biome> container)
    {
        CONTAINER = container;
        tags.parallelStream().forEach(tag -> tag.update(container::get));
    }

    static class CachedTag implements Tag.Identified<Biome>
    {
        protected final net.minecraft.util.Identifier id;
        private         Tag<Biome>                    currentTag;

        CachedTag(net.minecraft.util.Identifier id)
        {
            this.id = id;
        }

        @Override
        public net.minecraft.util.Identifier getId()
        {
            return this.id;
        }

        private Tag<Biome> get()
        {
            if (this.currentTag == null) {
                throw new IllegalStateException("Tag " + this.id + " used before it was bound");
            } else {
                return this.currentTag;
            }
        }

        void update(Function<net.minecraft.util.Identifier, Tag<Biome>> tagFactory)
        {
            this.currentTag = tagFactory.apply(this.id);
        }

        @Override
        public boolean contains(Biome entry)
        {
            if (this.currentTag == null)
                return false;
            return this.currentTag.contains(entry);
        }

        @Override
        public List<Biome> values()
        {
            return this.get().values();
        }
    }
}
