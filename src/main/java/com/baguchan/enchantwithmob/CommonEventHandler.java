package com.baguchan.enchantwithmob;

import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.message.EnchantedMessage;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class CommonEventHandler {
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {

            if (!(event.getObject() instanceof PlayerEntity)) {
                event.addCapability(new ResourceLocation(EnchantWithMob.MODID, "mob_enchant"), new MobEnchantCapability());
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof LivingEntity) {

            if (!event.getWorld().isRemote) {
                event.getEntity().getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                {
                    //Sync Client Enchant
                    EnchantedMessage message = new EnchantedMessage(event.getEntity(), cap.getMobEnchant());
                    EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> event.getEntity()), message);
                });
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            if (cap.hasEnchant() && cap.getMobEnchant() == MobEnchants.PROTECTION) {
                event.setAmount(event.getAmount() * 0.75F);
            }
        });

        if (event.getSource().getTrueSource() instanceof LivingEntity) {
            LivingEntity attaker = (LivingEntity) event.getSource().getTrueSource();

            attaker.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                if (cap.hasEnchant() && cap.getMobEnchant() == MobEnchants.STRONG) {
                    event.setAmount(event.getAmount() * 1.25F);
                }
            });
        }
    }
}
