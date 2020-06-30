package com.baguchan.enchantwithmob;

import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.message.EnchantedMessage;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
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
    public static void onSpawnEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof LivingEntity) {
            IWorld world = event.getWorld();


            if (EnchantConfig.COMMON.naturalSpawnEnchantedMob.get()) {
                LivingEntity livingEntity = (LivingEntity) event.getEntity();

                if (event.getSpawnReason() != SpawnReason.BREEDING && event.getSpawnReason() != SpawnReason.CONVERSION && event.getSpawnReason() != SpawnReason.STRUCTURE && event.getSpawnReason() != SpawnReason.MOB_SUMMONED) {
                    if (world.getRandom().nextFloat() < (0.005F * world.getDifficulty().getId()) + world.getDifficultyForLocation(livingEntity.getPosition()).getClampedAdditionalDifficulty() * 0.1F) {
                        if (!world.isRemote()) {
                            livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                            {
                                cap.setMobEnchant(livingEntity, MobEnchants.byId(MobEnchants.getRegistry().getValues().size()));
                            });
                        }
                    }
                }
            }
        }


    }

    @SubscribeEvent
    public static void onUpdateEnchanted(LivingEvent.LivingUpdateEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        if (livingEntity.getEntityWorld().getGameTime() % 80 == 0) {
            if (!livingEntity.world.isRemote) {
                livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                {
                    //Sync Client Enchant
                    if (cap.hasEnchant()) {
                        EnchantedMessage message = new EnchantedMessage(livingEntity, cap.getMobEnchant());
                        EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity), message);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
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
