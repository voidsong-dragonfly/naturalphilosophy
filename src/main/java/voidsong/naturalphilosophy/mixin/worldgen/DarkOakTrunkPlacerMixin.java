package voidsong.naturalphilosophy.mixin.worldgen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.NPBlocks;

@Mixin(DarkOakTrunkPlacer.class)
public class DarkOakTrunkPlacerMixin {
    @ModifyExpressionValue(
        method = "placeTrunk",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/TreeFeature;isAirOrLeaves(Lnet/minecraft/world/level/LevelSimulatedReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean darkOakCanReplaceTaggedBlocks(boolean original, @Local(argsOnly = true)LevelSimulatedReader level, @Local(name = "blockpos1") BlockPos pos) {
        return original || level.isStateAtPosition(pos, state -> state.is(BlockTags.REPLACEABLE_BY_TREES));
    }
}
