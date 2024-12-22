package voidsong.naturalphilosophy.common.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ArchaeologyBlockFeature extends Feature<ArchaeologyBlockFeature.ArchaeologyBlockConfiguration> {
    public ArchaeologyBlockFeature(Codec<ArchaeologyBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ArchaeologyBlockConfiguration> context) {
        ArchaeologyBlockConfiguration configuration = context.config();
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        BlockState state = configuration.provider().getState(context.random(), pos);
        boolean placed = level.setBlock(pos, state, 2);
        level.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent(brushableBlockEntity -> brushableBlockEntity.setLootTable(configuration.table(), pos.asLong()));
        return placed;
    }

    public record ArchaeologyBlockConfiguration(BlockStateProvider provider, ResourceLocation table) implements FeatureConfiguration {
        public static final Codec<ArchaeologyBlockConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("to_place").forGetter(ArchaeologyBlockConfiguration::provider),
            ResourceLocation.CODEC.fieldOf("table").forGetter(ArchaeologyBlockConfiguration::table)
        ).apply(builder, ArchaeologyBlockConfiguration::new));
    }
}
