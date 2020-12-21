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

package me.lambdaurora.lambdafoxes.mixin;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import me.lambdaurora.lambdafoxes.entity.ai.goal.FollowTrustedOwnerGoal;
import me.lambdaurora.lambdafoxes.entity.ai.goal.FoxAttackWithOwnerGoal;
import me.lambdaurora.lambdafoxes.entity.ai.goal.FoxSitGoal;
import me.lambdaurora.lambdafoxes.item.FoxArmorItem;
import me.lambdaurora.lambdafoxes.registry.FoxType;
import me.lambdaurora.lambdafoxes.registry.LambdaFoxesRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.aperlambda.lambdacommon.utils.function.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity implements LambdaFoxEntity {
    @Shadow
    @Final
    private static TrackedData<Integer> TYPE;

    @Shadow
    @Final
    private static TrackedData<Optional<UUID>> OWNER;

    @Shadow
    public abstract boolean isSleeping();

    @Shadow
    protected abstract void setSleeping(boolean sleeping);

    @Shadow
    protected abstract void stopActions();

    @Shadow
    public abstract void setTarget(LivingEntity target);

    @Shadow
    protected abstract void setAggressive(boolean aggressive);

    @Shadow
    public abstract boolean isSitting();

    @Shadow
    protected abstract void spit(ItemStack stack);

    @Shadow
    public abstract boolean isChasing();

    @Shadow
    protected abstract boolean canTrust(UUID uuid);

    private boolean waiting;
    private float appreciation;

    protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(EntityType<? extends FoxEntity> entityType, World world, CallbackInfo ci) {
        this.appreciation = 0.f;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(LambdaFoxesRegistry.FOX_TRUST_LEVEL, this.random.nextInt(2));
        this.dataTracker.startTracking(LambdaFoxesRegistry.FOX_PET_STATUS, false);
        this.dataTracker.startTracking(LambdaFoxesRegistry.FOX_PET_COOLDOWN, 0);
    }

    @ModifyArg(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/FleeEntityGoal;<init>(Lnet/minecraft/entity/mob/PathAwareEntity;Ljava/lang/Class;FDDLjava/util/function/Predicate;)V", ordinal = 0), index = 5)
    private Predicate<LivingEntity> onInitPlayerFeelGoal(Predicate<LivingEntity> predicate) {
        return Predicates.and(predicate, livingEntity -> this.isWild());
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo ci) {
        this.goalSelector.add(1, new FoxSitGoal((FoxEntity) (Object) this));
        this.goalSelector.add(5, new FollowTrustedOwnerGoal((FoxEntity) (Object) this, 1.0D, 10.0F, 2.0F, false));
        this.targetSelector.add(4, new FoxAttackWithOwnerGoal(this));
    }

    @Inject(
            method = "initialize",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/FoxEntity;setType(Lnet/minecraft/entity/passive/FoxEntity$Type;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                              @Nullable EntityData entityData, @Nullable CompoundTag entityTag, CallbackInfoReturnable<EntityData> cir) {
        // Me: Can I have registry?
        // Mojang: no, we already have that at home
        // Registry at home: *is enums*
        this.setFoxType(FoxType.rollFoxType(this.random, world.getBiome(this.getBlockPos())));
    }

    @Inject(method = "createChild", at = @At("TAIL"), cancellable = true)
    private void onCreateChild(ServerWorld world, @NotNull PassiveEntity mate, CallbackInfoReturnable<FoxEntity> cir) {
        FoxEntity child = cir.getReturnValue();

        List<FoxType> roll = new ArrayList<>();

        roll.add(FoxType.rollFoxType(this.random, this.getFoxType(), false));
        if (this.getFoxType() != ((LambdaFoxEntity) mate).getFoxType())
            roll.add(FoxType.rollFoxType(this.random, ((LambdaFoxEntity) mate).getFoxType(), false));

        FoxType rolled = FoxType.rollFoxType(this.random, roll, this.getFoxType());
        ((LambdaFoxEntity) child).setFoxType(rolled);

        int trustLevel = 0;

        float maxAppreciation = Math.max(this.getAppreciation(), ((LambdaFoxEntity) mate).getAppreciation());
        if (maxAppreciation > .5f) {
            if (this.getAppreciation() > ((LambdaFoxEntity) mate).getAppreciation()) {
                trustLevel = this.getTrustLevel() + 1;
            } else {
                trustLevel = ((LambdaFoxEntity) mate).getTrustLevel();
            }
        } else {
            trustLevel = Math.max(this.getTrustLevel(), ((LambdaFoxEntity) mate).getTrustLevel());
        }

        ((LambdaFoxEntity) child).setTrustLevel(trustLevel);

        cir.setReturnValue(child);
    }

    @Inject(method = "addTypeSpecificGoals", at = @At("HEAD"), cancellable = true)
    private void onAddTypeSpecificGoals(CallbackInfo ci) {
        if (this.world.isClient()) {
            ci.cancel(); // Why this isn't done already?
            return;
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void onWriteCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        tag.putString("Type", this.getFoxType().getKey());
        tag.putInt("TrustLevel", this.getTrustLevel());
        tag.putBoolean("Waiting", this.isWaiting());
        tag.putFloat("Appreciation", this.appreciation);
    }

    @Inject(method = "readCustomDataFromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;setCrouching(Z)V", shift = At.Shift.AFTER))
    private void onReadCustomDataTag(CompoundTag tag, CallbackInfo ci) {
        this.setFoxType(FoxType.fromId(tag.getString("Type")));
        this.setTrustLevel(tag.getInt("TrustLevel"));
        this.setWaiting(tag.getBoolean("Waiting"));
        this.appreciation = tag.getFloat("Appreciation");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        int petCooldown = this.dataTracker.get(LambdaFoxesRegistry.FOX_PET_COOLDOWN);
        if (!this.getEntityWorld().isClient()) {
            if (petCooldown > 0) {
                petCooldown--;
                this.dataTracker.set(LambdaFoxesRegistry.FOX_PET_COOLDOWN, petCooldown);
                if (petCooldown == 0)
                    this.dataTracker.set(LambdaFoxesRegistry.FOX_PET_STATUS, false);
            }
        }
    }

    @Override
    public int getArmor() {
        int bonus = super.getArmor();

        ItemStack armor = this.getFoxArmor();
        if (armor.getItem() instanceof FoxArmorItem) {
            bonus = Math.max(((FoxArmorItem) armor.getItem()).bonus, bonus);
        }

        return bonus;
    }


    @Override
    public void setOnFireFor(int seconds) {
        if (this.getFoxType().isFireImmune())
            return;

        super.setOnFireFor(seconds);
    }

    @Override
    public boolean isFireImmune() {
        return this.getFoxType().isFireImmune() || super.isFireImmune();
    }

    @Override
    public @NotNull ActionResult interactMob(@NotNull PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        int petCooldown = this.dataTracker.get(LambdaFoxesRegistry.FOX_PET_COOLDOWN);

        if (this.world.isClient && !this.isWild()) {
            boolean hasItem = !(this.getFoxArmor().isEmpty() && this.getMainHandStack().isEmpty());

            // Pet the foxxo.
            if (this.canTrust(player.getUuid()) && player.isSneaking() && stack.isEmpty() && !hasItem && petCooldown == 0) {
                return ActionResult.SUCCESS;
            }

            if (this.isTamed() && this.isOwner(player)) {
                return ActionResult.CONSUME;
            } else if (item instanceof FoxArmorItem || (player.isSneaking() && stack.isEmpty() && hasItem)) {
                return ActionResult.CONSUME;
            } else if (item.isFood() && this.getHealth() < this.getMaxHealth()) {
                return ActionResult.CONSUME;
            } else {
                return !this.isBreedingItem(stack) || this.getHealth() >= this.getMaxHealth() && this.isTamed() ? ActionResult.PASS : ActionResult.SUCCESS;
            }
        }

        if (!this.isWild()) {
            if (!(item instanceof DyeItem)) {
                if (item.isFood() && this.getHealth() < this.getMaxHealth()) {
                    this.eat(player, hand, stack);
                    this.heal((float) item.getFoodComponent().getHunger());
                    return ActionResult.CONSUME;
                }

                if (item instanceof FoxArmorItem && !EnchantmentHelper.hasBindingCurse(this.getFoxArmor())) {
                    this.setFoxArmor(stack.split(1));
                    return ActionResult.CONSUME;
                }

                if (this.canTrust(player.getUuid()) && player.isSneaking() && stack.isEmpty()) {
                    if (this.isOwner(player) && (!this.getFoxArmor().isEmpty() || !this.getMainHandStack().isEmpty())) {
                        // First try the main hand.
                        boolean success = false;
                        if (!this.getMainHandStack().isEmpty()) {
                            this.spit(this.getMainHandStack());
                            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                            success = true;
                        } else {
                            if (!EnchantmentHelper.hasBindingCurse(this.getFoxArmor())) {
                                this.setFoxArmor(ItemStack.EMPTY);
                                success = true;
                            }
                        }
                        if (success)
                            return ActionResult.CONSUME;
                    } else if (petCooldown == 0) {
                        // Spawn particles
                        for (int i = 0; i < 7; ++i) {
                            final double offsetX = this.getRandom().nextGaussian() * 0.02;
                            final double offsetY = this.getRandom().nextGaussian() * 0.02;
                            final double offsetZ = this.getRandom().nextGaussian() * 0.02;
                            ((ServerWorld) this.getEntityWorld()).spawnParticles(
                                    ParticleTypes.HEART,
                                    this.getX() + this.getRandom().nextFloat() * this.getWidth() * 2.f - this.getWidth(),
                                    this.getEyeY() + this.getRandom().nextFloat() * this.getHeight() * .5f,
                                    this.getZ() + this.getRandom().nextFloat() * this.getWidth() * 2.f - this.getWidth(),
                                    1,
                                    offsetX, offsetY, offsetZ,
                                    0.0
                            );
                        }
                        this.dataTracker.set(LambdaFoxesRegistry.FOX_PET_STATUS, true);
                        this.dataTracker.set(LambdaFoxesRegistry.FOX_PET_COOLDOWN, 150);
                        return ActionResult.CONSUME;
                    }
                }

                if (this.isTamed()) {
                    ActionResult result = super.interactMob(player, hand);
                    if (!result.isAccepted() || this.isBaby()) {
                        this.setWaiting(!this.isWaiting());
                        this.jumping = false;
                        this.setTarget(null);
                        return ActionResult.SUCCESS;
                    }

                    return result;
                }
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        if (this.isTamed()) {
            Optional<LivingEntity> livingEntity = this.getOwner();
            if (livingEntity.isPresent()) {
                return livingEntity.get().getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (this.isTamed()) {
            Optional<LivingEntity> livingEntity = this.getOwner();

            if (livingEntity.isPresent()) {
                return other == livingEntity.get() || livingEntity.get().isTeammate(other);
            }
        }

        return super.isTeammate(other);
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.world.isClient && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getTrustLevel() >= this.getMaxTrustLevel() - 1) {
            this.getOwner().filter(owner -> owner instanceof ServerPlayerEntity).ifPresent(owner -> owner.sendSystemMessage(this.getDamageTracker().getDeathMessage(), Util.NIL_UUID));
        }

        super.onDeath(source);
    }

    @Override
    public FoxType getFoxType() {
        return FoxType.fromNumericId(this.dataTracker.get(TYPE));
    }

    @Override
    public void setFoxType(@NotNull FoxType type) {
        this.dataTracker.set(TYPE, type.getNumericId());
    }

    @Override
    public void setFoxSleeping(boolean sleeping) {
        this.setSleeping(sleeping);
    }

    @Override
    public void setFoxAggressive(boolean aggressive) {
        this.setAggressive(aggressive);
    }

    @Override
    public @NotNull ItemStack getFoxArmor() {
        return this.getEquippedStack(EquipmentSlot.CHEST);
    }

    @Override
    public void setFoxArmor(@NotNull ItemStack stack) {
        ItemStack oldArmor = this.getFoxArmor();
        if (!oldArmor.isEmpty()) {
            ItemEntity itemEntity = this.dropStack(oldArmor);
            if (itemEntity != null)
                itemEntity.setPickupDelay(40);
        }

        this.equipStack(EquipmentSlot.CHEST, stack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    @Override
    public void stopFoxActions() {
        this.stopActions();
    }

    @Override
    public float getTailAngle() {
        if (this.isSleeping())
            return this.isBaby() ? -2.1816616F : -2.6179938F;
        if (this.isSitting())
            return 0.7853982F;
        return !this.isWild() ? (-0.05235988F - (this.getMaxHealth() - this.getHealth()) * 0.02F) : -0.05235988F;
    }

    @Override
    public float getAppreciation() {
        return this.appreciation;
    }

    @Override
    public void setAppreciation(float appreciation) {
        this.appreciation = appreciation;
    }

    @Override
    public int getTrustLevel() {
        return this.dataTracker.get(LambdaFoxesRegistry.FOX_TRUST_LEVEL);
    }

    @Override
    public void setTrustLevel(int trustLevel) {
        trustLevel = MathHelper.clamp(trustLevel, 0, this.getMaxTrustLevel());
        this.dataTracker.set(LambdaFoxesRegistry.FOX_TRUST_LEVEL, trustLevel);

        if (trustLevel >= this.getMaxTrustLevel()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            this.setHealth(20.0f);
        } else if (trustLevel >= this.getMaxTrustLevel() - 1) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
            this.setHealth(15.0f);
        } else
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
    }

    @Override
    public int getMaxTrustLevel() {
        return 3;
    }

    @Inject(method = "canTrust", at = @At("RETURN"), cancellable = true)
    private void onCanTrust(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() && this.getTrustLevel() > this.getMaxTrustLevel() - 2);
    }

    @Override
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    @Override
    public boolean isWaiting() {
        return this.waiting;
    }

    @Override
    public boolean canAttackWithOwner(@NotNull LivingEntity target, @NotNull LivingEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof FoxEntity) {
                return false; // Foxes don't attack foxes.
            } else if (target instanceof WolfEntity) {
                WolfEntity wolf = (WolfEntity) target;
                return !wolf.isTamed() || wolf.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
                return false;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity) target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
            }
        }
        return false;
    }

    @Override
    public @NotNull Optional<UUID> getOwnerUuid() {
        return this.dataTracker.get(OWNER);
    }

    @Override
    public @NotNull Optional<LivingEntity> getOwner() {
        return this.getOwnerUuid().map(uuid -> this.world.getPlayerByUuid(uuid));
    }

    @Override
    public boolean canAnimatePet() {
        return this.isBeingPet() && !(this.isSleeping() || this.isChasing());
    }

    @Override
    public boolean isBeingPet() {
        return this.dataTracker.get(LambdaFoxesRegistry.FOX_PET_STATUS);
    }
}
