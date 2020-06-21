package com.baguchan.enchantwithmob.registry;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.client.render.EnchanterRenderer;
import com.baguchan.enchantwithmob.entity.EnchanterEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY = new DeferredRegister<>(ForgeRegistries.ENTITIES, EnchantWithMob.MODID);

    public static final RegistryObject<EntityType<EnchanterEntity>> ENCHANTER = ENTITY.register("enchanter",() -> EntityType.Builder.create(EnchanterEntity::new, EntityClassification.CREATURE).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(0.6F, 1.95F).build(prefix("enchanter")));

    private static String prefix(String path) {
        return EnchantWithMob.MODID + "." + path;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setupEntitiesClient()
    {
        RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends EnchanterEntity>)ENCHANTER.get(), EnchanterRenderer::new);
    }
}
