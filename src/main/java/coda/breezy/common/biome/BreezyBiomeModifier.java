package coda.breezy.common.biome;

import coda.breezy.BreezyConfig;
import coda.breezy.registry.BreezyBiomeModifiers;
import coda.breezy.registry.BreezyParticles;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class BreezyBiomeModifier implements BiomeModifier {
    public static final BreezyBiomeModifier INSTANCE = new BreezyBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            BiomeSpecialEffectsBuilder effects = builder.getSpecialEffects();

            if (BreezyConfig.Client.INSTANCE.shouldDisplayWind.get()) {
                if (!biome.is(BiomeTags.IS_OVERWORLD)) return;

                var biome1 = biome.unwrapKey().isPresent() ? biome.unwrapKey().get() : Biomes.PLAINS;

                AmbientParticleSettings def = new AmbientParticleSettings(BreezyParticles.WIND.get(), 0.0002F);
                AmbientParticleSettings low = new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.lowWindFrequency.get().floatValue() * 0.015F);
                AmbientParticleSettings med = new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.mediumWindFrequency.get().floatValue() * 0.015F);
                AmbientParticleSettings high = new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.highWindFrequency.get().floatValue() * 0.015F);

                if (biome1.equals(Biomes.FOREST) || biome1.equals(Biomes.TAIGA) || biome1.equals(Biomes.DESERT)) {
                    effects.ambientParticle(low);
                }
                else if (biome1.equals(Biomes.PLAINS) || biome1.equals(Biomes.SAVANNA)) {
                    effects.ambientParticle(med);
                }
                else if (biome1.equals(Biomes.SNOWY_SLOPES)
                        || biome1.equals(Biomes.STONY_PEAKS)
                        || biome1.equals(Biomes.JAGGED_PEAKS)
                        || biome1.equals(Biomes.FROZEN_PEAKS)) {
                    effects.ambientParticle(high);
                }
                else {
                    effects.ambientParticle(def);
                }
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return BreezyBiomeModifiers.BREEZY_BIOME_MODIFIER.get();
    }
}