package voidsong.naturalphilosophy.common.worldgen.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import voidsong.naturalphilosophy.common.blocks.GiantBambooStalkBlock;
import voidsong.naturalphilosophy.common.worldgen.NPTrunkPlacers;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public class GiantBambooTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<GiantBambooTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> trunkPlacerParts(instance).apply(instance, GiantBambooTrunkPlacer::new)
    );

    public GiantBambooTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    @Nonnull
    protected TrunkPlacerType<?> type() {
        return NPTrunkPlacers.GIANT_BAMBOO.get();
    }

    @Override
    @Nonnull
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        int freeTreeHeight,
        BlockPos pos,
        @Nonnull TreeConfiguration config
    ) {
        setDirtAt(level, blockSetter, random, pos.below(), config);

        for (int i = 0; i < freeTreeHeight; i++) {
            this.placeLog(level, blockSetter, random, pos.above(i), config, (i > freeTreeHeight - 5) ? BambooLeaves.LARGE : ((i > freeTreeHeight - 6) ? BambooLeaves.SMALL : BambooLeaves.NONE), Math.max(0, i - (freeTreeHeight - 2)));
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
    }

    protected boolean placeLog(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        @Nonnull BlockPos pos,
        @Nonnull TreeConfiguration config,
        @Nonnull BambooLeaves leaves,
        int stage
    ) {
        return this.placeLog(level, blockSetter, random, pos, config, (before) -> before.hasProperty(GiantBambooStalkBlock.LEAVES) ? before.setValue(GiantBambooStalkBlock.LEAVES, leaves).setValue(GiantBambooStalkBlock.STAGE, stage) : before);
    }
}
