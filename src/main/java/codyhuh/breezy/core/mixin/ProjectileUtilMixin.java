package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import codyhuh.breezy.core.other.HitBoxUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    @ModifyVariable(
            method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("HEAD"),
            argsOnly = true)
    private static Predicate<Entity> viewResult(Predicate<Entity> originalPredicate, Entity seeker) {
        return entity -> originalPredicate.test(entity) && breezy$balloonCheck(entity, seeker, seeker.getViewVector(1.0F));
    }

    @ModifyVariable(
            method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("HEAD"), argsOnly = true)
    private static Predicate<Entity> moveResult(Predicate<Entity> originalPredicate, Level level, Entity mover) {
        return entity -> originalPredicate.test(entity) && breezy$balloonCheck(entity, mover, mover.getDeltaMovement());
    }

    @Unique
    private static boolean breezy$balloonCheck(Entity candidate, Entity seeker, Vec3 direction) {
        if (candidate instanceof HotAirBalloonEntity balloon) {
            return !HitBoxUtil.isNotTargetingBalloonOrBasket(seeker, balloon, direction);
        } else {
            return true;
        }
    }
}
