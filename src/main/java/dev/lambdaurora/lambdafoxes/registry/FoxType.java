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

package dev.lambdaurora.lambdafoxes.registry;

import dev.lambdaurora.lambdafoxes.LambdaFoxes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a fox type.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FoxType {
    private static final List<FoxType> TYPES = new ArrayList<>();

    /* Default Minecraft fox types */

    public static final Identifier BASE_FOX_MODEL = LambdaFoxes.id("fox");

    public static final FoxType RED = new FoxType(new Identifier("minecraft", "red"), 0,
            8, null, true, 1.f, EntityType.FOX, false,
            BASE_FOX_MODEL) {
        @Override
        public String getKey() {
            return this.getName();
        }

        @Override
        public net.minecraft.util.Identifier getTextureId(@NotNull FoxEntity entity) {
            return entity.isSleeping() ? new net.minecraft.util.Identifier("textures/entity/fox/fox_sleep.png")
                    : new net.minecraft.util.Identifier("textures/entity/fox/fox.png");
        }
    };
    public static final FoxType SNOW = new FoxType(new Identifier("minecraft", "snow"), 1,
            8, null, true, 1.f, EntityType.FOX, false,
            BASE_FOX_MODEL) {
        @Override
        public String getKey() {
            return this.getName();
        }
    };

    public static final FoxType SILVER = new Builder(LambdaFoxes.id("silver"), 4, true).inherits(RED).register();
    public static final FoxType CROSS = new Builder(LambdaFoxes.id("cross"), 5, true).inherits(RED).register();
    public static final FoxType PLATINUM = new Builder(LambdaFoxes.id("platinum"), 3, true).inherits(RED).register();
    public static final FoxType MARBLE = new Builder(LambdaFoxes.id("marble"), 2, false).inherits(RED).register();
    public static final FoxType WHITE = new Builder(LambdaFoxes.id("white"), 2, true).inherits(RED).register();

    public static final FoxType FENNEC = new Builder(LambdaFoxes.id("fennec"), 8, true)
            .scaleFactor(.8f).register();

    private final Identifier id;
    private final int numericId;
    private final int weight;
    private final Optional<FoxType> inherited;
    private final boolean natural;
    private final Tag.Identified<Biome> biomes;
    private final float scaleFactor;
    private final EntityType<? extends FoxEntity> limitedTo;
    private final boolean fireImmune;

    private final Identifier modelId;

    @Environment(EnvType.CLIENT)
    private EntityModelLayer model;
    @Environment(EnvType.CLIENT)
    private EntityModelLayer armorModel;

    private FoxType(@NotNull Identifier id, int numericId,
                    int weight,
                    @Nullable FoxType inherited, boolean natural,
                    float scaleFactor,
                    @NotNull EntityType<? extends FoxEntity> limitedTo,
                    boolean fireImmune,
                    Identifier modelId) {
        this.id = id;
        this.numericId = numericId;
        this.weight = Math.max(0, weight);
        this.inherited = Optional.ofNullable(inherited);
        this.natural = natural;

        if (inherited != null) {
            this.biomes = inherited.biomes;
            this.modelId = inherited.modelId;
        } else {
            var biomeTagNamespace = id.getNamespace();
            if (biomeTagNamespace.equalsIgnoreCase("minecraft"))
                biomeTagNamespace = LambdaFoxes.NAMESPACE;
            this.biomes = TagFactory.BIOME.create(new Identifier(biomeTagNamespace, "fox_spawn/" + id.getPath()));
            this.modelId = modelId;
        }

        this.scaleFactor = scaleFactor;
        this.limitedTo = limitedTo;
        this.fireImmune = fireImmune;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            this.initClient();

        TYPES.add(this);
    }

    @Environment(EnvType.CLIENT)
    private void initClient() {
        this.model = new EntityModelLayer(this.modelId, "main");
        this.armorModel = new EntityModelLayer(this.modelId, "armor");
    }

    public @NotNull Identifier getIdentifier() {
        return this.id;
    }

    public @NotNull String getName() {
        return this.id.getPath();
    }

    /**
     * Returns the fox type key.
     *
     * @return the fox type key
     */
    public String getKey() {
        return this.id.toString();
    }

    /**
     * Returns the numeric id of this fox type.
     *
     * @return the fox type numeric id
     */
    public int getNumericId() {
        return this.numericId;
    }

    /**
     * Returns the probability weight. Greater is the weight, more chances there is for that type to spawn.
     *
     * @return the probability weight
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Returns whether this fox type is a mutated variation.
     *
     * @return {@code true} if this fox type is a mutated variation, else {@code false}
     */
    public boolean isMutated() {
        return this.inherited.isPresent();
    }

    /**
     * Gets the scale factor of this fox type.
     *
     * @return the scale factor
     */
    public float getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * Returns whether this fox type is compatible with the specified entity type.
     *
     * @param type the entity type
     * @return true if this fox type is compatible with the entity type
     */
    public boolean isCompatible(EntityType<? extends FoxEntity> type) {
        return this.limitedTo.equals(type);
    }

    /**
     * Returns whether this fox type is fire immune or not.
     *
     * @return {@code true} if this fox type is fire immune, else {@code false}
     */
    public boolean isFireImmune() {
        return this.fireImmune;
    }

    /**
     * Returns the texture identifier.
     *
     * @param entity the entity
     * @return the texture identifier
     */
    @Environment(EnvType.CLIENT)
    public Identifier getTextureId(@NotNull FoxEntity entity) {
        var base = this.getName() + "_fox";
        if (entity.isSleeping())
            base += "_sleep";
        return new Identifier(this.id.getNamespace(), "textures/entity/fox/" + base + ".png");
    }

    @Environment(EnvType.CLIENT)
    public Identifier getModelId() {
        return this.modelId;
    }

    @Environment(EnvType.CLIENT)
    public EntityModelLayer getModel() {
        return this.model;
    }

    @Environment(EnvType.CLIENT)
    public EntityModelLayer getArmorModel() {
        return this.armorModel;
    }

    /**
     * Rolls the fox type for the specified biome.
     *
     * @param random random
     * @param biome the biome
     * @return the chosen fox type
     */
    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull Biome biome) {
        var types = TYPES.stream().filter(type -> type.inherited.isEmpty() && type.biomes.contains(biome)).collect(Collectors.toList());
        if (types.size() == 0)
            return rollFoxType(random, RED, true);

        return rollFoxType(random, rollFoxType(random, types), true);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull FoxType parent, boolean natural) {
        var types = TYPES.stream()
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

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull List<FoxType> types) {
        return rollFoxType(random, types, RED);
    }

    public static @NotNull FoxType rollFoxType(@NotNull Random random, @NotNull List<FoxType> types, @NotNull FoxType defaultType) {
        if (types.size() == 0)
            return defaultType;

        int sum = 0;
        for (var type : types) {
            sum += type.getWeight();
        }

        int r = random.nextInt() % sum;

        FoxType rolled;
        int acc = 0;
        var iterator = types.iterator();
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
    public static @NotNull FoxType fromId(@NotNull String id) {
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
    public static @NotNull FoxType fromId(@NotNull Identifier id) {
        return TYPES.stream()
                .filter(entry -> entry.getIdentifier().equals(id))
                .findAny().orElse(RED);
    }

    public static Stream<FoxType> stream() {
        return TYPES.stream();
    }

    @Environment(EnvType.CLIENT)
    public static Stream<Identifier> getModels() {
        return TYPES.stream().map(FoxType::getModelId).distinct();
    }

    /**
     * Returns the fox type associated to the numeric id.
     *
     * @param id the numeric id
     * @return the associated fox type
     */
    public static @NotNull FoxType fromNumericId(int id) {
        return TYPES.stream()
                .filter(entry -> entry.getNumericId() == id)
                .findAny().orElse(RED);
    }

    public static @NotNull Optional<FoxType> findFirstCompatible(@NotNull EntityType<? extends FoxEntity> entityType) {
        return TYPES.stream().filter(type -> type.isCompatible(entityType)).findFirst();
    }

    private static int computeNumericId(int a, int b) {
        int sum = (a + b);
        return (sum * sum + 3 * a + b) / 2;
    }

    private static int computeNumericId(@NotNull Identifier id) {
        String str = id.toString();
        if (str.length() < 2)
            return 0;
        int result = computeNumericId(str.charAt(0), str.charAt(1));
        for (int i = 2; i < str.length(); i++) {
            result = computeNumericId(result, str.charAt(i));
        }
        return result;
    }

    public static class Builder {
        private final @NotNull Identifier id;
        private final int weight;
        private final boolean natural;
        private @Nullable FoxType inherited;
        private float scaleFactor = 1.0F;
        private @NotNull EntityType<? extends FoxEntity> limitedTo = EntityType.FOX;
        private Identifier model;
        private boolean fireImmune = false;

        public Builder(@NotNull Identifier id, int weight, boolean natural) {
            this.id = id;
            this.weight = weight;
            this.natural = natural;
            this.model = id;
        }

        /**
         * Makes the fox type inherits another fox type.
         *
         * @param type the type to inherit
         * @return the builder instance
         */
        public Builder inherits(@Nullable FoxType type) {
            this.inherited = type;
            return this;
        }

        /**
         * Sets a specific scale factor for this fox type.
         *
         * @param scaleFactor the scale factor
         * @return the builder instance
         */
        public Builder scaleFactor(float scaleFactor) {
            this.scaleFactor = scaleFactor;
            return this;
        }

        public Builder limitedTo(@NotNull EntityType<? extends FoxEntity> type) {
            this.limitedTo = type;
            return this;
        }

        /**
         * Sets the model used for this fox.
         *
         * @return the builder instance
         */
        public Builder model(Identifier id) {
            this.model = id;
            return this;
        }

        /**
         * Sets whether this fox type is fire immune or not.
         *
         * @param fireImmune {@code true} if this fox type is fire immune, else {@code false}
         * @return the builder instance
         */
        public Builder fireImmune(boolean fireImmune) {
            this.fireImmune = fireImmune;
            return this;
        }

        /**
         * Registers the fox type.
         *
         * @return the instance of the fox type
         */
        public FoxType register() {
            int numericId = computeNumericId(id);
            if (numericId == 0 || numericId == 1)
                throw new IllegalStateException("FoxType `" + id + "` is invalid, cannot compute valid numeric id.");

            return new FoxType(this.id, numericId, this.weight, this.inherited, this.natural, this.scaleFactor, this.limitedTo, this.fireImmune, this.model);
        }
    }
}
