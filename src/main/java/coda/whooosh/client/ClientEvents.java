package coda.whooosh.client;

import coda.whooosh.Whooosh;
import coda.whooosh.client.render.HotAirBalloonRenderer;
import coda.whooosh.registry.WhoooshEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Whooosh.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhoooshEntities.HOT_AIR_BALLOON.get(), HotAirBalloonRenderer::new);
    }
}
