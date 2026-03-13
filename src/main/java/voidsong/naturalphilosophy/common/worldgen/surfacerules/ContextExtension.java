package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface ContextExtension {
    // Rule return functions for cached LazyXZCondition rules
    SurfaceRules.Condition naturalphilosophy$getCliff();
    SurfaceRules.Condition naturalphilosophy$getFlat();
    SurfaceRules.Condition naturalphilosophy$getFlatLiquid();
    // Value return functions for cached parameterized rules & conditions
    double naturalphilosophy$getCachedNoiseValue(ResourceKey<NormalNoise.NoiseParameters> noise, int x, int z);
}
