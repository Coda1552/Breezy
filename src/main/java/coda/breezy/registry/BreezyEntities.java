package coda.breezy.registry;

import coda.breezy.Breezy;
import coda.breezy.common.entities.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BreezyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Breezy.MOD_ID);

    public static final RegistryObject<EntityType<HotAirBalloonEntity>> HOT_AIR_BALLOON = create("hot_air_balloon", EntityType.Builder.of(HotAirBalloonEntity::new, MobCategory.MISC).sized(1.2f, 2.0f));

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(Breezy.MOD_ID + "." + name));
    }
}
