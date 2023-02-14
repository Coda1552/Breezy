package coda.breezy.client.render;

import coda.breezy.Breezy;
import coda.breezy.client.ClientEvents;
import coda.breezy.client.model.HotAirBalloonModel;
import coda.breezy.common.entities.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Optional;

public class HotAirBalloonRenderer extends EntityRenderer<HotAirBalloonEntity> {
    private static final ResourceLocation TEX = new ResourceLocation(Breezy.MOD_ID, "textures/entity/hot_air_balloon.png");
    private final HotAirBalloonModel<HotAirBalloonEntity> model;

    public HotAirBalloonRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new HotAirBalloonModel<>(context.bakeLayer(ClientEvents.BALLOON_MODEL));
    }

    @Override
    public ResourceLocation getTextureLocation(HotAirBalloonEntity entity) {
        return TEX;
    }

    @Override
    public void render(HotAirBalloonEntity entity, float p_114486_, float partialTick, PoseStack stack, MultiBufferSource source, int p_114490_) {
        super.render(entity, p_114486_, partialTick, stack, source, p_114490_);

        model.prepareMobModel(entity, 0.0F, 0.0F, partialTick);
        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);

        model.renderToBuffer(stack, source.getBuffer(RenderType.entityTranslucent(TEX)), p_114490_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        int sandbags = entity.getSandbags();

        for (int i = 1; i <= 8; i++) {
            ModelPart sandBagsPart = model.sandBags.getChild("sandBag" + i);

            sandBagsPart.visible = i > sandbags;
        }
    }
}
