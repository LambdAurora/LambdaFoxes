/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity;

import me.lambdaurora.lambdafoxes.registry.FoxType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the enhanced fox entity.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LambdaFoxEntity extends TrustEntity
{
    FoxType getFoxType();

    void setFoxType(@NotNull FoxType type);
}
