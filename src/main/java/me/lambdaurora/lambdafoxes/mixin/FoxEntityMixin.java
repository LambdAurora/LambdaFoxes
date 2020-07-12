/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.registry.FoxType;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

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
    private void onInit(EntityType<? extends FoxEntity> entityType, World world, CallbackInfo ci)
    {
        this.trustLevel = this.random.nextInt(2);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(CallbackInfo ci)
    {
        //this.dataTracker.startTracking(LambdaFoxesRegistry.FOX_TRUST_LEVEL, 0);
    }

    @Inject(
            method = "initialize",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/FoxEntity;setType(Lnet/minecraft/entity/passive/FoxEntity$Type;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void onInitialize(WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CompoundTag entityTag, CallbackInfoReturnable<EntityData> cir)
    {
        // Me: Can I have registry?
        // Mojang: no, we already have that at home
        // Registry at home: *is enums*
        this.setFoxType(FoxType.rollFoxType(this.random, world.getBiome(this.getBlockPos())));
    }

    @Inject(method = "createChild", at = @At("TAIL"), cancellable = true)
    private void onCreateChild(@NotNull PassiveEntity mate, CallbackInfoReturnable<FoxEntity> cir)
    {
        FoxEntity child = cir.getReturnValue();

        List<FoxType> roll = new ArrayList<>();

        roll.add(FoxType.rollFoxType(this.random, this.getFoxType(), false));
        if (this.getFoxType() != ((LambdaFoxEntity) mate).getFoxType())
            roll.add(FoxType.rollFoxType(this.random, ((LambdaFoxEntity) mate).getFoxType(), false));

        FoxType rolled = FoxType.rollFoxType(this.random, roll);
        if (rolled == null)
            rolled = this.getFoxType();
        ((LambdaFoxEntity) child).setFoxType(rolled);

        cir.setReturnValue(child);
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
