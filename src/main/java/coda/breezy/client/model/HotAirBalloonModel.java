package coda.breezy.client.model;

import coda.breezy.Breezy;
import coda.breezy.common.entities.HotAirBalloonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class HotAirBalloonModel extends AnimatedTickingGeoModel<HotAirBalloonEntity> {

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
        //return new ResourceLocation(breezy.MOD_ID, "animations/entity/hot_air_balloon.animation.json");
    }

}
