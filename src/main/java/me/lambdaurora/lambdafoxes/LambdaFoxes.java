/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes;

import me.lambdaurora.lambdafoxes.registry.FoxType;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the LambdaFoxes mod.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class LambdaFoxes implements ModInitializer
{
    private static LambdaFoxes       INSTANCE;
    public final   Logger            logger = LogManager.getLogger("lambdafoxes");
    public final   LambdaFoxesConfig config = new LambdaFoxesConfig(this);

    @Override
    public void onInitialize()
    {
        INSTANCE = this;
        this.log("Initializing LambdaFoxes...");

        this.config.load();
        FoxType.fromId("minecraft:red");
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
}
