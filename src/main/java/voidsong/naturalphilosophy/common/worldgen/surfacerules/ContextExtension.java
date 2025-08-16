package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import net.minecraft.world.level.levelgen.SurfaceRules;

public interface ContextExtension {
    // Rule return functions for cached LazyXZCondition rules
    SurfaceRules.Condition naturalphilosophy$getCliff();
    SurfaceRules.Condition naturalphilosophy$getFlat();
    SurfaceRules.Condition naturalphilosophy$getFlatLiquid();
    SurfaceRules.Condition naturalphilosophy$getLandTopLayer();
    // Value return functions for cached parameterized rules & conditions
    int naturalphilosophy$getOceanHeightmapDepth();
}
