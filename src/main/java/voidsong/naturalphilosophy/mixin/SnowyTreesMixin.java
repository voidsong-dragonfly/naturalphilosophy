package voidsong.naturalphilosophy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Inspired by code from Apollounknowndev in Simple Snowy Fix, and updated to 1.20.4
 * Cleaned up with help from wiresegal re: LocalCapture vs @Local
 */
@SuppressWarnings("unused")
@Mixin(TreeFeature.class)
public class SnowyTreesMixin {
    @Inject(
        method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z",
        at = @At(value= "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;encapsulatingPositions(Ljava/lang/Iterable;)Ljava/util/Optional;", shift = At.Shift.BEFORE)
    )
    private void snowOnLeaves(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir, @Local(name = "worldgenlevel") WorldGenLevel level, @Local(name = "set2") Set<BlockPos> set2) {
        for (BlockPos leaves : set2) {
            BlockPos top = leaves.above();
            if (level instanceof WorldGenRegion && level.isEmptyBlock(top) && !level.isEmptyBlock(leaves) && level.getBiome(top).value().shouldSnow(level, top)) {
                level.setBlock(top, Blocks.SNOW.defaultBlockState(), 2);
            }
        }
    }
}