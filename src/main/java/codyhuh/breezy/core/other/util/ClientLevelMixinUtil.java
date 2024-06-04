package codyhuh.breezy.core.other.util;

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

public class ClientLevelMixinUtil {
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
}
