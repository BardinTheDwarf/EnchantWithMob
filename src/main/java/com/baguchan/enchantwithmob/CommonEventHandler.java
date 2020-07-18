package com.baguchan.enchantwithmob;

import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.capability.MobEnchantHandler;
import com.baguchan.enchantwithmob.message.EnchantedMessage;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
                    if (world.getRandom().nextFloat() < (0.005F * world.getDifficulty().getId()) + world.getDifficultyForLocation(livingEntity.func_233580_cy_()).getClampedAdditionalDifficulty() * 0.1F) {
                        if (!world.isRemote()) {
                            livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                            {
                                MobEnchant mobEnchant = MobEnchants.byId(world.getRandom().nextInt(MobEnchants.getRegistry().getValues().size()));
                                int i = 0;
                                switch (world.getDifficulty()) {
                                    case EASY:
                                        i = MathHelper.clamp(1 + world.getRandom().nextInt(2), 0, mobEnchant.getMaxLevel());

                                        cap.addMobEnchant(livingEntity, mobEnchant, i);
                                        break;
                                    case NORMAL:
                                        i = MathHelper.clamp(1 + world.getRandom().nextInt(4), 0, mobEnchant.getMaxLevel());

                                        cap.addMobEnchant(livingEntity, mobEnchant, i);
                                        break;
                                    case HARD:
                                        i = MathHelper.clamp(2 + world.getRandom().nextInt(4), 0, mobEnchant.getMaxLevel());

                                        cap.addMobEnchant(livingEntity, mobEnchant, i);
                                        break;
                                }
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
                        for (int i = 0; i < cap.getMobEnchants().size(); i++) {
                            EnchantedMessage message = new EnchantedMessage(livingEntity, cap.getMobEnchants().get(i));
                            EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity), message);
                        }
                    }
                });
            }
        }

        if (!livingEntity.world.isRemote) {
            livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                for (MobEnchantHandler enchantHandler : cap.getMobEnchants()) {
                    enchantHandler.getMobEnchant().tick(livingEntity, enchantHandler.getEnchantLevel());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            if (cap.hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.mobEnchants, MobEnchants.PROTECTION)) {
                event.setAmount(getDamageReduction(event.getAmount(), cap));
            }
        });

        if (event.getSource().getTrueSource() instanceof LivingEntity) {
            LivingEntity attaker = (LivingEntity) event.getSource().getTrueSource();

            attaker.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                if (cap.hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.mobEnchants, MobEnchants.STRONG)) {
                    event.setAmount(getDamageAddition(event.getAmount(), cap));
                }
            });
        }
    }

    public static float getDamageReduction(float damage, MobEnchantCapability cap) {
        int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.PROTECTION);
        if (i > 0) {
            damage -= (double) MathHelper.floor(damage * (double) ((float) i * 0.15F));
        }
        return damage;
    }

    public static float getDamageAddition(float damage, MobEnchantCapability cap) {
        int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.STRONG);
        if (i > 0) {
            damage += (double) MathHelper.floor(damage * (double) ((float) i * 0.15F));
        }
        return damage;
    }
}
