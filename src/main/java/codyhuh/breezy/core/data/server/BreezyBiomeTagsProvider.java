package codyhuh.breezy.core.data.server;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.core.other.tags.BreezyBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.biome.Biomes.*;

public class BreezyBiomeTagsProvider extends BiomeTagsProvider {
    public BreezyBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, Breezy.MOD_ID, helper);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(BreezyBiomeTags.LESS_WIND).addTag(BiomeTags.IS_FOREST)
                .addTag(BiomeTags.IS_TAIGA).addTag(Tags.Biomes.IS_DESERT);
        this.tag(BreezyBiomeTags.MORE_WIND).addTag(Tags.Biomes.IS_MOUNTAIN)
                .add(WINDSWEPT_FOREST).add(WINDSWEPT_HILLS).add(WINDSWEPT_GRAVELLY_HILLS).add(WINDSWEPT_SAVANNA);
        this.tag(BreezyBiomeTags.NO_WIND).add(DEEP_DARK).add(DRIPSTONE_CAVES);
    }
}
