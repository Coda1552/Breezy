package codyhuh.breezy.core.other;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import codyhuh.breezy.core.mixin.ProjectileAccessor;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

import static codyhuh.breezy.core.other.HitBoxUtil.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID)
public class BreezyEvents {
    @SubscribeEvent
    public static void leJank(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        if (result instanceof EntityHitResult eHit) {
            if (eHit.getEntity() instanceof HotAirBalloonEntity balloon) {
                Projectile projectile = event.getProjectile();
                if (projectile.getOwner() != null) {
                    if (balloon.hasPassenger(projectile.getOwner())) {
                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                    }
                }
                Entity passenger = balloon.getFirstPassenger();
                if (isNotTargetingBalloonOrBasket(projectile, balloon, projectile.getDeltaMovement())) {
                    if (passenger != null && findClosestEntity(projectile, balloon, projectile.getDeltaMovement()) == passenger) {
                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT_NO_DAMAGE);
                        ((ProjectileAccessor) projectile).callOnHitEntity(new EntityHitResult(passenger));
                    } else {
                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof HotAirBalloonEntity balloon) {
            Player player = event.getEntity();
            Vec3 playerEyePosition = player.getEyePosition(1.0F);
            Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
            // if they hit the balloon they hit the balloon bruh
            if (isNotTargetingBalloonOrBasket(player, balloon, playerViewVector)) {
                event.setCanceled(true);
            } else return;

            double balloonDistance = trueBalloonDist(player.getEntityReach(), player, balloon);
            Entity passenger = balloon.getFirstPassenger();
            // if passenger distance is less than balloon distance then hit the non-player passenger, not the balloon
            if (passenger != null && passenger != player) {
                double passengerDistance = passengerDist(player.getEntityReach(), player, passenger);
                if (passengerDistance < balloonDistance) {
                    player.attack(passenger);
                    return;
                }
            }
            Entity closestEntity = findClosestEntity(player, balloon, playerViewVector.normalize());
            if (closestEntity != null) {
                player.attack(closestEntity);
            }
        }
    }

    @SubscribeEvent
    public static void noName(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(Items.NAME_TAG) &&
                event.getTarget() instanceof HotAirBalloonEntity) {
            event.setCancellationResult(InteractionResult.PASS);
        }
    }
}
