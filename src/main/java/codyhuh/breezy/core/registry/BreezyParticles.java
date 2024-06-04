package codyhuh.breezy.core.registry;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.client.particles.WindParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BreezyParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Breezy.MOD_ID);

    public static final RegistryObject<SimpleParticleType> WIND =
            PARTICLES.register("wind", () -> new SimpleParticleType(false));

    @SuppressWarnings({"unused"})
    @Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegisterParticleFactories {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            engine.register(WIND.get(), WindParticle.Provider::new);
        }
    }
}
