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
import net.minecraft.entity.EntityType;
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

    public static final FoxType RED  = new FoxType(new Identifier("minecraft", "red"), 0, 8, null, true, 1.0F, EntityType.FOX, false, false)
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
    public static final FoxType SNOW = new FoxType(new Identifier("minecraft", "snow"), 1, 8, null, true, 1.0F, EntityType.FOX, false, false)
    {
        @Override
        public String getKey()
        {
            return this.getName();
        }
    };

    public static final FoxType SILVER   = new Builder(LambdaFoxes.id("silver"), 4, true).inherits(RED).register();
    public static final FoxType CROSS    = new Builder(LambdaFoxes.id("cross"), 5, true).inherits(RED).register();
    public static final FoxType PLATINUM = new Builder(LambdaFoxes.id("platinum"), 3, true).inherits(RED).register();
    public static final FoxType MARBLE   = new Builder(LambdaFoxes.id("marble"), 2, false).inherits(RED).register();
    public static final FoxType WHITE    = new Builder(LambdaFoxes.id("white"), 2, true).inherits(RED).register();

    public static final FoxType FENNEC = new Builder(LambdaFoxes.id("fennec"), 8, true).scaleFactor(0.8f).bigEars(true).register();

    private final Identifier                      id;
    private final int                             numericId;
    public final  int                             weight;
    public final  Optional<FoxType>               inherited;
    public final  boolean                         natural;
    public final  Tag.Identified<Biome>           biomes;
    public final  float                           scaleFactor;
    public final  EntityType<? extends FoxEntity> limitedTo;
    public final  boolean                         bigEars;
    public final  boolean                         fireImmune;

    private FoxType(@NotNull Identifier id, int numericId, int weight, @Nullable FoxType inherited, boolean natural, float scaleFactor, @NotNull EntityType<? extends FoxEntity> limitedTo, boolean bigEars, boolean fireImmune)
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

        this.scaleFactor = scaleFactor;
        this.limitedTo = limitedTo;
        this.bigEars = bigEars;
        this.fireImmune = fireImmune;

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
     * Returns whether this fox type is compatible with the specified entity type.
     *
     * @param type The entity type.
     * @return True if this fox type is compatible with the entity type.
     */
    public boolean isCompatible(EntityType<? extends FoxEntity> type)
    {
        return this.limitedTo.equals(type);
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
        List<FoxType> types = TYPES.stream().filter(type -> !type.inherited.isPresent() && type.biomes.contains(biome)).collect(Collectors.toList());
        if (types.size() == 0)
            return rollFoxType(random, RED, true);

        return rollFoxType(random, rollFoxType(random, types), true);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull FoxType parent, boolean natural)
    {
        List<FoxType> types = TYPES.stream()
                .filter(type -> {
                    if (type == parent)
                        return true;
                    if (natural && !type.natural)
                        return false;
                    return type.inherited.isPresent() && type.inherited.get() == parent;
                })
                .collect(Collectors.toList());
        return rollFoxType(random, types, parent);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull List<FoxType> types)
    {
        return rollFoxType(random, types, RED);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull List<FoxType> types, @NotNull FoxType defaultType)
    {
        if (types.size() == 0)
            return defaultType;

        int sum = 0;
        for (FoxType type : types) {
            sum += type.getWeight();
        }

        int r = random.nextInt() % sum;

        FoxType rolled;
        int acc = 0;
        Iterator<FoxType> iterator = types.iterator();
        while ((rolled = iterator.next()) != null) {
            acc += rolled.getWeight();
            if (r < acc) break;
        }
        if (rolled == null)
            rolled = defaultType;
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
        return TYPES.stream()
                .filter(entry -> entry.getIdentifier().equals(id))
                .findAny().orElse(RED);
    }

    /**
     * Returns the fox type associated to the identifier.
     *
     * @param id The identifier.
     * @return The associated fox type.
     */
    public static @NotNull FoxType fromId(@NotNull net.minecraft.util.Identifier id)
    {
        return fromId(new Identifier(id.getNamespace(), id.getPath()));
    }

    /**
     * Returns the fox type associated to the numeric id.
     *
     * @param id The numeric id.
     * @return The associated fox type.
     */
    public static @NotNull FoxType fromNumericId(int id)
    {
        return TYPES.stream()
                .filter(entry -> entry.getNumericId() == id)
                .findAny().orElse(RED);
    }

    public static @NotNull Optional<FoxType> findFirstCompatible(@NotNull EntityType<? extends FoxEntity> entityType)
    {
        return TYPES.stream().filter(type -> type.isCompatible(entityType)).findFirst();
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

    public static class Builder
    {
        private final @NotNull Identifier                      id;
        private final          int                             weight;
        private final          boolean                         natural;
        private @Nullable      FoxType                         inherited;
        private                float                           scaleFactor = 1.0F;
        private @NotNull       EntityType<? extends FoxEntity> limitedTo   = EntityType.FOX;
        private                boolean                         bigEars     = false;
        private                boolean                         fireImmune  = false;

        public Builder(@NotNull net.minecraft.util.Identifier id, int weight, boolean natural)
        {
            this(new Identifier(id.getNamespace(), id.getPath()), weight, natural);
        }

        public Builder(@NotNull Identifier id, int weight, boolean natural)
        {
            this.id = id;
            this.weight = weight;
            this.natural = natural;
        }

        /**
         * Makes the fox type inherits another fox type.
         *
         * @param type The type to inherit.
         * @return The builder instance.
         */
        public Builder inherits(@Nullable FoxType type)
        {
            this.inherited = type;
            return this;
        }

        /**
         * Sets a specific scale factor for this fox type.
         *
         * @param scaleFactor The scale factor.
         * @return The builder instance.
         */
        public Builder scaleFactor(float scaleFactor)
        {
            this.scaleFactor = scaleFactor;
            return this;
        }

        public Builder limitedTo(@NotNull EntityType<? extends FoxEntity> type)
        {
            this.limitedTo = type;
            return this;
        }

        /**
         * Sets whether this fox type should have big ears or not.
         *
         * @param bigEars True if this fox type has big ears, else false.
         * @return The builder instance.
         */
        public Builder bigEars(boolean bigEars)
        {
            this.bigEars = bigEars;
            return this;
        }

        /**
         * Sets whether this fox type is fire immune or not.
         *
         * @param fireImmune True if this fox type is fire immune, else false.
         * @return The builder instance.
         */
        public Builder fireImmune(boolean fireImmune)
        {
            this.fireImmune = fireImmune;
            return this;
        }

        /**
         * Registers the fox type.
         *
         * @return The instance of the fox type.
         */
        public FoxType register()
        {
            int numericId = computeNumericId(id);
            if (numericId == 0 || numericId == 1)
                throw new IllegalStateException("FoxType `" + id.toString() + "` is invalid, cannot compute valid numeric id.");

            return new FoxType(this.id, numericId, this.weight, this.inherited, this.natural, this.scaleFactor, this.limitedTo, this.bigEars, this.fireImmune);
        }
    }
}
