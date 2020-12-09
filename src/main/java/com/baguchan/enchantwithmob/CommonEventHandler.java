package com.baguchan.enchantwithmob;

import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.capability.MobEnchantHandler;
import com.baguchan.enchantwithmob.message.MobEnchantedMessage;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import com.baguchan.enchantwithmob.registry.ModItems;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class CommonEventHandler {
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(new ResourceLocation(EnchantWithMob.MODID, "mob_enchant"), new MobEnchantCapability());
        }
    }

    @SubscribeEvent
    public static void onSpawnEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof LivingEntity) {
            IWorld world = event.getWorld();

            if (world.getWorldInfo().getGameRulesInstance().get(GameRules.MOB_GRIEFING).get() && EnchantConfig.COMMON.naturalSpawnEnchantedMob.get()) {
                LivingEntity livingEntity = (LivingEntity) event.getEntity();
                if (!(livingEntity instanceof AnimalEntity) || EnchantConfig.COMMON.spawnEnchantedAnimal.get()) {
                    if (event.getSpawnReason() != SpawnReason.BREEDING && event.getSpawnReason() != SpawnReason.CONVERSION && event.getSpawnReason() != SpawnReason.STRUCTURE && event.getSpawnReason() != SpawnReason.MOB_SUMMONED) {
                        if (world.getRandom().nextFloat() < (0.005F * world.getDifficulty().getId()) + world.getDifficultyForLocation(livingEntity.getPosition()).getClampedAdditionalDifficulty() * 0.05F) {
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

                                    if (mobEnchant == MobEnchants.HEALTH_BOOST) {
                                        livingEntity.setHealth(livingEntity.getMaxHealth());
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }


    }

    @SubscribeEvent
    public static void onEntitySpawnSpecial(EntityJoinWorldEvent event) {


        if (!event.getEntity().world.isRemote && event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();

            livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                if (!cap.hasEnchant() && EnchantConfig.COMMON.enchantedBoss.get() && !livingEntity.isNonBoss()) {
                    if (livingEntity.world.getWorldInfo().getGameRulesInstance().get(GameRules.MOB_GRIEFING).get() && EnchantConfig.COMMON.naturalSpawnEnchantedMob.get()) {
                        MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, livingEntity.world.getRandom(), 10, true);
                    }
                }
            });
        }

    }

    @SubscribeEvent
    public static void onUpdateEnchanted(LivingEvent.LivingUpdateEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

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

        if (event.getSource().getTrueSource() instanceof LivingEntity) {
            LivingEntity attaker = (LivingEntity) event.getSource().getTrueSource();

            attaker.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                 if (cap.hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.mobEnchants, MobEnchants.STRONG)) {
                     //make snowman stronger
                     if (event.getAmount() == 0) {
                         event.setAmount(getDamageAddition(1, cap));
                     } else {
                         event.setAmount(getDamageAddition(event.getAmount(), cap));
                     }
                 }
            });

            livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                if (cap.hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.mobEnchants, MobEnchants.THORN)) {
                    int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.THORN);

                    if (livingEntity.getRNG().nextFloat() < i * 0.05F) {
                        attaker.attackEntityFrom(DamageSource.causeThornsDamage(livingEntity), getThornDamage(event.getAmount(), cap));
                    }
                }
            });
        }

        livingEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            if (cap.hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.mobEnchants, MobEnchants.PROTECTION)) {
                event.setAmount(getDamageReduction(event.getAmount(), cap));
            }
        });
    }

    public static float getDamageAddition(float damage, MobEnchantCapability cap) {
        int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.STRONG);
        if (level > 0) {
            damage += 1.0F + (float) Math.max(0, level - 1) * 1.0F;
        }
        return damage;
    }

    public static float getDamageReduction(float damage, MobEnchantCapability cap) {
        int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.PROTECTION);
        if (i > 0) {
            damage -= (double) MathHelper.floor(damage * (double) ((float) i * 0.15F));
        }
        return damage;
    }

    public static float getThornDamage(float damage, MobEnchantCapability cap) {
        int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.mobEnchants, MobEnchants.THORN);
        if (i > 0) {
            damage = (float) MathHelper.floor(damage * (double) ((float) i * 0.15F));
        }
        return damage;
    }


    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity entityTarget = event.getTarget();

        if (stack.getItem() == ModItems.MOB_ENCHANT_BOOK && !event.getPlayer().getCooldownTracker().hasCooldown(stack.getItem())) {
            if (entityTarget instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityTarget;
                if (MobEnchantUtils.hasMobEnchant(stack)) {

                    target.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                    {
                        MobEnchantUtils.addItemMobEnchantToEntity(stack, target, cap);
                        event.getPlayer().playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

                        stack.damageItem(1, event.getPlayer(), (entity) -> entity.sendBreakAnimation(event.getHand()));

                        event.getPlayer().getCooldownTracker().setCooldown(stack.getItem(), 40);

                        event.setCancellationResult(ActionResultType.SUCCESS);
                        event.setCanceled(true);
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack stack1 = event.getLeft();
        ItemStack stack2 = event.getRight();

        if (stack1.getItem() == ModItems.MOB_ENCHANT_BOOK && stack2.getItem() == ModItems.MOB_ENCHANT_BOOK) {
            Map<MobEnchant, Integer> map = MobEnchantUtils.getEnchantments(stack1);

            Map<MobEnchant, Integer> map1 = MobEnchantUtils.getEnchantments(stack2);
            boolean flag2 = false;
            boolean flag3 = false;

            for (MobEnchant enchantment1 : map1.keySet()) {
                if (enchantment1 != null) {
                    int i2 = map.getOrDefault(enchantment1, 0);
                    int j2 = map1.get(enchantment1);
                    j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                    boolean flag1 = true;

                    for (MobEnchant enchantment : map.keySet()) {
                        if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                            flag1 = false;
                        }
                    }

                    if (!flag1) {
                        flag3 = true;
                    } else {
                        flag2 = true;
                        if (j2 > enchantment1.getMaxLevel()) {
                            j2 = enchantment1.getMaxLevel();
                        }

                        map.put(enchantment1, j2);
                        int k3 = 0;
                        switch (enchantment1.getRarity()) {
                            case COMMON:
                                k3 = 1;
                                break;
                            case UNCOMMON:
                                k3 = 2;
                                break;
                            case RARE:
                                k3 = 4;
                                break;
                            case VERY_RARE:
                                k3 = 8;
                        }
                    }
                }
            }
            if (!stack1.isEmpty()) {
                int k2 = stack1.getRepairCost();
                if (!stack2.isEmpty() && k2 < stack2.getRepairCost()) {
                    k2 = stack2.getRepairCost();
                }

                ItemStack stack3 = new ItemStack(stack1.getItem());

                MobEnchantUtils.setEnchantments(map, stack3);
                stack3.setRepairCost(4 + k2);
                event.setOutput(stack3);
                event.setCost(4 + k2);
                event.setMaterialCost(1);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity)
            player.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(handler -> {
                for (int i = 0; i < handler.getMobEnchants().size(); i++) {
                    EnchantWithMob.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MobEnchantedMessage(player, handler.getMobEnchants().get(i)));

                }
            });
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity playerEntity = event.getPlayer();
        playerEntity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(handler -> {
            for (int i = 0; i < handler.getMobEnchants().size(); i++) {
                EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new MobEnchantedMessage(playerEntity, handler.getMobEnchants().get(i)));
            }
        });
    }
}
