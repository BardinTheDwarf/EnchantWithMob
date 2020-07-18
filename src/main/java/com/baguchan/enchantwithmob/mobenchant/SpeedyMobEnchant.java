package com.baguchan.enchantwithmob.mobenchant;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class SpeedyMobEnchant extends MobEnchant {
    public SpeedyMobEnchant(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(LivingEntity entity, int level) {
        super.tick(entity, level);

        if (entity.ticksExisted % 20 == 0) {
            entity.addPotionEffect(new EffectInstance(Effects.SPEED, 600, level, true, false));
        }
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + (enchantmentLevel - 1) * 10;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMinLevel() {
        return 10;
    }
}
