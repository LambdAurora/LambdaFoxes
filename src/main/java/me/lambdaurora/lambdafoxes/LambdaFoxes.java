/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes;

import me.lambdaurora.lambdafoxes.registry.LambdaFoxesRegistry;
import me.lambdaurora.lambdafoxes.tag.BiomeTagReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aperlambda.lambdacommon.Identifier;
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
    public static final String            MODID  = "lambdafoxes";
    private static      LambdaFoxes       INSTANCE;
    public final        Logger            logger = LogManager.getLogger("lambdafoxes");
    public final        LambdaFoxesConfig config = new LambdaFoxesConfig(this);

    @Override
    public void onInitialize()
    {
        INSTANCE = this;
        this.log("Initializing LambdaFoxes...");

        this.config.load();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BiomeTagReloadListener());

        LambdaFoxesRegistry.init();
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.logger.info("[LambdaFoxes] " + info);
    }

    /**
     * Returns a LambdaFoxes identifier.
     *
     * @param path The path.
     * @return The identifier.
     */
    public static Identifier id(@NotNull String path)
    {
        return new Identifier(MODID, path);
    }

    /**
     * Returns a LambdaFoxes Minecraft identifier.
     *
     * @param path The path.
     * @return The identifier.
     */
    public static net.minecraft.util.Identifier mc(@NotNull String path)
    {
        return new net.minecraft.util.Identifier(MODID, path);
    }
}
