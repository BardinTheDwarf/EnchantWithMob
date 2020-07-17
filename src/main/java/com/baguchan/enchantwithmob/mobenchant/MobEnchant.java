package com.baguchan.enchantwithmob.mobenchant;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class MobEnchant extends ForgeRegistryEntry<MobEnchant> {
    protected final Type enchantType;
    private final int level;

    public MobEnchant(Properties properties) {
        this.enchantType = properties.enchantType;
        this.level = properties.level;
    }

    public Type getEnchantType() {
        return enchantType;
    }

    public int getMaxLevel() {
        return level;
    }

    public static class Properties {
        private final Type enchantType;
        private final int level;

        public Properties(Type enchantType, int level) {
            this.enchantType = enchantType;
            this.level = level;
        }
    }

    public enum Type {
        normal,
        powerful,
        mystery
    }
}