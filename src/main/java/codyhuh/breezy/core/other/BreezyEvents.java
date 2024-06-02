package codyhuh.breezy.core.other;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Optional;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID)
public class BreezyEvents {

    @SubscribeEvent
    public static void leJank(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        if (result instanceof EntityHitResult eHit) {
            if (eHit.getEntity().getVehicle() instanceof HotAirBalloonEntity) {
                System.out.println("Where is your rider? " + eHit.getEntity());
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT);
            }
            if (eHit.getEntity() instanceof HotAirBalloonEntity balloon){
                Projectile projectile = event.getProjectile();
                double distance = 10;
                double passengerDist = 10;
                boolean passengerPossibleVictim = !balloon.getPassengers().isEmpty() &&
                        !balloon.hasPassenger(Objects.requireNonNull(projectile.getOwner()));
                AABB projBox = projectile.getBoundingBox();
                RandomSource random = balloon.getRandom();
                Vec3 projPoint = projectile.position();
                Vec3 projVelocity = projectile.getDeltaMovement().normalize();
                for (int i = 0; i < 3; i++) {
                    double x = projBox.minX + (projBox.maxX - projBox.minX) * random.nextDouble();
                    double y = projBox.minY + (projBox.maxY - projBox.minY) * random.nextDouble();
                    double z = projBox.minZ + (projBox.maxZ - projBox.minZ) * random.nextDouble();
                    Vec3 point = new Vec3(x, y, z);
                    distance = Math.min(distance, trueBalloonDist(4, 0.75, point, projVelocity, balloon));
                    if (passengerPossibleVictim) {
                        passengerDist = Math.min(passengerDist, distanceToPassenger(4, 0.75, point, projVelocity, Objects.requireNonNull(balloon.getFirstPassenger())));
                    }
//                balloon.level().addParticle(ParticleTypes.END_ROD, projectile.getX() + x, projectile.getY() + y, projectile.getZ() + z, 0, 0, 0);
                }
                if (distance > 4) {

                    event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                    if (passengerDist < 4) {
                        EntityHitResult entityHitResult = new EntityHitResult(balloon.getFirstPassenger());
//                    projectile.onHitEntity(entityHitResult);
                    }
                }
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
            if (balloon.hasPassenger(player) && !isLookingAtHitbox(player, boxInLevel(BALLOON_AABB, balloon)) && !isLookingAtHitbox(player, boxInLevel(BASKET_AABB, balloon))) {
                event.setCanceled(true);
                return;
            }
            double passengerDist;
            if (!balloon.getPassengers().isEmpty() && !balloon.hasPassenger(player)) {
                Entity passenger = balloon.getPassengers().get(0);
                passengerDist = distanceToPassenger(5, 0.45, player.getEyePosition(1.0F), playerViewVector, passenger);
                if (passengerDist < boxOrBalloonDist) {
                    event.setCanceled(true);
                    player.attack(passenger);
                }
            }
        }
    }

    public static double trueBalloonDist(double length, double delta, Vec3 origin, Vec3 direction, HotAirBalloonEntity balloon) {
        AABB balloonBox = boxInLevel(BALLOON_AABB, balloon);
        AABB basketBox = boxInLevel(BASKET_AABB, balloon);
        for (double i = 0; i < length; i += delta) {
            Vec3 point = origin.add(direction.scale(i));
//            balloon.level().addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
            if (balloonBox.contains(point) || basketBox.contains(point)) {
                return i;
            }
        }
        return length + delta;
    }

    public static double distanceToPassenger(double length, double delta, Vec3 origin, Vec3 direction, Entity victim) {
        AABB passengerBox = victim.getBoundingBox();
        for (double i = 0; i < length; i += delta) {
            Vec3 point = origin.add(direction.scale(i));
//            victim.level().addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
            if (passengerBox.contains(point)) {
                return i;
            }
        }
        return length+delta;
    }

    public static void noName(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(Items.NAME_TAG) &&
                event.getTarget() instanceof HotAirBalloonEntity) {
            event.setCancellationResult(InteractionResult.PASS);
        }
    }

    public static boolean isLookingAtHitbox(Player player, AABB box) {
        Vec3 playerEyePosition = player.getEyePosition(1.0F);
        Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
        Vec3 lookTarget = playerEyePosition.add(playerViewVector.scale(5.0)); // Assume a maximum view distance of 5 units

        Optional<Vec3> intersection = box.clip(playerEyePosition, lookTarget);
        return intersection.isPresent();
    }
}
