package codyhuh.breezy.core.other;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Predicate;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BALLOON_AABB;
import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BASKET_AABB;

public class HitBoxUtil {
    public static AABB boxInLevel(AABB box, Entity entity) {
        return new AABB(box.minX + entity.getX(), box.minY + entity.getY(), box.minZ + entity.getZ(),
                box.maxX + entity.getX(), box.maxY + entity.getY(), box.maxZ + entity.getZ());
    }

    public static Entity findClosestEntity(Entity seeker, HotAirBalloonEntity balloon, Vec3 direction) {
        // This method finds the closest entity. If it intersects with the seeker's eyeline, they're good as hit.
        Vec3 playerEyePosition = seeker.getEyePosition();
        Vec3 endPoint = playerEyePosition.add(direction.normalize().scale(5.0F));
        Predicate<Entity> entityFilter = entity -> verifyClosest(entity, seeker, balloon);
        Entity closestEntity = helpFindClosest(seeker, playerEyePosition, endPoint, entityFilter);
        if (closestEntity != null) {
            Optional<Vec3> intersection = closestEntity.getBoundingBox().clip(playerEyePosition, endPoint);
            if (intersection.isPresent()) {
                return closestEntity;
            }
        }
        return null;
    }

    public static boolean verifyClosest(Entity entity, Entity seeker, HotAirBalloonEntity balloon) {
        if (entity == balloon || entity == seeker) return false;
        if (entity instanceof HotAirBalloonEntity otherBalloon) {
            return !isNotTargetingBalloonOrBasket(seeker, otherBalloon, seeker.getViewVector(1.0F).normalize());
        }
        return true;
    }

    public static Entity helpFindClosest(Entity seeker, Vec3 start, Vec3 end, Predicate<Entity> filter) {
        double closestDistance = Double.MAX_VALUE;
        Entity closestEntity = null;
        for (Entity entity : seeker.level().getEntities(seeker, new AABB(start, end).inflate(1.0D), filter)) {
            Optional<Vec3> intersection = entity.getBoundingBox().clip(start, end);
            if (intersection.isPresent()) {
                double distance = start.distanceToSqr(intersection.get());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }


    public static double trueBalloonDist(double length, Entity seeker, HotAirBalloonEntity balloon) {
        AABB balloonBox = boxInLevel(BALLOON_AABB, balloon);
        AABB basketBox = boxInLevel(BASKET_AABB, balloon);
        Vec3 origin = seeker.getEyePosition();
        Vec3 endPoint = origin.add(seeker.getViewVector(1.0F).scale(length));
        double distance = Double.MAX_VALUE;
        double balloonDistance = Double.MAX_VALUE;
        Optional<Vec3> balloonIntersection = balloonBox.clip(origin, endPoint);
        if (balloonIntersection.isPresent()) {
            balloonDistance = origin.distanceTo(balloonIntersection.get());
            if (balloonDistance < length) {
                distance = balloonDistance;
            }
        }
        Optional<Vec3> basketIntersection = basketBox.clip(origin, endPoint);
        if (basketIntersection.isPresent()) {
            double basketDistance = origin.distanceTo(basketIntersection.get());
            if (basketDistance < balloonDistance) {
                distance = basketDistance;
            }
        }
        return distance;
    }


    public static double passengerDist(double length, Entity seeker, Entity passenger) {
        Vec3 origin = seeker.getEyePosition();
        AABB passengerBox = passenger.getBoundingBox();
        Vec3 endPoint = origin.add(seeker.getViewVector(1.0F).scale(length));
        double distance = Double.MAX_VALUE;
        Optional<Vec3> passengerIntersection = passengerBox.clip(origin, endPoint);
        if (passengerIntersection.isPresent()) {
            double passengerDistance = origin.distanceTo(passengerIntersection.get());
            if (passengerDistance < distance) {
                distance = passengerDistance;
            }
        }
        return distance;
    }

    public static boolean isNotTargetingBalloonOrBasket(Entity viewer, HotAirBalloonEntity balloon, Vec3 direction) {
        return isNotAimingAtHitbox(viewer, boxInLevel(BALLOON_AABB, balloon), direction) && isNotAimingAtHitbox(viewer, boxInLevel(BASKET_AABB, balloon), direction);
    }

    public static boolean isNotAimingAtHitbox(Entity viewer, AABB box, Vec3 direction) {
        Vec3 viewerPos = viewer.getEyePosition(1.0F);
        Vec3 aimVector = direction.normalize();
        Vec3 lookTarget = viewerPos.add(aimVector.scale(3.0));
        if (viewer instanceof Player player) {
            lookTarget = viewerPos.add(aimVector.scale(player.getEntityReach()));
        }
        Optional<Vec3> intersection = box.clip(viewerPos, lookTarget);
        return intersection.isEmpty();
    }
}
