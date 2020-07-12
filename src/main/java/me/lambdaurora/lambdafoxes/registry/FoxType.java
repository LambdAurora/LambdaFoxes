/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.registry;

import me.lambdaurora.lambdafoxes.LambdaFoxes;
import me.lambdaurora.lambdafoxes.tag.BiomeTags;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.tag.Tag;
import net.minecraft.world.biome.Biome;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Identifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a fox type.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FoxType implements Identifiable
{
    private static final List<FoxType> TYPES = new ArrayList<>();

    /* Default Minecraft fox types */

    public static final FoxType RED  = new FoxType(new Identifier("minecraft", "red"), 0, 8, null, true)
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
    public static final FoxType SNOW = new FoxType(new Identifier("minecraft", "snow"), 1, 8, null, true)
    {
        @Override
        public String getKey()
        {
            return this.getName();
        }
    };

    public static final FoxType SILVER   = register(LambdaFoxes.id("silver"), 4, RED, true);
    public static final FoxType CROSS    = register(LambdaFoxes.id("cross"), 5, RED, true);
    public static final FoxType PLATINUM = register(LambdaFoxes.id("platinum"), 3, RED, true);
    public static final FoxType MARBLE   = register(LambdaFoxes.id("marble"), 2, RED, false);
    public static final FoxType WHITE    = register(LambdaFoxes.id("white"), 2, RED, true);

    private final Identifier            id;
    private final int                   numericId;
    public final  int                   weight;
    public final  Optional<FoxType>     inherited;
    public final  boolean               natural;
    public final  Tag.Identified<Biome> biomes;

    private FoxType(@NotNull Identifier id, int numericId, int weight, @Nullable FoxType inherited, boolean natural)
    {
        this.id = id;
        this.numericId = numericId;
        this.weight = Math.max(0, weight);
        this.inherited = Optional.ofNullable(inherited);
        this.natural = natural;

        if (inherited != null)
            this.biomes = inherited.biomes;
        else {
            String biomeNamespace = id.getNamespace();
            if (biomeNamespace.equalsIgnoreCase("minecraft"))
                biomeNamespace = LambdaFoxes.MODID;
            this.biomes = BiomeTags.register(new Identifier(biomeNamespace, "fox_spawn/" + id.getName()));
        }

        TYPES.add(this);
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
     * Returns the probability weight. Greater is the weight, more chances there is for that type to spawn.
     *
     * @return The probability weight.
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * Returns whether this fox type is a mutated variation.
     *
     * @return True if this fox type is a mutated variation, else false.
     */
    public boolean isMutated()
    {
        return this.inherited.isPresent();
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
     * Rolls the fox type for the specified biome.
     *
     * @param random Random.
     * @param biome  The biome.
     * @return The chosen fox type.
     */
    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull Biome biome)
    {
        List<FoxType> types = TYPES.parallelStream().filter(type -> !type.inherited.isPresent() && type.biomes.contains(biome)).collect(Collectors.toList());
        if (types.size() == 0)
            return rollFoxType(random, RED, true);

        FoxType rolled = rollFoxType(random, types);
        if (rolled == null)
            rolled = RED;
        return rollFoxType(random, rolled, true);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull FoxType parent, boolean natural)
    {
        List<FoxType> types = TYPES.parallelStream()
                .filter(type -> {
                    if (type == parent)
                        return true;
                    if (natural && !type.natural)
                        return false;
                    return type.inherited.isPresent() && type.inherited.get() == parent;
                })
                .collect(Collectors.toList());
        FoxType rolled = rollFoxType(random, types);
        if (rolled == null)
            rolled = RED;
        return rolled;
    }

    public static @Nullable FoxType rollFoxType(@NotNull Random random, @NotNull List<FoxType> types)
    {
        if (types.size() == 0)
            return RED;

        int sum = types.parallelStream().map(FoxType::getWeight).reduce(0, Integer::sum);

        int r = random.nextInt() % sum;

        FoxType rolled;
        int acc = 0;
        Iterator<FoxType> iterator = types.iterator();
        while ((rolled = iterator.next()) != null) {
            acc += rolled.getWeight();
            if (r < acc) break;
        }
        return rolled;
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
        return TYPES.parallelStream()
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
        return TYPES.parallelStream()
                .filter(entry -> entry.getNumericId() == id)
                .findAny().orElse(RED);
    }

    public static @NotNull FoxType register(@NotNull Identifier id, int weight, boolean natural)
    {
        return register(id, weight, null, natural);
    }

    public static @NotNull FoxType register(@NotNull Identifier id, int weight, @Nullable FoxType inherited, boolean natural)
    {
        int numericId = computeNumericId(id);
        if (numericId == 0 || numericId == 1)
            throw new IllegalStateException("FoxType `" + id.toString() + "` is invalid, cannot compute valid numeric id.");

        return new FoxType(id, numericId, weight, inherited, natural);
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
