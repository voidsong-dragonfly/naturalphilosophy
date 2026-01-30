package voidsong.naturalphilosophy.common.worldgen.rootplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.apache.commons.lang3.tuple.Pair;
import voidsong.naturalphilosophy.common.worldgen.NPRootPlacers;

import javax.annotation.Nonnull;

public class LargeMangroveRootPlacer extends MangroveRootPlacer {

    List<Pair<Vec3i, Direction>> rootLocations = List.of(
        Pair.of(new Vec3i(2, 0, 0), Direction.EAST),
        Pair.of(new Vec3i(2, 0, 1), Direction.EAST),
        Pair.of(new Vec3i(0, 0, 2), Direction.SOUTH),
        Pair.of(new Vec3i(1, 0, 2), Direction.SOUTH),
        Pair.of(new Vec3i(-1, 0, 0), Direction.WEST),
        Pair.of(new Vec3i(-1, 0, 1), Direction.WEST),
        Pair.of(new Vec3i(0, 0, -1), Direction.NORTH),
        Pair.of(new Vec3i(1, 0, -1), Direction.NORTH)
    );

    public static final MapCodec<LargeMangroveRootPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> rootPlacerParts(instance)
            .and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter(placer -> placer.mangroveRootPlacement))
            .apply(instance, LargeMangroveRootPlacer::new)
    );

    public LargeMangroveRootPlacer(IntProvider trunkOffset, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement, MangroveRootPlacement mangroveRootPlacement) {
        super(trunkOffset, rootProvider, aboveRootPlacement, mangroveRootPlacement);
    }

    @Override
    @Nonnull
    protected RootPlacerType<?> type() {
        return NPRootPlacers.LARGE_MANGROVE_ROOT_PLACER.get();
    }

    @Override
    @Nonnull
    public BlockPos getTrunkOrigin(@Nonnull BlockPos pos, @Nonnull RandomSource random) {
        return pos;
    }

    @Override
    public boolean placeRoots(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        BlockPos pos,
        BlockPos trunkOrigin,
        @Nonnull TreeConfiguration treeConfig
    ) {
        List<BlockPos> list = Lists.newArrayList();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

        while (blockpos$mutableblockpos.getY() < trunkOrigin.getY()) {
            if (!this.canPlaceRoot(level, blockpos$mutableblockpos)) {
                return false;
            }

            blockpos$mutableblockpos.move(Direction.UP);
        }

        list.add(trunkOrigin.below());

        for (Pair<Vec3i, Direction> pair : rootLocations) {
            int offset = trunkOffsetY.sample(random);
            if(offset > 0) {
                BlockPos blockpos = trunkOrigin.offset(pair.getKey()).above(offset-1);
                List<BlockPos> list1 = Lists.newArrayList();
                if (!this.simulateRoots(level, random, blockpos, pair.getValue(), trunkOrigin, list1, 0)) {
                    return false;
                }

                list.addAll(list1);
                list.add(blockpos);
            }
        }

        for (BlockPos blockpos1 : list) {
            this.placeRoot(level, blockSetter, random, blockpos1, treeConfig);
        }

        return true;
    }
}
