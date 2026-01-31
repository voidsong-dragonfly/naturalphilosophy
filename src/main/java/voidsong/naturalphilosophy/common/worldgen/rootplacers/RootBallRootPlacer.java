package voidsong.naturalphilosophy.common.worldgen.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import voidsong.naturalphilosophy.common.worldgen.NPRootPlacers;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiConsumer;

public class RootBallRootPlacer extends RootPlacer {
    public static final MapCodec<RootBallRootPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> rootPlacerParts(instance)
            .and(RootBallPlacement.CODEC.fieldOf("root_ball_placement").forGetter(placer -> placer.placement))
            .apply(instance, RootBallRootPlacer::new)
    );
    public final RootBallPlacement placement;

    public RootBallRootPlacer(IntProvider trunkOffset, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement, RootBallPlacement rootPlacement) {
        super(trunkOffset, rootProvider, aboveRootPlacement);
        this.placement = rootPlacement;
    }

    @Override
    @Nonnull
    protected RootPlacerType<?> type() {
        return NPRootPlacers.ROOT_BALL_ROOT_PLACER.get();
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
        @Nonnull BlockPos pos,
        @Nonnull BlockPos trunkOrigin,
        @Nonnull TreeConfiguration treeConfig
    ) {
        // Variables used repeatedly
        int radius = placement.rootRadius;
        MutableBlockPos mutablePos = pos.mutable();
        // Place the roots up until the final block
        for (int k = 2; k <= placement.rootColumnMaxDepth; k++) {
            // Move mutable position down
            mutablePos.setWithOffset(pos, 0, -k, 0);
            // Check if we have air here to place hanging roots
            if (level.isStateAtPosition(mutablePos, IBlockStateExtension::isEmpty)) {
                placeHangingRoots(level, blockSetter, random, mutablePos.immutable(), new MutableBlockPos());
            }
            // Place this lever's root set
            for (int j = 0; j < placement.rootPlacementAttempts; j++) {
                // New root position & placement
                mutablePos.setWithOffset(pos, random.nextInt(radius) - random.nextInt(radius), -k, random.nextInt(radius) - random.nextInt(radius));
                if (level.isStateAtPosition(mutablePos, state -> state.is(placement.canGrowThrough()))) {
                    blockSetter.accept(mutablePos.immutable(), rootProvider.getState(random, mutablePos));
                }
            }
        }
        // Place hanging roots below the final block if we have a cave
        if (level.isStateAtPosition(mutablePos.setWithOffset(pos, 0, -(placement.rootColumnMaxDepth+1), 0), IBlockStateExtension::isEmpty)) {
            placeHangingRoots(level, blockSetter, random, mutablePos.immutable(), new MutableBlockPos());
        }

        return true;
    }

    @Override
    protected boolean canPlaceRoot(@Nonnull LevelSimulatedReader level, @Nonnull BlockPos pos) {
        return super.canPlaceRoot(level, pos) || level.isStateAtPosition(pos, state -> state.is(placement.canGrowThrough()));
    }

    private void placeHangingRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos basePos, MutableBlockPos mutablePos) {
        int radius = placement.rootRadius;
        for (int k = 0; k < placement.hangingRootPlacementAttempts; k++) {
            mutablePos.setWithOffset(
                basePos,
                random.nextInt(radius) - random.nextInt(radius),
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(radius) - random.nextInt(radius)
            );
            if (level.isStateAtPosition(mutablePos, IBlockStateExtension::isEmpty)) {
                BlockState state = placement.hangingRootStateProvider.getState(random, mutablePos);
                if (level.isStateAtPosition(mutablePos.above(), above -> above.is(placement.canHangRootsFrom))) {
                    blockSetter.accept(mutablePos.immutable(), state);
                }
            }
        }
    }

    public record RootBallPlacement(
        HolderSet<Block> canGrowThrough,
        int rootRadius,
        int rootColumnMaxDepth,
        int rootPlacementAttempts,
        BlockStateProvider hangingRootStateProvider,
        HolderSet<Block> canHangRootsFrom,
        int hangingRootPlacementAttempts) {

        public static final Codec<RootBallPlacement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(placement -> placement.canGrowThrough),
                    Codec.intRange(1, 8).fieldOf("root_radius").forGetter(placement -> placement.rootRadius),
                    Codec.intRange(1, 8).fieldOf("root_depth").forGetter(placement -> placement.rootColumnMaxDepth),
                    Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter(placement -> placement.rootPlacementAttempts),
                    BlockStateProvider.CODEC.fieldOf("hanging_root_state_provider").forGetter(placement -> placement.hangingRootStateProvider),
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_hang_roots_from").forGetter(placement -> placement.canHangRootsFrom),
                    Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter(placement -> placement.hangingRootPlacementAttempts)
                ).apply(instance, RootBallPlacement::new)
        );

    }
}
