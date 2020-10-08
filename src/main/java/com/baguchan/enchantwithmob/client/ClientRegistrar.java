package com.baguchan.enchantwithmob.client;

import com.baguchan.enchantwithmob.client.render.layer.EnchantLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@OnlyIn(Dist.CLIENT)
public class ClientRegistrar {
    public static void loadComplete(FMLLoadCompleteEvent evt) {
        Minecraft.getInstance().getRenderManager().renderers.values().forEach(r -> {
            if (r instanceof LivingRenderer) {
                ((LivingRenderer) r).addLayer(new EnchantLayer((LivingRenderer) r));
            }
        });
    }
}
