package voidsong.naturalphilosophy.common.worldgen.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.worldgen.NPTreeDecorators;

import javax.annotation.Nonnull;

public class FlamingBromeliadDecorator extends TreeDecorator {
    public static final MapCodec<FlamingBromeliadDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
            .fieldOf("probability")
            .xmap(FlamingBromeliadDecorator::new, instance -> instance.probability);
    private final float probability;

    public FlamingBromeliadDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    @Nonnull
    protected TreeDecoratorType<FlamingBromeliadDecorator> type() {
        return NPTreeDecorators.FLAMING_BROMELIAD.get();
    }

    @Override
    public void place(@Nonnull Context context) {
        RandomSource randomsource = context.random();
        for(BlockPos pos : context.logs()) {
            if (context.isAir(pos.above()) && !(randomsource.nextFloat() >= this.probability)) {
                context.setBlock(pos.above(), NPBlocks.FLAMING_BROMELIAD.get().defaultBlockState());
            }
        }
    }
}
