package codyhuh.breezy.client.model;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HotAirBalloonModel extends AnimatedGeoModel<HotAirBalloonEntity> {

    @Override
    public ResourceLocation getModelResource(HotAirBalloonEntity object) {
        return new ResourceLocation(Breezy.MOD_ID, "geo/hot_air_balloon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HotAirBalloonEntity object) {
        return new ResourceLocation(Breezy.MOD_ID, "textures/entity/hot_air_balloon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HotAirBalloonEntity animatable) {
        return null;
    }
}
