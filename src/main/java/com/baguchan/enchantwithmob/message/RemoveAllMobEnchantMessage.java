package com.baguchan.enchantwithmob.message;

import com.baguchan.enchantwithmob.EnchantWithMob;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RemoveAllMobEnchantMessage {
    private int entityId;

    public RemoveAllMobEnchantMessage(Entity entity) {
        this.entityId = entity.getEntityId();
    }

    public RemoveAllMobEnchantMessage(int id) {
        this.entityId = id;
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
    }

    public static RemoveAllMobEnchantMessage deserialize(PacketBuffer buffer) {
        int entityId = buffer.readInt();

        return new RemoveAllMobEnchantMessage(entityId);
    }

    public static boolean handle(RemoveAllMobEnchantMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().player.world.getEntityByID(message.entityId);
                if (entity != null && entity instanceof LivingEntity) {
                    entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP, null).ifPresent(enchantCap ->
                    {
                        enchantCap.removeAllMobEnchant((LivingEntity) entity);
                    });
                }
            });
        }

        return true;
    }
}