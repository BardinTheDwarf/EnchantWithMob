package com.baguchan.enchantwithmob.client.render.layer;

import com.baguchan.enchantwithmob.client.model.EnchanterModel;
import com.baguchan.enchantwithmob.entity.EnchanterEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class HeldBookLayer<T extends EnchanterEntity> extends LayerRenderer<T, EnchanterModel<T>> {
    protected final BookModel bookModel = new BookModel();


    public HeldBookLayer(IEntityRenderer<T, EnchanterModel<T>> enchanterRenderer) {
        super(enchanterRenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isAlive()) {
            float bookAnimation = entitylivingbaseIn.getBookAnimationScale(partialTicks);

            float f = MathHelper.interpolateAngle(partialTicks, entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset);

            matrixStackIn.push();
            matrixStackIn.translate(0.0D, 1.1625D, 0.0F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f + 90F));
            matrixStackIn.translate(-0.575D, 0.0D, 0.0D);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(60.0F * bookAnimation));
            this.bookModel.func_228247_a_(0.0F, MathHelper.clamp(bookAnimation, 0.0F, 0.1F), MathHelper.clamp(bookAnimation, 0.0F, 0.9F), bookAnimation);
            IVertexBuilder ivertexbuilder = EnchantmentTableTileEntityRenderer.TEXTURE_BOOK.getBuffer(bufferIn, RenderType::getEntitySolid);
            this.bookModel.func_228249_b_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
        }
    }
}
