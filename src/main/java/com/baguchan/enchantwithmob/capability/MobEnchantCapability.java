package com.baguchan.enchantwithmob.capability;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.message.EnchantedMessage;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MobEnchantCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    public List<MobEnchantHandler> mobEnchants = Lists.newArrayList();

    public void addMobEnchant(LivingEntity entity, MobEnchant mobEnchant, int enchantLevel) {

        this.mobEnchants.add(new MobEnchantHandler(mobEnchant, enchantLevel));
        //Sync Client Enchant
        if (!entity.world.isRemote) {
            EnchantedMessage message = new EnchantedMessage(entity, mobEnchant, enchantLevel);
            EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        }
    }

    public List<MobEnchantHandler> getMobEnchants() {
        return mobEnchants;
    }

    public boolean hasEnchant() {
        return !this.mobEnchants.isEmpty();
    }


    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == EnchantWithMob.MOB_ENCHANT_CAP ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < mobEnchants.size(); i++) {
            listnbt.add(mobEnchants.get(i).writeNBT());
        }

        nbt.put("StoredMobEnchants", listnbt);

        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT list = MobEnchantUtils.getEnchantmentListForNBT(nbt);

        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT compoundnbt = list.getCompound(i);

            mobEnchants.add(new MobEnchantHandler(MobEnchantUtils.getEnchantFromNBT(compoundnbt), MobEnchantUtils.getEnchantLevelFromNBT(compoundnbt)));
        }
    }
}