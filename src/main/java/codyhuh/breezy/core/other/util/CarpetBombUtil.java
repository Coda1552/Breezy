package codyhuh.breezy.core.other.util;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BALLOON_AABB;

public class CarpetBombUtil {
    public static InteractionResult carpetBomb(PlayerInteractEvent.RightClickItem event, ItemStack pen, ItemStack pineapple, EntityType<?> bombType) {
        Player player = event.getEntity();
        if (player.getVehicle() instanceof HotAirBalloonEntity balloon && !balloon.onGround() &&
                HitBoxUtil.isNotAimingAtHitbox(player, HitBoxUtil.boxInLevel(BALLOON_AABB, balloon),
                        player.getViewVector((float) player.getEntityReach())) && !pen.isEmpty() && !pineapple.isEmpty()) {

            if (event.getLevel() instanceof ServerLevel server) {
                Entity bomb = (Entity) bombType.create(event.getLevel());
                if (bomb != null) {
                    bomb.moveTo(Vec3.atBottomCenterOf(player.blockPosition().above()));
                    server.addFreshEntity(bomb);
                    balloon.playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        pineapple.shrink(1);
                        pen.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    }
                }
            } else {
                player.swing(event.getHand());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
