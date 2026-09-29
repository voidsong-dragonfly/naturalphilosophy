package voidsong.naturalphilosophy.common.worldgen.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import voidsong.naturalphilosophy.common.worldgen.NPTreeDecorators;

import javax.annotation.Nonnull;
import java.util.List;

public class RootAreaAlterGroundDecorator extends TreeDecorator {
    public static final MapCodec<RootAreaAlterGroundDecorator> CODEC = RecordCodecBuilder.mapCodec(
    builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("provider").forGetter(instance -> instance.provider),
            Codec.BOOL.fieldOf("remove_grass").forGetter(instance -> instance.removeGrass),
            Codec.intRange(-1, 8).optionalFieldOf("max_leaf_distance", -1).forGetter(instance -> instance.maxLeafDistance)
        ).apply(builder, RootAreaAlterGroundDecorator::new)
    );
    private final BlockStateProvider provider;
    private final boolean removeGrass;
    private final int maxLeafDistance;

    public RootAreaAlterGroundDecorator(BlockStateProvider provider, boolean removeGrass, int maxLeafDistance) {
        this.provider = provider;
        this.removeGrass = removeGrass;
        this.maxLeafDistance = maxLeafDistance;
    }

    @Override
    @Nonnull
    protected TreeDecoratorType<?> type() {
        return NPTreeDecorators.ROOT_AREA_ALTER_GROUND.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        List<BlockPos> origins = Lists.newArrayList();
        List<BlockPos> roots = context.roots();
        List<BlockPos> logs = context.logs();
        if (roots.isEmpty()) {
            origins.addAll(logs);
        } else {
            origins.addAll(logs);
            origins.addAll(roots);
        }

        if (!origins.isEmpty()) {
            var eventProvider = EventHooks.alterGround(context, origins, this.provider::getState);

            origins.stream().filter(pos -> filterDistance(context.random(), logs.getFirst(), pos)).forEach(pos -> this.placeCircle(context, pos, eventProvider));
        }
    }

    private static boolean filterDistance(RandomSource random, BlockPos origin, BlockPos place) {
        origin = origin.atY(place.getY());
        int bound = (int)Math.round(1.5f * Math.abs(origin.distSqr(place)));
        return bound == 0 || random.nextInt(bound) == 0;
    }

    private static boolean filterLeafHeight(BlockPos pos, LevelSimulatedReader level, int distance) {
        if ((level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() - pos.getY()) == 0) return true;
        for (int i = 1; i < distance; i++) {
            if (level.isStateAtPosition(pos.offset(0, i, 0), state -> state.is(BlockTags.LEAVES))) return false;
        }
        return true;
    }

    private void placeCircle(TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider) {
        int radius = context.roots().isEmpty() || context.logs().size() < 10 ? 1 : 2;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (Math.abs(i) != radius || Math.abs(j) != radius) {
                    this.placeBlockAt(context, pos.offset(i, 0, j), eventProvider);
                }
            }
        }
    }

    private void placeBlockAt(TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider) {
        BlockPos origin = context.logs().getFirst();
        BlockPos heightmap = pos.equals(origin) ? origin.above() : context.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        if (!TreeFeature.validTreePos(context.level(), heightmap)) return;
        if (!Feature.isGrassOrDirt(context.level(), heightmap.below())) return;
        if (maxLeafDistance != -1 && filterLeafHeight(heightmap, context.level(), maxLeafDistance)) return;
        // Modified version of the Minecraft version of this function; adds the option to replace grass above
        BlockPos below = heightmap.below();
        context.setBlock(below, eventProvider.getState(context.random(), heightmap.below()));
        // Added portion to replace grass
        if (removeGrass) {
            if (context.level().isStateAtPosition(heightmap, state -> state.is(Blocks.SHORT_GRASS)))
                context.setBlock(heightmap, Blocks.AIR.defaultBlockState());
            else if (context.level().isStateAtPosition(heightmap, state -> state.is(Blocks.TALL_GRASS))) {
                context.setBlock(heightmap, Blocks.AIR.defaultBlockState());
                context.setBlock(heightmap.above(), Blocks.AIR.defaultBlockState());
            }
        }
    }
}
