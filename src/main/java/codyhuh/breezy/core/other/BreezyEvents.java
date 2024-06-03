package codyhuh.breezy.core.other;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID)
public class BreezyEvents {
    @SubscribeEvent
    public static void noName(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(Items.NAME_TAG) &&
                event.getTarget() instanceof HotAirBalloonEntity) {
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }
}
