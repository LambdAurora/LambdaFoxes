/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes;

import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents the mod configuration.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class LambdaFoxesConfig
{
    private static boolean DEFAULT_VANILLA_MODE = false;

    public static final Path        CONFIG_FILE_PATH = Paths.get("config/lambdafoxes.toml");
    protected final     FileConfig  config;
    private final       LambdaFoxes mod;

    public LambdaFoxesConfig(@NotNull LambdaFoxes mod)
    {
        this.mod = mod;
        this.config = FileConfig.builder(CONFIG_FILE_PATH).concurrent().defaultResource("/lambdafoxes.toml").autosave().build();
    }

    /**
     * Loads the configuration file.
     */
    public void load()
    {
        this.config.load();
        this.mod.log("Configuration loaded.");
    }

    public void reset()
    {
        this.setVanillaMode(DEFAULT_VANILLA_MODE);
    }

    /**
     * Returns whether the mod is in Vanilla mode or not.
     *
     * @return True if the mod is in Vanilla mode, else false.
     */
    public boolean isVanillaMode()
    {
        return this.config.getOrElse("vanilla", DEFAULT_VANILLA_MODE);
    }

    /**
     * Sets whether the mod is in Vanilla mode or not.
     *
     * @param vanilla True if the mod is in Vanilla mode, else false.
     */
    public void setVanillaMode(boolean vanilla)
    {
        this.config.set("vanilla", vanilla);
    }
}
