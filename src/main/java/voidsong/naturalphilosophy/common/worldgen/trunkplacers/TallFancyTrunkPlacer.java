package voidsong.naturalphilosophy.common.worldgen.trunkplacers;


import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer.FoliageCoords;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import voidsong.naturalphilosophy.common.worldgen.NPTrunkPlacers;

import javax.annotation.Nonnull;

public class TallFancyTrunkPlacer extends FancyTrunkPlacer {
    public static final MapCodec<TallFancyTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> trunkPlacerParts(instance).apply(instance, TallFancyTrunkPlacer::new)
    );

    public TallFancyTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    @Nonnull
    protected TrunkPlacerType<?> type() {
        return NPTrunkPlacers.TALL_FANCY.get();
    }

    /**
     * This method is directly copied from {@link FancyTrunkPlacer#placeTrunk(LevelSimulatedReader, BiConsumer, RandomSource, int, BlockPos, TreeConfiguration)}.
     * I have done my best to re-name variables to something sensible, but they may not be accurate.
     * The only change is to pass (freeHeight-3) into {@link FancyTrunkPlacer#treeShape(int, int)} rather than freeHeight,
     * to reduce the branch spread for taller trees, so they do not remain roughly cubical in maximum bounding box
     */
    @Override
    @Nonnull
    public List<FoliageAttachment> placeTrunk(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        int freeTreeHeight,
        BlockPos pos,
        @Nonnull TreeConfiguration config
    ) {
        int freeHeight = freeTreeHeight + 2;
        int clippedHeight = Mth.floor((double)freeHeight * 0.618);
        setDirtAt(level, blockSetter, random, pos.below(), config);
        int branchMagic = Math.min(1, Mth.floor(1.382 + Math.pow((double)freeHeight/13.0, 2.0)));
        int topFoliageLocation = pos.getY() + clippedHeight;
        int maxBranchHeight = freeHeight - 5;
        List<FoliageCoords> list = Lists.newArrayList();
        list.add(new FoliageCoords(pos.above(maxBranchHeight), topFoliageLocation));

        for (; maxBranchHeight >= 0; maxBranchHeight--) {
            float baseLength = treeShape(freeTreeHeight - 1, maxBranchHeight);
            if (!(baseLength < 0.0F)) {
                for (int k1 = 0; k1 < branchMagic; k1++) {
                    double length = (double)baseLength * ((double)random.nextFloat() + 0.328);
                    double modifier = (double)(random.nextFloat() * 2.0F) * Math.PI;
                    double xLength = length * Math.sin(modifier) + 0.5;
                    double zLength = length * Math.cos(modifier) + 0.5;
                    BlockPos base = pos.offset(Mth.floor(xLength), maxBranchHeight - 1, Mth.floor(zLength));
                    BlockPos offset = base.above(5);
                    if (this.makeLimb(level, blockSetter, random, base, offset, false, config)) {
                        int xDiff = pos.getX() - base.getX();
                        int zDiff = pos.getZ() - base.getZ();
                        double highestY = (double)base.getY() - Math.sqrt((xDiff * xDiff + zDiff * zDiff)) * 0.381;
                        int maxHeight = highestY > (double)topFoliageLocation ? topFoliageLocation : (int)highestY;
                        BlockPos endPos = new BlockPos(pos.getX(), maxHeight, pos.getZ());
                        if (this.makeLimb(level, blockSetter, random, endPos, base, false, config)) {
                            list.add(new FoliageCoords(base, endPos.getY()));
                        }
                    }
                }
            }
        }

        this.makeLimb(level, blockSetter, random, pos, pos.above(clippedHeight), true, config);
        this.makeBranches(level, blockSetter, random, freeHeight, pos, list, config);
        List<FoliageAttachment> attachments = Lists.newArrayList();

        for (FoliageCoords foliagePos : list) {
            if (this.trimBranches(freeHeight, foliagePos.getBranchBase() - pos.getY())) {
                attachments.add(foliagePos.attachment);
            }
        }

        return attachments;
    }

    @Override
    public boolean trimBranches(int maxHeight, int currentHeight) {
        return (double)currentHeight >= (double)maxHeight * 0.4;
    }
}

