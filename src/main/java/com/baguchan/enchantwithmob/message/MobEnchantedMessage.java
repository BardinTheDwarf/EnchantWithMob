package com.baguchan.enchantwithmob.message;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.capability.MobEnchantHandler;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MobEnchantedMessage {
    private int entityId;
    private MobEnchant enchantType;
    private int level;

    public MobEnchantedMessage(Entity entity, MobEnchantHandler enchantType) {
        this.entityId = entity.getEntityId();
        this.enchantType = enchantType.getMobEnchant();
        this.level = enchantType.getEnchantLevel();
    }

    public MobEnchantedMessage(int id, MobEnchantHandler enchantType) {
        this.entityId = id;
        this.enchantType = enchantType.getMobEnchant();
        this.level = enchantType.getEnchantLevel();
    }

    public MobEnchantedMessage(Entity entity, MobEnchant enchantType, int level) {
        this.entityId = entity.getEntityId();
        this.enchantType = enchantType;
        this.level = level;
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeString(this.enchantType.toString());
        buffer.writeInt(this.level);
    }

    public static MobEnchantedMessage deserialize(PacketBuffer buffer) {
        int entityId = buffer.readInt();
        MobEnchant enchantType = MobEnchantUtils.getEnchantFromString(buffer.readString());
        int level = buffer.readInt();

        return new MobEnchantedMessage(entityId, new MobEnchantHandler(enchantType, level));
    }

    public static boolean handle(MobEnchantedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().player.world.getEntityByID(message.entityId);
                if (entity != null && entity instanceof LivingEntity) {
                    entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP, null).ifPresent(enchantCap ->
                    {
                        enchantCap.addMobEnchant((LivingEntity) entity, message.enchantType, message.level);
                    });
                }
            });
        }

        return true;
    }
}