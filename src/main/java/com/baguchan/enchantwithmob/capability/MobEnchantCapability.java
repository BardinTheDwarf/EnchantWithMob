package com.baguchan.enchantwithmob.capability;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobEnchantCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    private MobEnchant mobEnchant;

    public void update(Entity entity) {

    }

    public MobEnchant getMobEnchant() {
        return mobEnchant;
    }

    public void setMobEnchant(MobEnchant mobEnchant) {
        this.mobEnchant = mobEnchant;
    }

    public boolean hasEnchant() {
        return this.mobEnchant != null;
    }


    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == EnchantWithMob.MOB_ENCHANT_CAP ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        if(mobEnchant != null) {
            nbt.putString("MobEnchant", mobEnchant.getRegistryName().toString());
        }

        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        mobEnchant = MobEnchantUtils.getTomeTypeFromNBT(nbt);
    }
}