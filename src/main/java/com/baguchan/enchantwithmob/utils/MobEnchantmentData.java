package com.baguchan.enchantwithmob.utils;

import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import net.minecraft.util.WeightedRandom;

public class MobEnchantmentData extends WeightedRandom.Item {
    public final MobEnchant enchantment;
    public final int enchantmentLevel;

    public MobEnchantmentData(MobEnchant enchantmentObj, int enchLevel) {
        super(enchantmentObj.getRarity().getWeight());
        this.enchantment = enchantmentObj;
        this.enchantmentLevel = enchLevel;
    }
}