package coda.whooosh.registry;

import coda.whooosh.Whooosh;
import coda.whooosh.common.entities.HotAirBalloonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WhoooshEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Whooosh.MOD_ID);

    public static final RegistryObject<EntityType<HotAirBalloonEntity>> HOT_AIR_BALLOON = create("hot_air_balloon", EntityType.Builder.of(HotAirBalloonEntity::new, MobCategory.MISC).sized(1.2f, 0.9f));

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(Whooosh.MOD_ID + "." + name));
    }
}
