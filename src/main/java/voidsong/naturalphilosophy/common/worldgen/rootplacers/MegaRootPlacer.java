package voidsong.naturalphilosophy.common.worldgen.rootplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.apache.commons.lang3.tuple.Pair;
import voidsong.naturalphilosophy.common.worldgen.NPRootPlacers;

import javax.annotation.Nonnull;

public class MegaRootPlacer extends RootBallRootPlacer {

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

    public static final MapCodec<MegaRootPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> rootPlacerParts(instance)
            .and(BlockStateProvider.CODEC.fieldOf("surface_root_provider").forGetter(placer -> placer.rootProvider))
            .and(SurfaceRootPlacement.CODEC.fieldOf("surface_root_placement").forGetter(placer -> placer.surfaceRootPlacement))
            .and(GroundAmendment.CODEC.optionalFieldOf("ground_amendment", new GroundAmendment(BlockTags.DIRT, BlockStateProvider.simple(Blocks.DIRT))).forGetter(placer -> placer.amendment))
            .and(BlockStateProvider.CODEC.fieldOf("muddy_root_provider").forGetter(placer -> placer.muddyRootProvider))
            .and(RootBallPlacement.CODEC.fieldOf("root_ball_placement").forGetter(placer -> placer.placement))
            .apply(instance, MegaRootPlacer::new)
    );
    protected final BlockStateProvider surfaceRootProvider;
    public final SurfaceRootPlacement surfaceRootPlacement;

    public MegaRootPlacer(IntProvider trunkOffset,
                          BlockStateProvider rootProvider,
                          Optional<AboveRootPlacement> aboveRootPlacement,
                          BlockStateProvider surfaceRootProvider,
                          SurfaceRootPlacement largePlacement,
                          GroundAmendment amendment,
                          BlockStateProvider muddyRootProvider,
                          RootBallPlacement ballPlacement) {
        super(trunkOffset, rootProvider, aboveRootPlacement, amendment, muddyRootProvider, ballPlacement);
        this.surfaceRootProvider = surfaceRootProvider;
        this.surfaceRootPlacement = largePlacement;
    }

    @Override
    @Nonnull
    protected RootPlacerType<?> type() {
        return NPRootPlacers.MEGA_ROOT_PLACER.get();
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
        List<BlockPos> list = Lists.newArrayList();

        list.add(trunkOrigin.below());

        for (Pair<Vec3i, Direction> pair : rootLocations) {
            int offset = trunkOffsetY.sample(random);
            if(offset > 0) {
                BlockPos blockpos = trunkOrigin.offset(pair.getKey()).above(offset-1);
                List<BlockPos> simulate = Lists.newArrayList();
                if (!this.simulateRoots(level, random, blockpos, pair.getValue(), trunkOrigin, simulate, 0)) {
                    continue;
                }

                list.addAll(simulate);
                list.add(blockpos);
            }
        }

        for (BlockPos offset : list) {
            this.placeRoot(level, blockSetter, random, offset, treeConfig);
        }

        return super.placeRoots(level, blockSetter, random, pos, trunkOrigin, treeConfig);
    }

    @Override
    protected boolean canPlaceRoot(@Nonnull LevelSimulatedReader level, @Nonnull BlockPos pos) {
        return super.canPlaceRoot(level, pos) || level.isStateAtPosition(pos, state -> state.is(surfaceRootPlacement.canGrowThrough));
    }

    public record SurfaceRootPlacement(HolderSet<Block> canGrowThrough, int maxRootWidth, int maxRootLength, float randomSkewChance) {
        public static final Codec<SurfaceRootPlacement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(placement -> placement.canGrowThrough),
                    Codec.intRange(1, 12).fieldOf("max_root_width").forGetter(placement -> placement.maxRootWidth),
                    Codec.intRange(1, 64).fieldOf("max_root_length").forGetter(placement -> placement.maxRootLength),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter(placement -> placement.randomSkewChance)
                ).apply(instance, SurfaceRootPlacement::new)
        );
    }

    /**
     * This method is adapted from {@link net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer}
     * It is similar, but uses the surfaceRootProvider and checks canPlaceSurfaceRoot
     */
    @Override
    protected void placeRoot(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        @Nonnull BlockPos pos,
        @Nonnull TreeConfiguration treeConfig
    ) {
        if (canPlaceRoot(level, pos)) {
            blockSetter.accept(pos, this.getPotentiallyWaterloggedState(level, pos, this.surfaceRootProvider.getState(random, pos)));
            if (this.aboveRootPlacement.isPresent()) {
                AboveRootPlacement aboverootplacement = this.aboveRootPlacement.get();
                BlockPos top = pos.above();
                if (random.nextFloat() < aboverootplacement.aboveRootPlacementChance() && level.isStateAtPosition(top, BlockStateBase::isAir)) {
                    blockSetter.accept(top, this.getPotentiallyWaterloggedState(level, top, aboverootplacement.aboveRootProvider().getState(random, top)));
                }
            }
        }
    }

    /**
     * This method is copied wholesale from {@link net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer}
     * It has had no changes other than variable renames for my codestyle.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean simulateRoots(
        LevelSimulatedReader level,
        RandomSource random,
        BlockPos pos,
        Direction direction,
        BlockPos trunkOrigin,
        List<BlockPos> roots,
        int length
    ) {
        int maxLength = surfaceRootPlacement.maxRootLength();
        if (length != maxLength && roots.size() <= maxLength) {
            for (BlockPos blockpos : this.potentialRootPositions(pos, direction, random, trunkOrigin)) {
                if (this.canPlaceRoot(level, blockpos)) {
                    roots.add(blockpos);
                    if (!this.simulateRoots(level, random, blockpos, direction, trunkOrigin, roots, length + 1)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is copied wholesale from {@link net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer}
     * It has had no changes other than variable renames for my codestyle.
     */
    protected List<BlockPos> potentialRootPositions(BlockPos pos, Direction direction, RandomSource random, BlockPos trunkOrigin) {
        BlockPos blockpos = pos.below();
        BlockPos relative = pos.relative(direction);
        int trunkDistance = pos.distManhattan(trunkOrigin);
        int maxWidth = this.surfaceRootPlacement.maxRootWidth();
        float skewChance = this.surfaceRootPlacement.randomSkewChance();
        if (trunkDistance > maxWidth - 3 && trunkDistance <= maxWidth) {
            return random.nextFloat() < skewChance ? List.of(blockpos, relative.below()) : List.of(blockpos);
        } else if (trunkDistance > maxWidth) {
            return List.of(blockpos);
        } else if (random.nextFloat() < skewChance) {
            return List.of(blockpos);
        } else {
            return random.nextBoolean() ? List.of(relative) : List.of(blockpos);
        }
    }
}
