package codyhuh.breezy.core.other.tags;

import codyhuh.breezy.Breezy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class BreezyEntityTypeTags {
    public static final TagKey<EntityType<?>> HOT_ONES = entityTypeTag("hot_ones");

    public static TagKey<EntityType<?>> entityTypeTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Breezy.MOD_ID, name));
    }
}
