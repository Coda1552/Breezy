package coda.whooosh.registry;

import coda.whooosh.Whooosh;
import coda.whooosh.client.particles.WindParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WhoooshParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Whooosh.MOD_ID);

    public static final RegistryObject<SimpleParticleType> WIND =
            PARTICLES.register("wind", () -> new SimpleParticleType(false));

    @Mod.EventBusSubscriber(modid = Whooosh.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterParticleFactories {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerParticleTypes(ParticleFactoryRegisterEvent event) {
            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            engine.register(WIND.get(), WindParticle.Provider::new);
        }
    }
}
