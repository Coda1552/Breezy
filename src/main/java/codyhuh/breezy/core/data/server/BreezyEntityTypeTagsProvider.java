package codyhuh.breezy.core.data.server;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.core.other.tags.BreezyEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BreezyEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public BreezyEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, Breezy.MOD_ID, helper);
    }

    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BreezyEntityTypeTags.HOT_ONES).add(EntityType.BLAZE).add(EntityType.MAGMA_CUBE)
                .addOptional(new ResourceLocation("alexsmobs", "laviathan"))
                .addOptional(new ResourceLocation("alexsmobs", "sunbird"))
                .addOptional(new ResourceLocation("alexscaves", "nucleeper"))
                .addOptional(new ResourceLocation("alexscaves", "gammaroach"))
                .addOptional(new ResourceLocation("alexscaves", "radgill"))
                .addOptional(new ResourceLocation("alexscaves", "raycat"))
        ;
    }
}
