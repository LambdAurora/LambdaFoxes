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

package dev.lambdaurora.lambdafoxes;

import dev.lambdaurora.lambdafoxes.mixin.TagManagerAccessor;
import dev.lambdaurora.lambdafoxes.mixin.TagManagerLoaderAccessor;
import dev.lambdaurora.lambdafoxes.registry.LambdaFoxesRegistry;
import dev.lambdaurora.lambdafoxes.tag.UhOhRequiredTagListRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings.SpawnEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaFoxes mod.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class LambdaFoxes implements ModInitializer {
    public static final String NAMESPACE = "lambdafoxes";
    public static final RequiredTagList<Biome> REQUIRED_TAGS = UhOhRequiredTagListRegistry.register(Registry.BIOME_KEY, "tags/biomes");
    private static LambdaFoxes INSTANCE;
    public final Logger logger = LogManager.getLogger("lambdafoxes");

    @Override
    public void onInitialize() {
        INSTANCE = this;
        this.log("Initializing LambdaFoxes...");

        LambdaFoxesRegistry.init();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.reloadBiomeTags(server.getResourceManager(), server.getRegistryManager(), server.getTagManager());
        });

        BiomeModifications.create(id("foxier_fox"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("minecraft")
                                    && (id.getPath().equals("forest")
                                    || id.getPath().equals("flower_forest")
                                    || id.getPath().startsWith("dark_forest"));
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 5, 2, 4))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            if (!id.getNamespace().equals("minecraft"))
                                return false;
                            return id.getPath().startsWith("desert") || id.getPath().startsWith("savanna");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 2, 1, 3))
                )
                /* Terrestria */
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().equals("caldera_foothills");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 5, 2, 4))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().startsWith("dunes");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 6, 1, 2))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().equals("oasis");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 6, 1, 2))
                )
                /* Traverse */
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            var id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("traverse") && id.getPath().startsWith("autumnal_wood");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 5, 2, 4))
                );
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info the message to print
     */
    public void log(String info) {
        this.logger.info("[LambdaFoxes] " + info);
    }

    /**
     * Returns a LambdaFoxes identifier.
     *
     * @param path the path
     * @return the identifier
     */
    public static Identifier id(@NotNull String path) {
        return new Identifier(NAMESPACE, path);
    }

    private void reloadBiomeTags(ResourceManager resourceManager, DynamicRegistryManager registryManager, TagManager registryTagManager) {
        registryManager.getOptional(Registry.BIOME_KEY).ifPresent(biomeRegistry -> {
            var requiredTagList = LambdaFoxes.REQUIRED_TAGS;
            var groupLoader = new TagGroupLoader<>(biomeRegistry::getOrEmpty, requiredTagList.getDataType());
            var tagGroup = groupLoader.load(resourceManager);
            var mutableMap = new Object2ObjectOpenHashMap<>(((TagManagerAccessor) registryTagManager).getTagGroups());
            mutableMap.put(Registry.BIOME_KEY, tagGroup);
            ((TagManagerAccessor) registryTagManager).setTagGroups(mutableMap);
            requiredTagList.updateTagManager(registryTagManager);
        });
    }

    public static LambdaFoxes get() {
        return INSTANCE;
    }
}
