package com.baguchan.enchantwithmob.utils;

import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MobEnchantUtils {
    public static MobEnchant getTomeTypeFromNBT(@Nullable CompoundNBT tag) {
        return tag == null ? null : MobEnchants.getRegistry().getValue(ResourceLocation.tryCreate(tag.getString("MobEnchant")));

    }
}
