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

package me.lambdaurora.lambdafoxes.tag;

import com.google.common.util.concurrent.MoreExecutors;
import me.lambdaurora.lambdafoxes.LambdaFoxes;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BiomeTagReloadListener implements SimpleSynchronousResourceReloadListener
{
    private static final Identifier ID = LambdaFoxes.id("tags");
    private MinecraftServer server;

    @Override
    public Identifier getFabricId()
    {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies()
    {
        return Arrays.asList(ResourceReloadListenerKeys.TAGS, ResourceReloadListenerKeys.ADVANCEMENTS);
    }

    @Override
    public void apply(ResourceManager manager)
    {
        // MOJANG PLEASE ADD BIOME TAGS
        if (server == null)
            return;
        TagGroupLoader<Biome> container = new TagGroupLoader<>(server.getRegistryManager().get(Registry.BIOME_KEY)::getOrEmpty, "tags/biomes", "biome");

        try {
            Map<Identifier, Tag.Builder> map = container.prepareReload(manager, MoreExecutors.directExecutor()).get();
            BiomeTags.setContainer(container.applyReload(map));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onServerStarting(MinecraftServer server)
    {
        this.server = server;

        // THIS IS CURSED
        // AND YOU WON'T LIKE IT
        // THEY SAID BIOME TAGS WERE IMPOSSIBLE
        // AND I SAY I HAVE RESOURCE LOADER

        ReloadableResourceManagerImpl resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);

        server.getDataPackManager().getEnabledProfiles().forEach(profile -> {
            resourceManager.addPack(profile.createResourcePack());
        });

        this.apply(resourceManager);

        resourceManager.close();
    }
}
