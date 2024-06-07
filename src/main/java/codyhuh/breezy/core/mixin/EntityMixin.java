package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "showVehicleHealth", at = @At(("HEAD")), cancellable = true)
    public void balloonsArentAliveYouDingus(CallbackInfoReturnable<Boolean> cir){
        Entity entity = (Entity) (Object) this;
        if (entity.getVehicle() instanceof HotAirBalloonEntity) {
            cir.setReturnValue(false);
        }
    }
}
