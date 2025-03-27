package voidsong.naturalphilosophy.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.ContextExtension;

import java.util.function.Function;

@SuppressWarnings("unused")
@Mixin(SurfaceRules.class)
public abstract class SurfaceRulesMixin {

    @Mixin(SurfaceRules.ConditionSource.class)
    public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
        @Inject(method = "bootstrap", at = @At("HEAD"))
        private static void onBootstrap(
            Registry<MapCodec<? extends SurfaceRules.ConditionSource>> pRegistry,
            CallbackInfoReturnable<Codec<SurfaceRules.ConditionSource>> cir) {
            SurfaceRules.register(pRegistry, "naturalphilosophy:cliff", NPSurfaceRules.Cliff.CODEC);
            SurfaceRules.register(pRegistry, "naturalphilosophy:cliff_lip", NPSurfaceRules.CliffLip.CODEC);
            SurfaceRules.register(pRegistry, "naturalphilosophy:flat", NPSurfaceRules.Flat.CODEC);
        }
    }

    @Mixin(SurfaceRules.Context.class)
    protected static final class Context implements ContextExtension {
        @Unique
        SurfaceRules.Condition cliff, flat, cliffLip;

        @Inject(method="<init>", at=@At("RETURN"))
        public void instantiateConditions(SurfaceSystem system,
                                          RandomState randomState,
                                          ChunkAccess chunk,
                                          NoiseChunk noiseChunk,
                                          Function<BlockPos, Holder<Biome>> biomeGetter,
                                          Registry<Biome> p_224621_,
                                          WorldGenerationContext context,
                                          CallbackInfo ci) {
            SurfaceRules.Context self = (SurfaceRules.Context) (Object) this;
            cliff = new NPSurfaceRules.CliffMaterialCondition(self);
            cliffLip = new NPSurfaceRules.CliffLipMaterialCondition(self);
            flat = new NPSurfaceRules.FlatMaterialCondition(self);
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getCliff() {
            return cliff;
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getCliffLip() {
            return cliffLip;
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getFlat() {
            return flat;
        }
    }

}
