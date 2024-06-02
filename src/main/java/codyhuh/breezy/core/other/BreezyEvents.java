package codyhuh.breezy.core.other;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entities.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static codyhuh.breezy.common.entities.HotAirBalloonEntity.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID)
public class BreezyEvents {

    @SubscribeEvent
    public static void leJank(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        if (result instanceof EntityHitResult eHit && eHit.getEntity() instanceof HotAirBalloonEntity balloon) {
            Projectile projectile = event.getProjectile();
            Vec3 projVelocity = projectile.getDeltaMovement().normalize();
            if (trueBalloonDist(1.5, 0.3, projectile.position(), projVelocity, balloon) > 1.5) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            }
        }
    }

    public static AABB boxInLevel(AABB box, Entity entity) {
        return new AABB(box.minX + entity.getX(), box.minY + entity.getY(), box.minZ + entity.getZ(),
                box.maxX + entity.getX(), box.maxY + entity.getY(), box.maxZ + entity.getZ());
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof HotAirBalloonEntity balloon) {
            Player player = event.getEntity();
            Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
            double boxOrBalloonDist = trueBalloonDist(5, 0.65, player.getEyePosition(1.0F), playerViewVector, balloon);
            double passengerDist;
            if (!balloon.getPassengers().isEmpty() && !balloon.hasPassenger(player)) {
                Entity passenger = balloon.getPassengers().get(0);
                passengerDist = distanceToPassenger(3, 0.25, player.getEyePosition(1.0F), playerViewVector, passenger);
                if (passengerDist < boxOrBalloonDist) {
                    event.setCanceled(true);
                    player.attack(passenger);
                }
            }
        }
    }

    public static double trueBalloonDist(double length, double delta, Vec3 origin, Vec3 direction, HotAirBalloonEntity balloon) {
        double viewDist = length + delta;
        AABB balloonBox = boxInLevel(BALLOON_AABB, balloon);
        AABB basketBox = boxInLevel(BASKET_AABB, balloon);
        for (double i = 0; i < length; i += delta) {
            Vec3 point = origin.add(direction.scale(i));
            if (balloonBox.contains(point) || basketBox.contains(point)) {
                return i;
            }
        }
        return viewDist;
    }

    public static double distanceToPassenger(double length, double delta, Vec3 origin, Vec3 direction, Entity victim) {
        AABB passengerBox = victim.getBoundingBox();
        for (double i = 0; i < 3; i += 0.25) {
            Vec3 point = origin.add(direction.scale(i));
            if (passengerBox.contains(point)) {
                return i;
            }
        }
        return 6;
    }

}
