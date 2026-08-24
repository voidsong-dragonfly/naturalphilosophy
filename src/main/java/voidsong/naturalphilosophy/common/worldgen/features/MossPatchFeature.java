package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.features.MossPatchFeature.MossPatchConfiguration;

public class MossPatchFeature extends Feature<MossPatchConfiguration> {
    private static final ResourceKey<NormalNoise.NoiseParameters> NOISE = ResourceKey.create(Registries.NOISE, ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, "moss_patch"));
    private static final BlockState MOSS_CARPET = Blocks.MOSS_CARPET.defaultBlockState();

    public MossPatchFeature(Codec<MossPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MossPatchConfiguration> context) {
        MossPatchConfiguration configuration = context.config();
        WorldGenLevel level = context.level();
        RandomState random = level.getLevel().getChunkSource().randomState();
        MutableBlockPos pos = context.origin().mutable();
        for (int xOffset = -configuration.radius; xOffset <= configuration.radius; xOffset++) {
            for (int zOffset = -configuration.radius; zOffset <= configuration.radius; zOffset++) {
                if (xOffset*xOffset + zOffset*zOffset < configuration.radius*configuration.radius) {
                    int x = context.origin().getX() + xOffset;
                    int z = context.origin().getZ() + zOffset;
                    if (random.getOrCreateNoise(NOISE).getValue(x, 0, z) > configuration.threshold()) {
                        pos.set(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);
                        if (level.getBlockState(pos).isAir() && Block.canSupportRigidBlock(level, pos.below()) && !configuration.supportBlacklist().test(level, pos.below())) {
                            level.setBlock(pos, MOSS_CARPET, 3);
                        }
                    }
                }
            }
        }
        return true;
    }

    public record MossPatchConfiguration(int radius, double threshold, BlockPredicate supportBlacklist) implements FeatureConfiguration {
        public static final Codec<MossPatchConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("radius").forGetter(MossPatchConfiguration::radius),
            Codec.DOUBLE.fieldOf("threshold").forGetter(MossPatchConfiguration::threshold),
            BlockPredicate.CODEC.fieldOf("moss_cannot_place_on").forGetter(MossPatchConfiguration::supportBlacklist)
        ).apply(builder, MossPatchConfiguration::new));
    }
}
