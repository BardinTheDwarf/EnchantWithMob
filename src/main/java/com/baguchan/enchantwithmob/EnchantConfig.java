package com.baguchan.enchantwithmob;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnchantConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static boolean naturalSpawnEnchantedMob;
    public static boolean spawnEnchantedAnimal;

    static {
        Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void bakeConfig() {
        naturalSpawnEnchantedMob = COMMON.naturalSpawnEnchantedMob.get();
        spawnEnchantedAnimal = COMMON.spawnEnchantedAnimal.get();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == EnchantConfig.COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue naturalSpawnEnchantedMob;
        public final ForgeConfigSpec.BooleanValue spawnEnchantedAnimal;

        public Common(ForgeConfigSpec.Builder builder) {
            naturalSpawnEnchantedMob = builder
                    .translation(EnchantWithMob.MODID + ".config.naturalSpawnEnchantedMob")
                    .define("Enchanted Mob can Spawn Natural", true);
            spawnEnchantedAnimal = builder
                    .translation(EnchantWithMob.MODID + ".config.spawnEnchantedAnimal")
                    .define("Enchanted Animal can Spawn Natural", false);
        }
    }

}
