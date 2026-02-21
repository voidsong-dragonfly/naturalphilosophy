package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
    @Inject(method = "entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V", at = @At(value= "HEAD"), cancellable = true)
    private void armorPreventsCactusDamage(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        // We only care about entities that can wear armor
        if (entity instanceof LivingEntity living) {
            if (living.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FOOT_ARMOR)) {
                // If we're on top of the cactus with boots, no damage
                if ((int)(entity.position().y+0.075) > pos.getY()) {
                    ci.cancel();
                // If only our lower half is touching the cactus & we have legs/boots, no damage
                } else if (living.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.LEG_ARMOR)) {
                    if ((entity.position().y + 0.55 > pos.getY())) {
                        ci.cancel();
                    // If our top half is touching the cactus, but we have chest armor, no damage
                    } else if (living.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.CHEST_ARMOR)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
