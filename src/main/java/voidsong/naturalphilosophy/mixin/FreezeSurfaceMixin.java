package voidsong.naturalphilosophy.mixin;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("unused")
@Mixin(SnowAndFreezeFeature.class)
public abstract class FreezeSurfaceMixin {

    @ModifyArg(method = "place", at = @At(value = "INVOKE", target = "net/minecraft/world/level/WorldGenLevel.getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"), index = 0)
    private Heightmap.Types useCorrectHeightmapType(Heightmap.Types original, int koriginal, int loriginal) {
        return original == Heightmap.Types.MOTION_BLOCKING ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : original;
    }
}

