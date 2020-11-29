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

package me.lambdaurora.lambdafoxes.registry;

import me.lambdaurora.lambdafoxes.LambdaFoxes;
import me.lambdaurora.lambdafoxes.tag.BiomeTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.aperlambda.lambdacommon.utils.Nameable;
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
public class FoxType implements Nameable
{
    private static final List<FoxType> TYPES = new ArrayList<>();

    /* Default Minecraft fox types */

    public static final FoxType RED = new FoxType(new Identifier("minecraft", "red"), 0, 8, null, true, 1.0F, EntityType.FOX, false, false)
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

    public static final FoxType SILVER = new Builder(LambdaFoxes.id("silver"), 4, true).inherits(RED).register();
    public static final FoxType CROSS = new Builder(LambdaFoxes.id("cross"), 5, true).inherits(RED).register();
    public static final FoxType PLATINUM = new Builder(LambdaFoxes.id("platinum"), 3, true).inherits(RED).register();
    public static final FoxType MARBLE = new Builder(LambdaFoxes.id("marble"), 2, false).inherits(RED).register();
    public static final FoxType WHITE = new Builder(LambdaFoxes.id("white"), 2, true).inherits(RED).register();

    public static final FoxType FENNEC = new Builder(LambdaFoxes.id("fennec"), 8, true).scaleFactor(0.8f).bigEars().register();

    private final Identifier id;
    private final int numericId;
    public final int weight;
    public final Optional<FoxType> inherited;
    public final boolean natural;
    public final Tag.Identified<Biome> biomes;
    public final float scaleFactor;
    public final EntityType<? extends FoxEntity> limitedTo;
    public final boolean bigEars;
    public final boolean fireImmune;

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
            this.biomes = BiomeTags.register(new Identifier(biomeNamespace, "fox_spawn/" + id.getPath()));
        }

        this.scaleFactor = scaleFactor;
        this.limitedTo = limitedTo;
        this.bigEars = bigEars;
        this.fireImmune = fireImmune;

        TYPES.add(this);
    }

    public @NotNull Identifier getIdentifier()
    {
        return this.id;
    }

    @Override
    public @NotNull String getName()
    {
        return this.id.getPath();
    }

    /**
     * Returns the fox type key.
     *
     * @return the fox type key
     */
    public String getKey()
    {
        return this.id.toString();
    }

    /**
     * Returns the numeric id of this fox type.
     *
     * @return the fox type numeric id
     */
    public int getNumericId()
    {
        return this.numericId;
    }

    /**
     * Returns the probability weight. Greater is the weight, more chances there is for that type to spawn.
     *
     * @return the probability weight
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * Returns whether this fox type is a mutated variation.
     *
     * @return {@code true} if this fox type is a mutated variation, else {@code false}
     */
    public boolean isMutated()
    {
        return this.inherited.isPresent();
    }

    /**
     * Returns whether this fox type is compatible with the specified entity type.
     *
     * @param type the entity type
     * @return true if this fox type is compatible with the entity type
     */
    public boolean isCompatible(EntityType<? extends FoxEntity> type)
    {
        return this.limitedTo.equals(type);
    }

    /**
     * Returns the texture identifier.
     *
     * @param entity the entity
     * @return the texture identifier
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
     * @param random random
     * @param biome the biome
     * @return the chosen fox type
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
     * @param id the string identifier
     * @return the associated fox type
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
     * @param id the identifier
     * @return the associated fox type
     */
    public static @NotNull FoxType fromId(@NotNull Identifier id)
    {
        return TYPES.stream()
                .filter(entry -> entry.getIdentifier().equals(id))
                .findAny().orElse(RED);
    }

    /**
     * Returns the fox type associated to the numeric id.
     *
     * @param id the numeric id
     * @return the associated fox type
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
        int sum = (a + b);
        return (sum * sum + 3 * a + b) / 2;
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
        private final @NotNull Identifier id;
        private final int weight;
        private final boolean natural;
        private @Nullable FoxType inherited;
        private float scaleFactor = 1.0F;
        private @NotNull EntityType<? extends FoxEntity> limitedTo = EntityType.FOX;
        private boolean bigEars = false;
        private boolean fireImmune = false;

        public Builder(@NotNull Identifier id, int weight, boolean natural)
        {
            this.id = id;
            this.weight = weight;
            this.natural = natural;
        }

        /**
         * Makes the fox type inherits another fox type.
         *
         * @param type the type to inherit
         * @return the builder instance
         */
        public Builder inherits(@Nullable FoxType type)
        {
            this.inherited = type;
            return this;
        }

        /**
         * Sets a specific scale factor for this fox type.
         *
         * @param scaleFactor the scale factor
         * @return the builder instance
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
         * Sets big ears for this fox.
         *
         * @return the builder instance
         */
        public Builder bigEars()
        {
            this.bigEars = true;
            return this;
        }

        /**
         * Sets whether this fox type is fire immune or not.
         *
         * @param fireImmune {@code true} if this fox type is fire immune, else {@code false}
         * @return the builder instance
         */
        public Builder fireImmune(boolean fireImmune)
        {
            this.fireImmune = fireImmune;
            return this;
        }

        /**
         * Registers the fox type.
         *
         * @return the instance of the fox type
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
