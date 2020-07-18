package com.baguchan.enchantwithmob.capability;

import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.nbt.CompoundNBT;

public class MobEnchantHandler {
    private MobEnchant mobEnchant;
    private int enchantLevel;

    public MobEnchantHandler(MobEnchant mobEnchant, int enchantLevel) {
        this.mobEnchant = mobEnchant;
        this.enchantLevel = enchantLevel;
    }


    public MobEnchant getMobEnchant() {
        return mobEnchant;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public CompoundNBT writeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        if (mobEnchant != null) {
            nbt.putString("MobEnchant", mobEnchant.getRegistryName().toString());
            nbt.putInt("EnchantLevel", enchantLevel);
        }

        return nbt;
    }

    public void readNBT(CompoundNBT nbt) {
        mobEnchant = MobEnchantUtils.getEnchantFromNBT(nbt);
        enchantLevel = MobEnchantUtils.getEnchantLevelFromNBT(nbt);
    }
}
