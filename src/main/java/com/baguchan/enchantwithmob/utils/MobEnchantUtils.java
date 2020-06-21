package com.baguchan.enchantwithmob.utils;

import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MobEnchantUtils {
    @Nullable
    public static MobEnchant getEnchantTypeFromNBT(@Nullable CompoundNBT tag) {
        if (tag != null && MobEnchants.getRegistry().containsKey(ResourceLocation.tryCreate(tag.getString("MobEnchant")))) {
            return MobEnchants.getRegistry().getValue(ResourceLocation.tryCreate(tag.getString("MobEnchant")));
        } else {
            return null;
        }
    }

    @Nullable
    public static MobEnchant getEnchantFromString(@Nullable String id) {
        if (id != null && MobEnchants.getRegistry().containsKey(ResourceLocation.tryCreate(id))) {
            return MobEnchants.getRegistry().getValue(ResourceLocation.tryCreate(id));
        } else {
            return null;
        }
    }
}
