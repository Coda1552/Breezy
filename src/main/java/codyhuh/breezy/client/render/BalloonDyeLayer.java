package codyhuh.breezy.client.render;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BalloonDyeLayer extends GeoRenderLayer<HotAirBalloonEntity> {
    private static final ResourceLocation DYE_LOCATION = new ResourceLocation(Breezy.MOD_ID, "textures/entity/balloon_dye.png");
    private static final ResourceLocation MODEL = new ResourceLocation(Breezy.MOD_ID, "geo/entity/hot_air_balloon.geo.json");

    public BalloonDyeLayer(GeoRenderer<HotAirBalloonEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, HotAirBalloonEntity balloon, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType skinTexture = RenderType.entityCutoutNoCull(DYE_LOCATION);
        int color = balloon.getColor();
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        int[] dRGB = new int[]{255 - red, 255 - green, 255 - blue};
        this.getRenderer().reRender(this.getGeoModel().getBakedModel(MODEL), poseStack, bufferSource, balloon,
                skinTexture, bufferSource.getBuffer(skinTexture), partialTick, packedLight,
                OverlayTexture.NO_OVERLAY, dRGB[0], dRGB[1], dRGB[2], 1.0F);
    }
}

