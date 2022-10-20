package coda.breezy.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class WindParticle extends RisingParticle {
    private final SpriteSet sprites;

    public WindParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet p_107724_) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = p_107724_;
        this.scale(50.5F);
        this.lifetime = 50;
        this.hasPhysics = true;
        this.setPos(x, y, z);
        this.setSpriteFromAge(p_107724_);
        this.setAlpha(0.5F);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);

        if (y < 85) remove();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_107739_) {
            this.sprite = p_107739_;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Random random = new Random();
            int d = random.nextInt(30) + 40;
            double r = random.nextDouble() * Math.PI * 2;
            double newY = y + random.nextInt(15) + random.nextInt(15);

            return new WindParticle(world, (Math.cos(r) * d) + x, newY, (Math.sin(r) * d) + z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }
}