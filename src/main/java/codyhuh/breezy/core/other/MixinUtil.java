package codyhuh.breezy.core.other;

import codyhuh.breezy.BreezyConfig;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import codyhuh.breezy.core.other.tags.BreezyBiomeTags;
import codyhuh.breezy.core.registry.BreezyParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static codyhuh.breezy.core.other.HitBoxUtil.*;

public class MixinUtil {
    // Hotswapping mixins my beloved

    @OnlyIn(Dist.CLIENT)
    public static void tryAddWindParticle(ClientLevel level, BlockPos pos, RandomSource random) {
        if (!BreezyConfig.CLIENT.shouldDisplayWind.get() ||
                pos.getY() >= BreezyConfig.CLIENT.minimumWindHeight.get() + level.getSeaLevel()) return;
        if (BreezyConfig.CLIENT.windMustSeeSky.get() && !level.canSeeSky(pos)) return;
        Holder<Biome> holder = level.getBiome(pos);
        if (holder.is(BreezyBiomeTags.NO_WIND)) return;
        double chance;
        if (holder.is(BreezyBiomeTags.LESS_WIND)) {
            chance = BreezyConfig.CLIENT.lowWindFrequency.get();
        } else if (holder.is(BreezyBiomeTags.MORE_WIND)) {
            chance = BreezyConfig.CLIENT.highWindFrequency.get();
        } else {
            chance = BreezyConfig.CLIENT.defaultWindFrequency.get();
        }
        if (random.nextFloat() <= chance * 0.015) {
            level.addParticle(BreezyParticles.WIND.get(), (double) pos.getX() + random.nextDouble(), pos.getY() +
                    random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }

    public static EntityHitResult handlePassengerWoes(Projectile projectile, EntityHitResult result) {
        System.out.println(result.getEntity());
        if (result.getEntity() instanceof HotAirBalloonEntity balloon) {
            for (double i = 0; i < 5; i += 0.5) {
                Vec3 point = projectile.getEyePosition(1.0F).add(projectile.getDeltaMovement().scale(i));
//                System.out.println(point);
                projectile.level().addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
            }
            if (balloon.getFirstPassenger() != null) {
                result = new EntityHitResult(balloon.getFirstPassenger());
                return result;
            }
            if (isNotTargetingBalloonOrBasket(projectile, balloon, projectile.getDeltaMovement())) {
                projectile.hasImpulse = true;
                System.out.println("This projectile has been deemed innocent.");
                return null;
            }
        }
        return result;
    }
}
