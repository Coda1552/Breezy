package coda.breezy.client.render;

import coda.breezy.client.model.HotAirBalloonModel;
import coda.breezy.common.entities.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HotAirBalloonRenderer extends GeoEntityRenderer<HotAirBalloonEntity> {

    public HotAirBalloonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HotAirBalloonModel());
    }

    @Override
    public RenderType getRenderType(HotAirBalloonEntity animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public void render(GeoModel model, HotAirBalloonEntity animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        int sandbags = animatable.getSandbags();

        if (sandbags > 0) {
            for (int i = sandbags; i <= 8; i++) {
                model.getBone("sandBag" + i).get().setHidden(true);
            }
        }
        else {
            for (int i = 1; i <= 8; i++) {
                model.getBone("sandBag" + i).get().setHidden(true);
            }
        }
    }
}
