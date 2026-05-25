package voidsong.naturalphilosophy.mixin.blockmodifications;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.blocks.WaterPlantBlock;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {
    @ModifyReturnValue(method = "copyWaterloggedFrom", at = @At(value = "RETURN"))
    private static BlockState waterPlantWetModification(BlockState original, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) LevelReader level) {
        // We need to modify blocks with the wet property to inherit it properly
        if (original.hasProperty(WaterPlantBlock.WET)) {
            if (original.getValue(DoublePlantBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
                pos = pos.below();
                return original.setValue(WaterPlantBlock.WET, level.isWaterAt(pos));
            } else {
                return original.setValue(WaterPlantBlock.WET, original.getValue(BlockStateProperties.WATERLOGGED));
            }
        }
        return original;
    }
}
