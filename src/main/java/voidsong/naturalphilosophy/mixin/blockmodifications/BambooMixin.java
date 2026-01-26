package voidsong.naturalphilosophy.mixin.blockmodifications;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nonnull;

@Mixin(BambooStalkBlock.class)
public class BambooMixin extends Block {
    @Unique
    private static final VoxelShape SMALL_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 17.0, 12.0);
    @Unique
    private static final VoxelShape LARGE_SHAPE = Block.box(2.0, 1.0, 2.0, 14.0, 17.0, 14.0);
    @Final
    @Shadow
    public static EnumProperty<BambooLeaves> LEAVES;

    /**
     * This constructor is the default & will be ignored, it exists so we can extend Block
     * @param properties ignored & should not be used!
     */
    public BambooMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.hasProperty(BambooStalkBlock.LEAVES) && state.getValue(BambooStalkBlock.LEAVES).equals(BambooLeaves.LARGE) ? 1 : 0;
    }

    @ModifyReturnValue(method = "getShape", at = @At(value = "RETURN"))
    public VoxelShape getShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        VoxelShape shape =  state.getValue(LEAVES).equals(BambooLeaves.LARGE) ? LARGE_SHAPE : SMALL_SHAPE;
        return (state.is(Blocks.BAMBOO) && !state.getValue(LEAVES).equals(BambooLeaves.NONE)) ? shape.move(vec3.x, vec3.y, vec3.z) : original;
    }
}
