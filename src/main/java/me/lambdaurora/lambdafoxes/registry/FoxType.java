/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.registry;

import net.minecraft.entity.passive.FoxEntity;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a fox type.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FoxType implements Identifiable
{
    private static final Map<Integer, FoxType> TYPES = new HashMap<>();

    /* Default Minecraft fox types */

    public static final FoxType RED  = new FoxType(new Identifier("minecraft", "red"), 0, false, true)
    {
        @Override
        public String getKey()
        {
            return this.getName();
        }

        @Override
        public net.minecraft.util.Identifier getTextureId(@NotNull FoxEntity entity)
        {
            return entity.isSleeping() ? new net.minecraft.util.Identifier("textures/entity/fox/fox_sleep.png") : new net.minecraft.util.Identifier("textures/entity/fox/fox.png");
        }
    };
    public static final FoxType SNOW = new FoxType(new Identifier("minecraft", "snow"), 1, false, true)
    {
        @Override
        public String getKey()
        {
            return this.getName();
        }
    };

    public static final FoxType SILVER = register(new Identifier("lambdafoxes", "silver"));
    public static final FoxType CROSS  = register(new Identifier("lambdafoxes", "cross"));

    private final Identifier id;
    private final int        numericId;
    public final  boolean    canSpawnFromBreed;
    public final  boolean    mutated;

    private FoxType(@NotNull Identifier id, int numericId, boolean mutated, boolean canSpawnFromBreed)
    {
        this.id = id;
        this.numericId = numericId;
        this.mutated = mutated;
        this.canSpawnFromBreed = canSpawnFromBreed;

        TYPES.put(numericId, this);
    }

    @Override
    public @NotNull Identifier getIdentifier()
    {
        return this.id;
    }

    /**
     * Returns the fox type key.
     *
     * @return The fox type key.
     */
    public String getKey()
    {
        return this.id.toString();
    }

    /**
     * Returns the numeric id of this fox type.
     *
     * @return The fox type numeric id.
     */
    public int getNumericId()
    {
        return this.numericId;
    }

    /**
     * Returns the texture identifier.
     *
     * @param entity The entity.
     * @return The texture identifier.
     */
    public net.minecraft.util.Identifier getTextureId(@NotNull FoxEntity entity)
    {
        String base = this.getName() + "_fox";
        if (entity.isSleeping())
            base += "_sleep";
        return new net.minecraft.util.Identifier(this.id.getNamespace(), "textures/entity/fox/" + base + ".png");
    }

    /**
     * Returns the fox type associated to the identifier.
     *
     * @param id The string identifier.
     * @return The associated fox type.
     */
    public static @NotNull FoxType fromId(@NotNull String id)
    {
        if (!id.contains(":")) {
            id = "minecraft:" + id;
        }

        try {
            return fromId(new Identifier(id));
        } catch (IllegalArgumentException e) {
            return RED;
        }
    }

    /**
     * Returns the fox type associated to the identifier.
     *
     * @param id The identifier.
     * @return The associated fox type.
     */
    public static @NotNull FoxType fromId(@NotNull Identifier id)
    {
        return TYPES.entrySet().parallelStream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getIdentifier().equals(id))
                .findAny().orElse(RED);
    }

    /**
     * Returns the fox type associated to the numeric id.
     *
     * @param id The numeric id.
     * @return The associated fox type.
     */
    public static @NotNull FoxType fromNumericId(int id)
    {
        return TYPES.getOrDefault(id, RED);
    }

    public static @NotNull FoxType register(@NotNull Identifier id)
    {
        return register(id, true);
    }

    public static @NotNull FoxType register(@NotNull Identifier id, boolean mutated)
    {
        return register(id, mutated, true);
    }

    public static @NotNull FoxType register(@NotNull Identifier id, boolean mutated, boolean canSpawnFromBreed)
    {
        int numericId = computeNumericId(id);
        if (numericId == 0 || numericId == 1)
            throw new IllegalStateException("FoxType `" + id.toString() + "` is invalid, cannot compute valid numeric id.");

        System.out.println("Computed id " + numericId + " for " + id.toString());
        return new FoxType(id, numericId, mutated, canSpawnFromBreed);
    }

    private static int computeNumericId(int a, int b)
    {
        return ((a + b) ^ 2 + 3 * a + b) / 2;
    }

    private static int computeNumericId(@NotNull Identifier id)
    {
        String str = id.toString();
        if (str.length() < 2)
            return 0;
        int result = computeNumericId(str.charAt(0), str.charAt(1));
        for (int i = 2; i < str.length(); i++) {
            result = computeNumericId(result, str.charAt(i));
        }
        return result;
    }
}
