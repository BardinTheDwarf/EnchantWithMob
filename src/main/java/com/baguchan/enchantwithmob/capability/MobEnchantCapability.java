package com.baguchan.enchantwithmob.capability;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.message.EnchantedMessage;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobEnchantCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    private MobEnchant mobEnchant = null;
    private int enchantLevel;


    public MobEnchant getMobEnchant() {
        return mobEnchant;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setMobEnchant(LivingEntity entity, MobEnchant mobEnchant, int enchantLevel) {
        this.mobEnchant = mobEnchant;
        this.enchantLevel = enchantLevel;
        //Sync Client Enchant
        if (!entity.world.isRemote) {
            EnchantedMessage message = new EnchantedMessage(entity, mobEnchant);
            EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        }
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
            nbt.putInt("EnchantLevel", enchantLevel);
        }

        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        mobEnchant = MobEnchantUtils.getEnchantTypeFromNBT(nbt);
        enchantLevel = MobEnchantUtils.getEnchantLevelFromNBT(nbt);
    }
}