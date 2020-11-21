package com.baguchan.enchantwithmob.client.render.layer;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation ENCHANT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.DiffuseLightingState DIFFUSE_LIGHTING_ENABLED = new RenderState.DiffuseLightingState(true);
    protected static final RenderState.FogState BLACK_FOG = new RenderState.FogState("black_fog", () -> {
        RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.enableFog();
    }, () -> {
        FogRenderer.applyFog();
        RenderSystem.disableFog();
    });
    protected static final RenderState.AlphaState DEFAULT_ALPHA = new RenderState.AlphaState(0.003921569F);
    protected static final RenderState.OverlayState OVERLAY_ENABLED = new RenderState.OverlayState(true);
    protected static final RenderState.CullState CULL_DISABLED = new RenderState.CullState(false);

    public EnchantLayer(IEntityRenderer<T, M> p_i50947_1_) {
        super(p_i50947_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        entitylivingbaseIn.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            if (cap.hasEnchant() && !entitylivingbaseIn.isInvisible()) {
                float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
                EntityModel<T> entitymodel = this.func_225635_b_();
                entitymodel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                this.getEntityModel().copyModelAttributesTo(entitymodel);
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(getEnergyLight(this.getEnchantTexture(), this.func_225634_a_(f), f * 0.01F));
                entitymodel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                entitymodel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            }
        });
    }

    public static RenderType getEnergyLight(ResourceLocation locationIn, float uIn, float vIn) {
        return RenderType.makeType("energy_light", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).texturing(new RenderState.OffsetTexturingState(uIn, vIn)).fog(BLACK_FOG).transparency(ADDITIVE_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).overlay(OVERLAY_ENABLED).build(false));
    }

    protected float func_225634_a_(float p_225634_1_) {
        return p_225634_1_ * 0.01F;
    }

    protected ResourceLocation getEnchantTexture() {
        return ENCHANT_TEXTURE;
    }

    protected EntityModel<T> func_225635_b_() {
        return this.getEntityModel();
    }
}