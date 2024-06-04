package codyhuh.breezy.core.other.util;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BALLOON_AABB;
import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BASKET_AABB;

public class HitBoxUtil {
    public static AABB boxInLevel(AABB box, Entity entity) {
        return new AABB(box.minX + entity.getX(), box.minY + entity.getY(), box.minZ + entity.getZ(),
                box.maxX + entity.getX(), box.maxY + entity.getY(), box.maxZ + entity.getZ());
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
