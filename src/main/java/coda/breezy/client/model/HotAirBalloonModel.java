package coda.breezy.client.model;

import coda.breezy.common.entities.HotAirBalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class HotAirBalloonModel<T extends HotAirBalloonEntity> extends EntityModel<T> {
	private final ModelPart balloon;
	public final ModelPart sandBags;
	public final ModelPart sandBag1;
	public final ModelPart sandBag2;
	public final ModelPart sandBag3;
	public final ModelPart sandBag4;
	public final ModelPart sandBag5;
	public final ModelPart sandBag6;
	public final ModelPart sandBag7;
	public final ModelPart sandBag8;

	public HotAirBalloonModel(ModelPart root) {
		this.balloon = root.getChild("balloon");
		this.sandBags = root.getChild("sandBags");
		this.sandBag1 = sandBags.getChild("sandBag1");
		this.sandBag1.visible = false;
		this.sandBag2 = sandBags.getChild("sandBag2");
		this.sandBag2.visible = false;
		this.sandBag3 = sandBags.getChild("sandBag3");
		this.sandBag3.visible = false;
		this.sandBag4 = sandBags.getChild("sandBag4");
		this.sandBag4.visible = false;
		this.sandBag5 = sandBags.getChild("sandBag5");
		this.sandBag5.visible = false;
		this.sandBag6 = sandBags.getChild("sandBag6");
		this.sandBag6.visible = false;
		this.sandBag7 = sandBags.getChild("sandBag7");
		this.sandBag7.visible = false;
		this.sandBag8 = sandBags.getChild("sandBag8");
		this.sandBag8.visible = false;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition balloon = partdefinition.addOrReplaceChild("balloon", CubeListBuilder.create().texOffs(0, 64).addBox(-11.0F, -3.0F, -11.0F, 22.0F, 6.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(-16.0F, -35.0F, -16.0F, 32.0F, 32.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

		PartDefinition ropes = balloon.addOrReplaceChild("ropes", CubeListBuilder.create().texOffs(64, 92).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 24.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

		PartDefinition basket = ropes.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(0, 114).addBox(-10.0F, 6.0F, -10.0F, 20.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(0, 92).addBox(-10.0F, -6.0F, -10.0F, 20.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(66, 64).addBox(-10.0F, -6.0F, 7.0F, 20.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(98, 0).addBox(7.0F, -6.0F, -7.0F, 3.0F, 12.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -6.0F, -7.0F, 3.0F, 12.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 30.0F, 0.0F));

		PartDefinition sandBags = partdefinition.addOrReplaceChild("sandBags", CubeListBuilder.create(), PartPose.offset(9.0F, 26.0F, -9.0F));

		PartDefinition sandBag1 = sandBags.addOrReplaceChild("sandBag1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, -1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -14.5F, -1.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag1_r1 = sandBag1.addOrReplaceChild("sandBag1_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag2 = sandBags.addOrReplaceChild("sandBag2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, -1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -14.5F, -1.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag2_r1 = sandBag2.addOrReplaceChild("sandBag2_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag3 = sandBags.addOrReplaceChild("sandBag3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, -1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -14.5F, 19.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag3_r1 = sandBag3.addOrReplaceChild("sandBag3_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag4 = sandBags.addOrReplaceChild("sandBag4", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, -1).mirror().addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -14.5F, 19.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag4_r1 = sandBag4.addOrReplaceChild("sandBag4_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition sandBag5 = sandBags.addOrReplaceChild("sandBag5", CubeListBuilder.create().texOffs(0, -1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-19.0F, -14.5F, 4.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition sandBag5_r1 = sandBag5.addOrReplaceChild("sandBag5_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition sandBag6 = sandBags.addOrReplaceChild("sandBag6", CubeListBuilder.create().texOffs(0, -1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-19.0F, -14.5F, 14.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition sandBag6_r1 = sandBag6.addOrReplaceChild("sandBag6_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition sandBag7 = sandBags.addOrReplaceChild("sandBag7", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(0.0F, -1.0F, -0.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 0).mirror().addBox(-0.5F, -1.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -14.5F, 14.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition sandBag7_r1 = sandBag7.addOrReplaceChild("sandBag7_r1", CubeListBuilder.create().texOffs(0, 6).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition sandBag8 = sandBags.addOrReplaceChild("sandBag8", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(0.0F, -1.0F, -10.5F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 0).mirror().addBox(-0.5F, -1.0F, -10.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -14.5F, 14.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition sandBag8_r1 = sandBag8.addOrReplaceChild("sandBag8_r1", CubeListBuilder.create().texOffs(0, 6).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F, 0.0F, 0.0F, 0.3491F));

		return LayerDefinition.create(meshdefinition, 136, 136);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		poseStack.translate(0.0F, -1.5F, 0.0F);

		balloon.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sandBags.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();

	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

	}
}