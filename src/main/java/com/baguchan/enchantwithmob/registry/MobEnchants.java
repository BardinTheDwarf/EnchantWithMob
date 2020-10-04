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
    public static final MobEnchant SPEEDY = new SpeedyMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.RARE, 2)).addAttributesModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", (double) 0.05F, AttributeModifier.Operation.ADDITION);
    public static final MobEnchant STRONG = new StrongMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 3)).addAttributesModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", (double) 1.0F, AttributeModifier.Operation.ADDITION);
    public static final MobEnchant THORN = new ThronEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 3));
    public static final MobEnchant HEALTH_BOOST = new ThronEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 3)).addAttributesModifier(Attributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 2.0D, AttributeModifier.Operation.ADDITION);


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