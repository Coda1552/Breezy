package coda.breezy.client;

import coda.breezy.Breezy;
import coda.breezy.client.render.HotAirBalloonRenderer;
import coda.breezy.registry.BreezyEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonRenderer::new);
    }
}
