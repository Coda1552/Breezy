package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class NameTagItemMixin {
    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    public void noNameTags(ItemStack p_42954_, Player p_42955_, LivingEntity living, InteractionHand p_42957_, CallbackInfoReturnable<InteractionResult> cir) {
        if (living instanceof HotAirBalloonEntity) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}