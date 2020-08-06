package com.baguchan.enchantwithmob.registry;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.mobenchant.ProtectionMobEnchant;
import com.baguchan.enchantwithmob.mobenchant.SpeedyMobEnchant;
import com.baguchan.enchantwithmob.mobenchant.StrongMobEnchant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobEnchants {
    public static final MobEnchant PROTECTION = new ProtectionMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 4));
    public static final MobEnchant SPEEDY = new SpeedyMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.RARE, 2));
    public static final MobEnchant STRONG = new StrongMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 3));
    public static final MobEnchant THORN = new MobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 3));

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
                THORN.setRegistryName("thorn"));
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