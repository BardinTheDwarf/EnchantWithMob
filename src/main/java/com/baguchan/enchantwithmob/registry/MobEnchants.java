package com.baguchan.enchantwithmob.registry;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobEnchants {
    public static final MobEnchant PROTECTION = new ProtectionMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 4));
    public static final MobEnchant TOUGH = new ProtectionMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2)).addAttributesModifier(Attributes.ARMOR, "313644c5-ead2-4670-b9eb-0b41d59ce5a2", (double) 2.0F, AttributeModifier.Operation.ADDITION).addAttributesModifier(Attributes.ARMOR_TOUGHNESS, "8135df8f-38d9-490a-8d6f-c908fa973b34", (double) 0.5F, AttributeModifier.Operation.ADDITION);
    public static final MobEnchant SPEEDY = new SpeedyMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.UNCOMMON, 2)).addAttributesModifier(Attributes.MOVEMENT_SPEED, "501f27a9-4a75-4c2e-a2ab-91eeed71d748", (double) 0.05F, AttributeModifier.Operation.ADDITION);
    public static final MobEnchant STRONG = new StrongMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 3));
    public static final MobEnchant THORN = new ThronEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 3));
    public static final MobEnchant HEALTH_BOOST = new HealthBoostMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 5)).addAttributesModifier(Attributes.MAX_HEALTH, "f5d32c9f-2a3d-4157-bbf7-469d348ce097", 2.0D, AttributeModifier.Operation.ADDITION);


    private static ForgeRegistry<MobEnchant> registry;

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = (ForgeRegistry<MobEnchant>) new RegistryBuilder<MobEnchant>()
                .setType(MobEnchant.class)
                .setName(new ResourceLocation(EnchantWithMob.MODID, "mob_enchant"))
                .setDefaultKey(new ResourceLocation(EnchantWithMob.MODID, "protection"))
                .create();
    }


    @SubscribeEvent
    public static void onRegisterEnchant(RegistryEvent.Register<MobEnchant> event) {
        event.getRegistry().registerAll(PROTECTION.setRegistryName("protection"),
                TOUGH.setRegistryName("tough"),
                SPEEDY.setRegistryName("speedy"),
                STRONG.setRegistryName("strong"),
                THORN.setRegistryName("thorn"),
                HEALTH_BOOST.setRegistryName("health_boost"));
    }

    public static ForgeRegistry<MobEnchant> getRegistry() {
        if (registry == null) {
            throw new IllegalStateException("Registry not yet initialized");
        }
        return registry;
    }

    public static int getId(MobEnchant enchant) {
        return registry.getID(enchant);
    }

    public static MobEnchant byId(int id) {
        return registry.getValue(id);
    }
}