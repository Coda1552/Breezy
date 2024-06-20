package codyhuh.breezy.client.render;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

public class BalloonDyeLayer extends GeoLayerRenderer<HotAirBalloonEntity> {
    private static final ResourceLocation DYE_LOCATION = new ResourceLocation(Breezy.MOD_ID, "textures/entity/balloon_dye.png");
    private static final ResourceLocation MODEL = new ResourceLocation(Breezy.MOD_ID, "geo/hot_air_balloon.geo.json");

    public BalloonDyeLayer(GeoEntityRenderer<HotAirBalloonEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, HotAirBalloonEntity balloon,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType skinTexture = RenderType.entityCutoutNoCull(DYE_LOCATION);
        int color = balloon.getColor();
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        int[] dRGB = new int[]{255 - red, 255 - green, 255 - blue};
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), balloon, partialTicks, skinTexture,
                matrixStackIn, bufferIn, bufferIn.getBuffer(skinTexture), packedLightIn,
                OverlayTexture.NO_OVERLAY, dRGB[0], dRGB[1], dRGB[2], 1.0F);
    }
}
