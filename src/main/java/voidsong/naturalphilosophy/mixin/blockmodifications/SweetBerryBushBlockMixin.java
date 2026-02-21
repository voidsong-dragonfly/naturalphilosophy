package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {
    @Inject(method = "entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V", at = @At(value= "HEAD"), cancellable = true)
    private void armorMitigatesSweetBerryBushIssues(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
            // If the entity is wearing foot & leg armor, they can move through the block slightly faster and don't take damage
            if (living.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FOOT_ARMOR) && living.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.LEG_ARMOR)) {
                entity.makeStuckInBlock(state, new Vec3(0.7F, 0.656, 0.7F));
                ci.cancel();
            }
        }
    }
}
