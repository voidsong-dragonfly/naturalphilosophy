package voidsong.naturalphilosophy.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("unused")
@Mixin(SnowAndFreezeFeature.class)
public abstract class FreezeSurfaceMixin {

    @Overwrite
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        BlockPos.MutableBlockPos top = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos surface = new BlockPos.MutableBlockPos();

        for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
                int k = blockpos.getX() + i;
                int l = blockpos.getZ() + j;
                int i1 = worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, k, l);
                top.set(k, i1, l);
                surface.set(top).move(Direction.DOWN, 1);
                Biome biome = worldgenlevel.getBiome(top).value();
                if (biome.shouldFreeze(worldgenlevel, surface, false)) {
                    worldgenlevel.setBlock(surface, Blocks.ICE.defaultBlockState(), 2);
                }

                if (biome.shouldSnow(worldgenlevel, top)) {
                    worldgenlevel.setBlock(top, Blocks.SNOW.defaultBlockState(), 2);
                    BlockState blockstate = worldgenlevel.getBlockState(surface);
                    if (blockstate.hasProperty(SnowyDirtBlock.SNOWY)) {
                        worldgenlevel.setBlock(surface, blockstate.setValue(SnowyDirtBlock.SNOWY, Boolean.TRUE), 2);
                    }
                }
            }
        }

        return true;
    }
}

