package com.baguchan.enchantwithmob.client;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.client.render.layer.EnchantLayer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onRenderLayer(RenderLivingEvent event) {
        LivingEntity entity = event.getEntity();

        entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            if (!doesRendererHaveLayer(event.getRenderer(), EnchantLayer.class)) {
                event.getRenderer().addLayer(new EnchantLayer(event.getRenderer()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends LayerRenderer<?,?>> T getRenderLayer(LivingRenderer<?,?> renderer, Class<T> cls) {
        try {
            List<? extends LayerRenderer<?,?>> layers = renderer.layerRenderers;
            for(LayerRenderer<?,?> layer : layers) {
                if(cls == layer.getClass()) {
                    return (T) layer;
                }
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }



    public static boolean doesRendererHaveLayer(LivingRenderer<?,?> renderer, Class cls) {
        return getRenderLayer(renderer, cls) != null;
    }
}
