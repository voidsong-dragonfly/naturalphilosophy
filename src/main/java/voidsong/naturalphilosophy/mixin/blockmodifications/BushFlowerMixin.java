package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import voidsong.naturalphilosophy.common.NPTags;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class BushFlowerMixin {
    @Mixin(Blocks.class)
    public static class BlocksMixin {
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=lilac", ordinal = 0),
                                 to = @At(value = "CONSTANT", args = "stringValue=rose_bush", ordinal = 0)))
        private static BlockBehaviour.Properties changeLilacSpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f).offsetType(BlockBehaviour.OffsetType.NONE);
        }

        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=rose_bush", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=peony", ordinal = 0)))
        private static BlockBehaviour.Properties changeRoseSpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f).offsetType(BlockBehaviour.OffsetType.NONE);
        }
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=peony", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=tall_grass", ordinal = 0)))
        private static BlockBehaviour.Properties changePeonySpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f).offsetType(BlockBehaviour.OffsetType.NONE);
        }
    }

    @Mixin(TallFlowerBlock.class)
    public static class TallFlowerBlockMixin extends Block {
        @Unique
        private static final VoxelShape naturalphilosophy$SHAPE = Block.box(0, 0.0, 0, 16, 7, 16);

        /**
         * This constructor exists to allow extending Block; it will not be used!
         */
        public TallFlowerBlockMixin(Properties properties) {
            super(properties);
        }

        @Unique
        protected void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
            if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE && state.is(NPTags.Blocks.SLOW_BUSHES)) {
                entity.makeStuckInBlock(state, new Vec3(0.875F, 0.75, 0.875F));
            }
        }

        @Unique
        @Nonnull
        protected VoxelShape getShape(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
            if (state.is(NPTags.Blocks.SLOW_BUSHES)) {
                Vec3 vec3 = state.getOffset(level, pos);
                return (state.getValue(TallFlowerBlock.HALF).equals(DoubleBlockHalf.UPPER) ? naturalphilosophy$SHAPE : Shapes.block()).move(vec3.x, vec3.y, vec3.z);
            }
            return super.getShape(state, level, pos, context);
        }
    }
}
