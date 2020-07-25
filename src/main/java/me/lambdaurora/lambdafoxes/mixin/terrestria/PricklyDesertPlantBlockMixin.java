package me.lambdaurora.lambdafoxes.mixin.terrestria;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com.terraformersmc.terrestria.block.PricklyDesertPlantBlock")
public class PricklyDesertPlantBlockMixin extends Block
{
    public PricklyDesertPlantBlockMixin(Settings settings)
    {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        // Foxes don't get hurt by berry bushes, why would fennec foxes get hurt by tiny cactus?
        if (entity.getType() != EntityType.FOX)
            entity.damage(DamageSource.CACTUS, 1.0F);
    }
}
