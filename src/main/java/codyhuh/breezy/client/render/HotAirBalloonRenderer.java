package codyhuh.breezy.client.render;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class HotAirBalloonRenderer extends GeoEntityRenderer<HotAirBalloonEntity> {

    public HotAirBalloonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(Breezy.MOD_ID, "hot_air_balloon")));
        this.addRenderLayer(new BalloonDyeLayer(this));
    }

    @Override
    public RenderType getRenderType(HotAirBalloonEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick); //RenderType.entityTranslucent(texture);
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
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f1 / 10.0F * (float)animatable.getHurtDirection()));
        }

        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();

    }

    @Override
    public void preRender(PoseStack poseStack, HotAirBalloonEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        for (int i = 1; i <= 8; i++) {
            Optional<GeoBone> arm = model.getBone("sandBag" + i);
            arm.ifPresent(geoBone -> geoBone.setHidden(true));
        }

        for (int i = 1; i <= animatable.getSandbags(); i++) {
            Optional<GeoBone> arm = model.getBone("sandBag" + i);

            arm.ifPresent(geoBone -> geoBone.setHidden(false));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public boolean shouldShowName(@NotNull HotAirBalloonEntity p_114504_) {
        return false;
    }

}
