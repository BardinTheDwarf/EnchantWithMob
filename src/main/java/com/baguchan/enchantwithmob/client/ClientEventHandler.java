package com.baguchan.enchantwithmob.client;

import com.baguchan.enchantwithmob.EnchantWithMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
}
