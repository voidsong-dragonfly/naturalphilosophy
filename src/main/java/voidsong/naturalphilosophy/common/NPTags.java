package voidsong.naturalphilosophy.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import voidsong.naturalphilosophy.NaturalPhilosophy;

import javax.annotation.Nonnull;

public class NPTags {
    public static class Blocks {
        public static final TagKey<Block> LARGE_BUSHES = tag("large_bushes");
        public static final TagKey<Block> MELTS_PERMAFROST = tag("melts_permafrost");
        public static final TagKey<Block> FREEZES_PERMAFROST = tag("freezes_permafrost");
        public static final TagKey<Block> GIANT_BAMBOO_PLANTABLE_ON = tag("giant_bamboo_plantable_on");
        public static final TagKey<Block> MUDDY_ROOTS_REPLACEABLE = tag("muddy_roots_replaceable");

        private static TagKey<Block> tag(@Nonnull String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, name));
        }
    }
}
