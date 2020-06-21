package com.baguchan.enchantwithmob.message;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EnchantedMessage {
    private int entityId;
    private MobEnchant enchantType;

    public EnchantedMessage(Entity entity, MobEnchant enchantType) {
        this.entityId = entity.getEntityId();
        this.enchantType = enchantType;
    }

    public EnchantedMessage(int entityId, MobEnchant enchantType) {
        this.entityId = entityId;
        this.enchantType = enchantType;
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeString(this.enchantType.getRegistryName().toString());
    }

    public static EnchantedMessage deserialize(PacketBuffer buffer) {
        int entityId = buffer.readInt();
        MobEnchant enchantType = MobEnchantUtils.getEnchantFromString(buffer.readString());
        return new EnchantedMessage(entityId, enchantType);
    }

    public static boolean handle(EnchantedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().player.world.getEntityByID(message.entityId);
                if (entity != null && entity instanceof LivingEntity) {
                    entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP, null).ifPresent(enchantCap -> enchantCap.setMobEnchant((LivingEntity) entity, message.enchantType));
                }
            });
        }

        return true;
    }
}