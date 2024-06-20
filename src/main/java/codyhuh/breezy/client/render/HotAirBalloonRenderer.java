package codyhuh.breezy.client.render;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.client.model.HotAirBalloonModel;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Optional;

public class HotAirBalloonRenderer extends GeoEntityRenderer<HotAirBalloonEntity> {

    public HotAirBalloonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HotAirBalloonModel());
        this.addLayer(new BalloonDyeLayer(this));
    }

    @Override
    public RenderType getRenderType(HotAirBalloonEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(HotAirBalloonEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float f = (float)animatable.getHurtTime() - partialTick;
        float f1 = animatable.getDamage() - partialTick;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f1 / 10.0F * (float)animatable.getHurtDir()));
        }

        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();

    }

    @Override
    public void renderEarly(HotAirBalloonEntity animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        GeoModel model = getGeoModelProvider().getModel(new ResourceLocation(Breezy.MOD_ID, "geo/hot_air_balloon.geo.json"));

        for (int i = 1; i <= 8; i++) {
            Optional<GeoBone> arm = model.getBone("sandBag" + i);
            arm.ifPresent(geoBone -> geoBone.setHidden(true));
        }

        for (int i = 1; i <= animatable.getSandbags(); i++) {
            Optional<GeoBone> arm = model.getBone("sandBag" + i);

            arm.ifPresent(geoBone -> geoBone.setHidden(false));
        }

        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
    }

}
