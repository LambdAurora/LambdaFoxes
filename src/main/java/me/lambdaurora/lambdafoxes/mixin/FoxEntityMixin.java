/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.registry.FoxType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity implements LambdaFoxEntity
{
    @Shadow
    @Final
    private static TrackedData<Integer> TYPE;

    private int trustLevel;

    protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(EntityType<? extends FoxEntity> entityType, World world, CallbackInfo ci) {
        this.trustLevel = this.random.nextInt(2);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(CallbackInfo ci)
    {
        //this.dataTracker.startTracking(LambdaFoxesRegistry.FOX_TRUST_LEVEL, 0);
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void onWriteCustomDataToTag(CompoundTag tag, CallbackInfo ci)
    {
        tag.putString("Type", this.getFoxType().getKey());
        tag.putInt("TrustLevel", this.getTrustLevel());
    }

    @Inject(method = "readCustomDataFromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;setCrouching(Z)V", shift = At.Shift.AFTER))
    private void onReadCustomDataTag(CompoundTag tag, CallbackInfo ci)
    {
        this.setFoxType(FoxType.fromId(tag.getString("Type")));
        this.setTrustLevel(tag.getInt("TrustLevel"));
    }

    @Override
    public FoxType getFoxType()
    {
        return FoxType.fromNumericId(this.dataTracker.get(TYPE));
    }

    @Override
    public void setFoxType(@NotNull FoxType type)
    {
        this.dataTracker.set(TYPE, type.getNumericId());
    }

    @Override
    public int getTrustLevel()
    {
        return this.trustLevel;
    }

    @Override
    public void setTrustLevel(int trustLevel)
    {
        this.trustLevel = MathHelper.clamp(trustLevel, 0, 4);
    }

    @Override
    public int getMaxTrustLevel()
    {
        return 4;
    }
}
