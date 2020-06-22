package com.baguchan.enchantwithmob.entity;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EnchanterEntity extends SpellcastingIllagerEntity {
    private LivingEntity enchantTarget;
    public EnchanterEntity(EntityType<? extends EnchanterEntity> type, World p_i48551_2_) {
        super(type, p_i48551_2_);
        this.experienceValue = 12;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new EnchanterEntity.CastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.8D, 1.15D));
        this.goalSelector.addGoal(3, new EnchanterEntity.SpellGoal());
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        if (super.isOnSameTeam(entityIn)) {
            return true;
        } else if (entityIn instanceof LivingEntity && ((LivingEntity) entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    private void setEnchantTarget(@Nullable LivingEntity enchantTargetIn) {
        this.enchantTarget = enchantTargetIn;
    }

    @Nullable
    public LivingEntity getEnchantTarget() {
        return enchantTarget;
    }

    @Override
    public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ILLUSIONER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ILLUSIONER_HURT;
    }

    @Override
    protected SoundEvent getSpellSound() {
        return SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE;
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AbstractIllagerEntity.ArmPose getArmPose() {
        if (this.isSpellcasting()) {
            return AbstractIllagerEntity.ArmPose.SPELLCASTING;
        } else {
            return AbstractIllagerEntity.ArmPose.CROSSED;
        }
    }

    class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
        private CastingSpellGoal() {
            super();
        }

        @Override
        public void tick() {
            if (EnchanterEntity.this.isSpellcasting() && EnchanterEntity.this.getEnchantTarget() != null) {
                EnchanterEntity.this.getLookController().setLookPositionWithEntity(EnchanterEntity.this.getEnchantTarget(), (float) EnchanterEntity.this.getHorizontalFaceSpeed(), (float) EnchanterEntity.this.getVerticalFaceSpeed());
            } else if (EnchanterEntity.this.isSpellcasting() && EnchanterEntity.this.getAttackTarget() != null) {
                EnchanterEntity.this.getLookController().setLookPositionWithEntity(EnchanterEntity.this.getAttackTarget(), (float) EnchanterEntity.this.getHorizontalFaceSpeed(), (float) EnchanterEntity.this.getVerticalFaceSpeed());
            }
        }
    }


    public class SpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
        private final Predicate<LivingEntity> fillter = (entity) -> {
            return !(entity instanceof EnchanterEntity) && entity instanceof IMob && entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).map(mob -> !mob.hasEnchant()).orElse(false);
        };

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (EnchanterEntity.this.getAttackTarget() == null) {
                return false;
            } else if (EnchanterEntity.this.isSpellcasting()) {
                return false;
            } else if (EnchanterEntity.this.ticksExisted < this.spellCooldown) {
                return false;
            } else {
                List<LivingEntity> list = EnchanterEntity.this.world.getEntitiesWithinAABB(LivingEntity.class, EnchanterEntity.this.getBoundingBox().grow(16.0D, 4.0D, 16.0D), this.fillter);
                if (list.isEmpty()) {
                    return false;
                } else {
                    EnchanterEntity.this.setEnchantTarget(list.get(EnchanterEntity.this.rand.nextInt(list.size())));
                    return true;
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EnchanterEntity.this.getEnchantTarget() != null && this.spellWarmup > 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            super.resetTask();
            EnchanterEntity.this.setEnchantTarget(null);
        }

        protected void castSpell() {
            LivingEntity entity = EnchanterEntity.this.getEnchantTarget();
            if (entity != null && entity.isAlive()) {
                entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
                {
                    cap.setMobEnchant(entity, MobEnchants.byId(MobEnchants.getRegistry().getValues().size()));
                });
            }
        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 160;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED;
        }

        protected SpellcastingIllagerEntity.SpellType getSpellType() {
            return SpellcastingIllagerEntity.SpellType.WOLOLO;
        }
    }
}
