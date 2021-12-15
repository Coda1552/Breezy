package coda.whooosh.client.model;

import coda.whooosh.Whooosh;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HotAirBalloonModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Whooosh.MOD_ID, "balloon"), "main");
    public ModelPart balloon;
    public ModelPart ropes;
    public ModelPart basket;

    public HotAirBalloonModel(ModelPart root) {
        this.balloon = root.getChild("balloon");
        this.ropes = this.balloon.getChild("ropes");
        this.basket = this.ropes.getChild("basket");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();
        PartDefinition balloon = root.addOrReplaceChild("balloon", CubeListBuilder.create().texOffs(0, 64).addBox(-11.0F, -3.0F, -11.0F, 22.0F, 6.0F, 22.0F, false).texOffs(2, 0).addBox(-16.0F, -35.0F, -16.0F, 32.0F, 32.0F, 32.0F, false), PartPose.offsetAndRotation(0.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition ropes = balloon.addOrReplaceChild("ropes", CubeListBuilder.create().texOffs(64, 64).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 24.0F, 18.0F, false), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition basket = ropes.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(0, 114).addBox(-10.0F, 6.0F, -10.0F, 20.0F, 2.0F, 20.0F, false).texOffs(0, 92).addBox(-10.0F, -6.0F, -10.0F, 20.0F, 12.0F, 3.0F, false).texOffs(66, 64).addBox(-10.0F, -6.0F, 7.0F, 20.0F, 12.0F, 3.0F, false).texOffs(98, 0).addBox(-10.0F, -6.0F, -7.0F, 3.0F, 12.0F, 14.0F, false).texOffs(98, 0).addBox(7.0F, -6.0F, -7.0F, 3.0F, 12.0F, 14.0F, false), PartPose.offsetAndRotation(0.0F, 30.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 136, 136);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.balloon).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
