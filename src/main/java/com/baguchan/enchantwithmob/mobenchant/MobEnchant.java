package com.baguchan.enchantwithmob.mobenchant;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class MobEnchant extends ForgeRegistryEntry<MobEnchant> {
    protected final Type enchantType;

    public MobEnchant(Properties properties) {
        this.enchantType = properties.enchantType;
    }

    public Type getEnchantType() {
        return enchantType;
    }

    public static class Properties {
        private final Type enchantType;

        public Properties(Type enchantType) {
            this.enchantType = enchantType;
        }
    }

    public enum Type {
        normal,
        powerful,
        mystery
    }
}