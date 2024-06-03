package codyhuh.breezy.core.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileMixin {
    @Invoker
    void callOnHitEntity(EntityHitResult p_37259_);
}
