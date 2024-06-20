package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class MobEffectMixin {
    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    private void balloonsAreNotAlive(LivingEntity living, int p_19468_, CallbackInfo ci) {
        MobEffect mobEffect = (MobEffect)(Object)this;
        if (living instanceof HotAirBalloonEntity && mobEffect.equals(MobEffects.HARM)) {
            ci.cancel();
        }
    }

    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    private void balloonsAreStillNotAlive(Entity p_19462_, Entity p_19463_, LivingEntity living, int p_19465_, double p_19466_, CallbackInfo ci) {
        MobEffect mobEffect = (MobEffect)(Object)this;
        if (living instanceof HotAirBalloonEntity && mobEffect.equals(MobEffects.HARM)) {
            ci.cancel();
        }
    }
}