package voidsong.naturalphilosophy.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import voidsong.naturalphilosophy.NaturalPhilosophy;

import java.util.Optional;

public class NPTreeGrowers {
    public static final TreeGrower OAK = new TreeGrower(
            "np_oak",
            0.1F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(key("oak_forest")),
            Optional.of(key("oak_fancy")),
            Optional.of(key("oak_forest_flowers")),
            Optional.of(key("oak_fancy_flowers"))
    );
    public static final TreeGrower BIRCH = new TreeGrower(
            "np_oak",
            0.1F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(key("birch")),
            Optional.of(key("birch_forest")),
            Optional.of(key("birch_flowers")),
            Optional.of(key("birch_forest_flowers"))
    );
    public static final TreeGrower SPRUCE = new TreeGrower(
            "np_spruce",
            0.5F,
            Optional.of(key("spruce_mega")),
            Optional.of(key("pine_mega")),
            Optional.of(key("spruce")),
            Optional.of(key("pine")),
            Optional.empty(),
            Optional.empty()
    );
    public static final TreeGrower JUNGLE = new TreeGrower(
            "np_jungle",
            0.5F,
            Optional.of(key("jungle_canopy")),
            Optional.of(key("jungle_substory")),
            Optional.of(key("jungle")),
            Optional.of(key("jungle_understory")),
            Optional.empty(),
            Optional.empty()
    );
    public static final TreeGrower ACACIA = new TreeGrower(
            "np_acacia",
            0.1F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(key("acacia")),
            Optional.of(key("acacia_stunted")),
            Optional.empty(),
            Optional.empty()
    );
    public static final TreeGrower CHERRY = new TreeGrower(
            "np_cherry", Optional.empty(), Optional.of(key("cherry")), Optional.of(key("cherry_flowers"))
    );
    public static final TreeGrower DARK_OAK = new TreeGrower(
            "np_dark_oak",
            0.5F,
            Optional.of(key("dark_oak")),
            Optional.of(key("dark_oak_full_roots")),
            Optional.of(key("dark_oak_stunted")),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String tree) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, "sapling_trees/" + tree));
    }
}
