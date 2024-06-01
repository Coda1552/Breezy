package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entities.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Boat.class)
public class BoatMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;startRiding(Lnet/minecraft/world/entity/Entity;)Z"),
    locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void tick(CallbackInfo ci, List list, boolean flag, int j, Entity entity) {
        if (entity instanceof HotAirBalloonEntity) {
            Boat boat = (Boat)(Object)this;
            boat.push(entity);
            ci.cancel();
        }
    }
}
