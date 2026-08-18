package voidsong.naturalphilosophy.common.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.placementmodifiers.CanopyGapFilter;
import voidsong.naturalphilosophy.common.worldgen.placementmodifiers.DepthFilter;

import javax.annotation.Nonnull;

public class NPPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DepthFilter>> DEPTH_FILTER = PLACEMENT_MODIFIERS.register("depth_filter",
        () -> new PlacementModifierType<>() {
            @Override
            @Nonnull
            public MapCodec<DepthFilter> codec() {
                return DepthFilter.CODEC;
            }
        });
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<CanopyGapFilter>> CANOPY_GAP_FILTER = PLACEMENT_MODIFIERS.register("canopy_gap_filter",
            () -> new PlacementModifierType<>() {
                @Override
                @Nonnull
                public MapCodec<CanopyGapFilter> codec() {
                    return CanopyGapFilter.CODEC;
                }
            });
}
