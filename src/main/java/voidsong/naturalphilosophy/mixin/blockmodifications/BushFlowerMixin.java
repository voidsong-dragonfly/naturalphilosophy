package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import voidsong.naturalphilosophy.common.NPTags;

@SuppressWarnings("unused")
public class BushFlowerMixin {
    @Mixin(Blocks.class)
    public static class BlocksMixin {
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=lilac", ordinal = 0),
                                 to = @At(value = "CONSTANT", args = "stringValue=rose_bush", ordinal = 0)))
        private static BlockBehaviour.Properties changeLilacSpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f);
        }

        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=rose_bush", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=peony", ordinal = 0)))
        private static BlockBehaviour.Properties changeRoseSpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f);
        }
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=peony", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=tall_grass", ordinal = 0)))
        private static BlockBehaviour.Properties changePeonySpeedFactor(BlockBehaviour.Properties properties) {
            return properties.strength(0.3f);
        }
    }

    @Mixin(TallFlowerBlock.class)
    public static class TallFlowerBlockMixin {
        @Unique
        @SuppressWarnings({"AddedMixinMembersNamePattern", "unused"})
        protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE && state.is(NPTags.Blocks.SLOW_BUSHES)) {
                entity.makeStuckInBlock(state, new Vec3(0.7F, 0.75, 0.7F));
            }
        }
    }
}
