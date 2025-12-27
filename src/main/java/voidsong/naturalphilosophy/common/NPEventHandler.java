package voidsong.naturalphilosophy.common;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import voidsong.naturalphilosophy.common.config.NPClientConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class NPEventHandler {

    /**
     * Inspired by code from Exidex in SwingThroughGrass, migrated to run on the client instead of the server.
     * Simplified and cleaned up compared to their version
     */
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        // Grab level
        Level level = event.getLevel();
        // Return if we didn't hit a block in the first place
        if (!level.getBlockState(event.getPos()).getCollisionShape(level, event.getPos()).isEmpty() || !level.isClientSide || NPClientConfig.swingThroughVegetation.isFalse())
            return;
        // Grab raytrace hit
        Player player = event.getEntity();
        EntityHitResult rayTraceResult = getPossibleEntityHitResult(player, level);
        // Attack if we found an entity, using the gameMode attack as Player#attack() only works serverside for this
        if (rayTraceResult != null && Minecraft.getInstance().gameMode != null)
            Minecraft.getInstance().gameMode.attack(player, rayTraceResult.getEntity());
    }

    @Nullable
    private static EntityHitResult getPossibleEntityHitResult(Player player, Level level) {
        // View vectors
        Vec3 from = player.getEyePosition();
        Vec3 look = player.calculateViewVector(player.getXRot(), player.getYRot());
        Vec3 to = from.add(look.scale(player.blockInteractionRange()));
        // Block hit result; this code is modified from Item#getPlayerPOVHitResult changed to use ClipContext.Block.COLLIDER
        BlockHitResult hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hitResult.getType() != HitResult.Type.MISS)
            to = hitResult.getLocation();
        // Entity hit result, this is for some reason in ProjectileUtil
        return ProjectileUtil.getEntityHitResult(player, from, to, new AABB(from, to),
            EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e instanceof LivingEntity && e.isPickable() && !getAllRidingEntities(player).contains(e)),
            Mth.square(player.blockInteractionRange())
        );
    }

    private static List<Entity> getAllRidingEntities(Player player) {
        List<Entity> ridingEntities = new ArrayList<>();
        Entity entity = player;
        while (entity.isPassenger()) {
            Entity ridden = entity.getVehicle();
            if (ridden == null)
                break;
            entity = ridden;
            ridingEntities.add(entity);
        }
        return ridingEntities;
    }

}
