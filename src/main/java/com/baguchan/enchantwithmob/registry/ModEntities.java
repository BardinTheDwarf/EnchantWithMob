package com.baguchan.enchantwithmob.registry;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.client.render.EnchanterRenderer;
import com.baguchan.enchantwithmob.entity.EnchanterEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final EntityType<EnchanterEntity> ENCHANTER = EntityType.Builder.create(EnchanterEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F).build(prefix("enchanter"));

    private static String prefix(String path) {
        return EnchantWithMob.MODID + "." + path;
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(ENCHANTER.setRegistryName("enchanter"));
        GlobalEntityTypeAttributes.put(ENCHANTER, EnchanterEntity.getAttributeMap().create());
        Raid.WaveMember.create("enchanter", ENCHANTER, new int[]{0, 0, 1, 0, 1, 1, 2, 1});
    }

    @OnlyIn(Dist.CLIENT)
    public static void setupEntitiesClient() {
        RenderingRegistry.registerEntityRenderingHandler(ENCHANTER, EnchanterRenderer::new);
    }
}
