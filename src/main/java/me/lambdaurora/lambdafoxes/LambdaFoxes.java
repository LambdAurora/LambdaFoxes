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

package me.lambdaurora.lambdafoxes;

import me.lambdaurora.lambdafoxes.registry.LambdaFoxesRegistry;
import me.lambdaurora.lambdafoxes.tag.BiomeTagReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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
public class LambdaFoxes implements ModInitializer
{
    public static final String MODID = "lambdafoxes";
    private static LambdaFoxes INSTANCE;
    public final Logger logger = LogManager.getLogger("lambdafoxes");

    @Override
    public void onInitialize()
    {
        INSTANCE = this;
        this.log("Initializing LambdaFoxes...");

        BiomeTagReloadListener listener = new BiomeTagReloadListener();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(listener);
        ServerLifecycleEvents.SERVER_STARTING.register(listener::onServerStarting);

        LambdaFoxesRegistry.init();

        BiomeModifications.create(id("foxier_fox"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("minecraft")
                                    && (id.getPath().equals("forest")
                                    || id.getPath().equals("flower_forest")
                                    || id.getPath().startsWith("dark_forest"));
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 5, 2, 4))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
                            if (!id.getNamespace().equals("minecraft"))
                                return false;
                            return id.getPath().startsWith("desert") || id.getPath().startsWith("savanna");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 2, 1, 3))
                )
                /* Terrestria */
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().equals("caldera_foothills");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 5, 2, 4))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().startsWith("dunes");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 6, 1, 2))
                )
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
                            return id.getNamespace().equals("terrestria") && id.getPath().equals("oasis");
                        }), (selection, context) -> context.getSpawnSettings()
                                .addSpawn(SpawnGroup.CREATURE, new SpawnEntry(EntityType.FOX, 6, 1, 2))
                )
                /* Traverse */
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.foundInOverworld().and(context -> {
                            Identifier id = context.getBiomeKey().getValue();
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
    public void log(String info)
    {
        this.logger.info("[LambdaFoxes] " + info);
    }

    /**
     * Returns a LambdaFoxes identifier.
     *
     * @param path the path
     * @return the identifier
     */
    public static Identifier id(@NotNull String path)
    {
        return new Identifier(MODID, path);
    }

    public static LambdaFoxes get()
    {
        return INSTANCE;
    }
}
