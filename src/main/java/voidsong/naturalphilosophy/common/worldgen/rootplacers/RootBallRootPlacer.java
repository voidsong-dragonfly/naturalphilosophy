package voidsong.naturalphilosophy.common.worldgen.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
            .and(GroundAmendment.CODEC.optionalFieldOf("ground_amendment", new GroundAmendment(BlockTags.DIRT, BlockStateProvider.simple(Blocks.DIRT))).forGetter(placer -> placer.amendment))
            .and(RootBallPlacement.CODEC.fieldOf("root_ball_placement").forGetter(placer -> placer.placement))
            .apply(instance, RootBallRootPlacer::new)
    );
    public final GroundAmendment amendment;
    public final RootBallPlacement placement;

    public RootBallRootPlacer(IntProvider trunkOffset, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement, GroundAmendment amendment, RootBallPlacement rootPlacement) {
        super(trunkOffset, rootProvider, aboveRootPlacement);
        this.amendment = amendment;
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
        int offset = placement.megaOffset;
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
                mutablePos.setWithOffset(pos, random.nextInt(radius+offset) - random.nextInt(radius), -k, random.nextInt(radius+offset) - random.nextInt(radius));
                // We do not go through RootPlacer#placeRoot here because we want an exact match and not to place above-root placements.
                // This root placer is closer to a secondary feature than a root placer such as MegaRootPlacer or MangroveRootPlacer
                // Going through RootPlacer#canPlaceRoot would also block it off from classes that extend this, such as MegaRootPlacer
                if (level.isStateAtPosition(mutablePos, state -> state.is(placement.canGrowThrough()))) {
                    blockSetter.accept(mutablePos.immutable(), getPotentiallyWaterloggedState(level, mutablePos, rootProvider.getState(random, mutablePos)));
                }
            }
            // Check to see if we have any ground blocks that need amendment after roots
            if (offset > 0 && k == 2) {
                for (int x = 0; x <= offset; x++) {
                    for (int z = 0; z <= offset; z++) {
                        // Set position to check
                        mutablePos.setWithOffset(pos, x, -k, z);
                        // Check if we can place;  See above comment (L85) on the reasoning behind this placement method
                        if (level.isStateAtPosition(mutablePos, state -> (state.canBeReplaced() || state.is(amendment.canReplace) && !state.equals(rootProvider.getState(random, mutablePos))))) {
                            blockSetter.accept(mutablePos.immutable(), getPotentiallyWaterloggedState(level, mutablePos, amendment.groundProvider.getState(random, mutablePos)));
                        }
                    }
                }
            }
        }
        // Place hanging roots below the final block if we have a cave
        if (level.isStateAtPosition(mutablePos.setWithOffset(pos, 0, -(placement.rootColumnMaxDepth+1), 0), IBlockStateExtension::isEmpty)) {
            placeHangingRoots(level, blockSetter, random, mutablePos.immutable(), new MutableBlockPos());
        }

        return true;
    }

    private void placeHangingRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos basePos, MutableBlockPos mutablePos) {
        int radius = placement.rootRadius;
        int offset = placement.megaOffset;
        for (int k = 0; k < placement.hangingRootPlacementAttempts; k++) {
            mutablePos.setWithOffset(
                basePos,
                random.nextInt(radius+offset) - random.nextInt(radius),
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(radius+offset) - random.nextInt(radius)
            );
            if (level.isStateAtPosition(mutablePos, IBlockStateExtension::isEmpty)) {
                BlockState state = placement.hangingRootStateProvider.getState(random, mutablePos);
                if (level.isStateAtPosition(mutablePos.above(), above -> above.is(placement.canHangRootsFrom))) {
                    blockSetter.accept(mutablePos.immutable(), state);
                }
            }
        }
    }

    public record GroundAmendment(TagKey<Block> canReplace, BlockStateProvider groundProvider) {
        // We use TagKey here rather than HomogeneousList so it's possible to default-case this to dirt
        public static final Codec<GroundAmendment> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                TagKey.codec(BuiltInRegistries.BLOCK.key()).fieldOf("can_replace").forGetter(placement -> placement.canReplace),
                BlockStateProvider.CODEC.fieldOf("ground_provider").forGetter(placement -> placement.groundProvider)
            ).apply(instance, GroundAmendment::new)
        );

    }

    public record RootBallPlacement(
        HolderSet<Block> canGrowThrough,
        int rootRadius,
        int rootColumnMaxDepth,
        int rootPlacementAttempts,
        BlockStateProvider hangingRootStateProvider,
        HolderSet<Block> canHangRootsFrom,
        int hangingRootPlacementAttempts,
        int megaOffset) {

        public static final Codec<RootBallPlacement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(placement -> placement.canGrowThrough),
                    Codec.intRange(1, 8).fieldOf("root_radius").forGetter(placement -> placement.rootRadius),
                    Codec.intRange(1, 8).fieldOf("root_depth").forGetter(placement -> placement.rootColumnMaxDepth),
                    Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter(placement -> placement.rootPlacementAttempts),
                    BlockStateProvider.CODEC.fieldOf("hanging_root_state_provider").forGetter(placement -> placement.hangingRootStateProvider),
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_hang_roots_from").forGetter(placement -> placement.canHangRootsFrom),
                    Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter(placement -> placement.hangingRootPlacementAttempts),
                    Codec.intRange(0, 2).optionalFieldOf("mega_offset", 0).forGetter(placement -> placement.megaOffset)
                ).apply(instance, RootBallPlacement::new)
        );

    }
}
