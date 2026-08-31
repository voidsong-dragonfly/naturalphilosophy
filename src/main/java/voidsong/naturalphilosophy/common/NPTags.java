package voidsong.naturalphilosophy.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import voidsong.naturalphilosophy.NaturalPhilosophy;

import javax.annotation.Nonnull;

public class NPTags {
    public static class Blocks {
        public static final TagKey<Block> MELTS_PERMAFROST = tag("melts_permafrost");
        public static final TagKey<Block> FREEZES_PERMAFROST = tag("freezes_permafrost");
        public static final TagKey<Block> MUDDY_ROOTS_REPLACEABLE = tag("muddy_roots_replaceable");
        public static final TagKey<Block> SPREADING_BUSH_FLOWERS = tag("spreading_bush_flowers");
        public static final TagKey<Block> SUPPORTS_GIANT_BAMBOO = tag("supports_giant_bamboo");
        public static final TagKey<Block> SUPPORTS_DUNE_GRASS = tag("supports_dune_grass");
        public static final TagKey<Block> SUPPORTS_FLAMING_BROMELIAD = tag("supports_flaming_bromeliad");
        public static final TagKey<Block> SUPPORTS_MUDDY_PLANT = tag("supports_muddy_plant");

        private static TagKey<Block> tag(@Nonnull String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, name));
        }
    }
}
