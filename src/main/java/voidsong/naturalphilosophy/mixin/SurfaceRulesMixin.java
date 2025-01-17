package voidsong.naturalphilosophy.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import voidsong.naturalphilosophy.common.surfacerules.CliffSurfaceRule;
import voidsong.naturalphilosophy.common.surfacerules.ContextExtension;

import java.util.function.Function;

@Mixin(SurfaceRules.class)
public abstract class SurfaceRulesMixin {

    @Mixin(SurfaceRules.ConditionSource.class)
    public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
        @Inject(method = "bootstrap", at = @At("HEAD"))
        private static void onBootstrap(
            Registry<Codec<? extends SurfaceRules.ConditionSource>> pRegistry,
            CallbackInfoReturnable<Codec<SurfaceRules.ConditionSource>> cir) {
            SurfaceRules.register(pRegistry, "cliff", CliffSurfaceRule.Cliff.CODEC);
        }
    }

    @SuppressWarnings("unused")
    @Mixin(SurfaceRules.Context.class)
    protected static final class Context implements ContextExtension {
        @SuppressWarnings("DataFlowIssue")
        @Unique
        final SurfaceRules.Condition naturalphilosophy$cliff = new CliffSurfaceRule.CliffMaterialCondition((SurfaceRules.Context)(Object)this);

        @Override
        public SurfaceRules.Condition naturalphilosophy$getCliff() {
            return this.naturalphilosophy$cliff;
        }
    }

}
