package codyhuh.breezy.core.other;

import codyhuh.breezy.BreezyConfig;
import codyhuh.breezy.core.other.tags.BreezyBiomeTags;
import codyhuh.breezy.core.registry.BreezyParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MixinUtil {
    @OnlyIn(Dist.CLIENT)
    public static void tryAddWindParticle(ClientLevel level, BlockPos pos, RandomSource random) {
        if (!BreezyConfig.CLIENT.shouldDisplayWind.get()) return;
        if (!level.canSeeSky(pos)) return;
        double chance;
        Holder<Biome> holder = level.getBiome(pos);
        if (holder.is(BreezyBiomeTags.LESS_WIND)) {
            chance = BreezyConfig.CLIENT.lowWindFrequency.get();
        } else if (holder.is(BreezyBiomeTags.MORE_WIND)) {
            chance = BreezyConfig.CLIENT.highWindFrequency.get();
        } else {
            chance = BreezyConfig.CLIENT.defaultWindFrequency.get();
        }
        if (random.nextFloat() <= chance * 0.015) {
            level.addParticle(BreezyParticles.WIND.get(), (double)pos.getX() + random.nextDouble(), pos.getY() +
                    random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }
}
