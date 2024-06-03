package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static codyhuh.breezy.core.other.HitBoxUtil.isNotTargetingBalloonOrBasket;
import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;

@Mixin(Projectile.class)
public abstract class ProjectileMixin{
    @Shadow
    protected abstract boolean canHitEntity(Entity p_37250_);

    @Shadow
    protected abstract void onHitEntity(EntityHitResult p_37259_);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMixin(CallbackInfo ci) {
//        Projectile projectile = (Projectile) (Object) this;
//        if (!projectile.level().isClientSide) {
//            HitResult hitresult = breezy$getHitResultOnMoveVector(projectile, this::canHitEntity);
//            if (hitresult instanceof EntityHitResult entityHitResult &&
//                    entityHitResult.getEntity() instanceof HotAirBalloonEntity balloon
//            && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(projectile, entityHitResult)
//            && isNotTargetingBalloonOrBasket(projectile, balloon, projectile.getDeltaMovement())) {
//                ci.cancel();
//                Predicate<Entity> entityFilter = entity -> entity != balloon && canHitEntity(entity);
//                HitResult hitResult1 = breezy$getHitResultOnMoveVector(projectile, entityFilter);
//                if (hitResult1 instanceof EntityHitResult entityHitResult1 &&
//                        !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(projectile, entityHitResult1)) {
//                    onHitEntity(new EntityHitResult(entityHitResult1.getEntity()));
//                }
//            }
//        }
    }
}
