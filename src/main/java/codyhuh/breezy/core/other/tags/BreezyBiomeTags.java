package codyhuh.breezy.core.other.tags;

import codyhuh.breezy.Breezy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BreezyBiomeTags {
    public static final TagKey<Biome> LESS_WIND = biomeTag("less_wind");
    public static final TagKey<Biome> DEFAULT_WIND = biomeTag("default_wind");
    public static final TagKey<Biome> MORE_WIND = biomeTag("more_wind");

    public static TagKey<Biome> biomeTag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(Breezy.MOD_ID, name));
    }
}
