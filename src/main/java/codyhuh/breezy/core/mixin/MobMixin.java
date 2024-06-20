package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "isSunBurnTick", at = @At("RETURN"), cancellable = true)
    public void balloonRefuge(CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob)(Object)this;
        if (mob.getVehicle() instanceof HotAirBalloonEntity) {
            cir.setReturnValue(false);
        }
    }
}