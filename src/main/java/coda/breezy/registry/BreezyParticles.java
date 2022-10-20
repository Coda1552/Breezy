package coda.breezy.registry;

import coda.breezy.Breezy;
import coda.breezy.client.particles.WindParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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

    @Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterParticleFactories {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            engine.register(WIND.get(), WindParticle.Provider::new);
        }
    }
}
