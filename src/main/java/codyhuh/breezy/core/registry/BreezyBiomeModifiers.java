package codyhuh.breezy.core.registry;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.biome.BreezyBiomeModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BreezyBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Breezy.MOD_ID);

    public static final RegistryObject<Codec<BreezyBiomeModifier>> BREEZY_BIOME_MODIFIER = BIOME_MODIFIERS.register("breezy_bm", () -> Codec.unit(BreezyBiomeModifier.INSTANCE));

}