/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.tag;

import com.google.common.util.concurrent.MoreExecutors;
import me.lambdaurora.lambdafoxes.LambdaFoxes;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BiomeTagReloadListener implements SimpleSynchronousResourceReloadListener
{
    private static final Identifier ID = LambdaFoxes.mc("");

    @Override
    public Identifier getFabricId()
    {
        return ID;
    }

    @Override
    public void apply(ResourceManager manager)
    {
        RegistryTagContainer<Biome> container = new RegistryTagContainer<>(Registry.BIOME, "tags/biomes", "biome");

        try {
            Map<Identifier, Tag.Builder> map = container.prepareReload(manager, MoreExecutors.directExecutor()).get();
            container.applyReload(map);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        BiomeTags.setContainer(container);
    }
}
