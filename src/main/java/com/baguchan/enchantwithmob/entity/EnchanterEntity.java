package com.baguchan.enchantwithmob.entity;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.registry.ModItems;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EnchanterEntity extends SpellcastingIllagerEntity {
    private LivingEntity enchantTarget;

    public float prevCapeX, prevCapeY, prevCapeZ;
    public float capeX, capeY, capeZ;
    private float clientSideBookAnimation0;
    private float clientSideBookAnimation;

    public EnchanterEntity(EntityType<? extends EnchanterEntity> type, World p_i48551_2_) {
        super(type, p_i48551_2_);
        this.experienceValue = 12;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new EnchanterEntity.CastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidTargetEntityGoal<>(this, MobEntity.class, 6.5F, 0.8D, 1.05D));
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

    public static AttributeModifierMap.MutableAttribute getAttributeMap() {
        return MonsterEntity.func_234295_eP_().func_233815_a_(Attributes.MOVEMENT_SPEED, (double) 0.3F).func_233815_a_(Attributes.MAX_HEALTH, 24.0D).func_233815_a_(Attributes.FOLLOW_RANGE, 24.0D);
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
    public void tick() {
        super.tick();

        if (this.world.isRemote) {
            if (this.clientSideBookAnimation != this.clientSideBookAnimation0) {
                this.recalculateSize();
            }

            this.clientSideBookAnimation0 = this.clientSideBookAnimation;
            if (this.isSpellcasting()) {
                this.clientSideBookAnimation = MathHelper.clamp(this.clientSideBookAnimation + 0.1F, 0.0F, 1.0F);
            } else {
                this.clientSideBookAnimation = MathHelper.clamp(this.clientSideBookAnimation - 0.15F, 0.0F, 1.0F);
            }
        }

        this.updateCape();
    }

    @OnlyIn(Dist.CLIENT)
    public float getBookAnimationScale(float tick) {
        return MathHelper.lerp(tick, this.clientSideBookAnimation0, this.clientSideBookAnimation) / 1.0F;
    }

    private void updateCape() {
        double elasticity = 0.25;
        double gravity = -0.1;
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        this.capeY += gravity;
        this.capeX += (this.getPosX() - this.capeX) * elasticity;
        this.capeY += (this.getPosY() - this.capeY) * elasticity;
        this.capeZ += (this.getPosZ() - this.capeZ) * elasticity;
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
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);

        if (this.rand.nextFloat() < 0.125F + 0.025F * looting) {
            ItemStack itemStack = new ItemStack(ModItems.MOB_ENCHANT_BOOK);

            this.entityDropItem(MobEnchantUtils.addRandomEnchantmentToItemStack(rand, itemStack, 8, false));
        }
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
                    LivingEntity target = list.get(EnchanterEntity.this.rand.nextInt(list.size()));
                    if (target != EnchanterEntity.this.getAttackTarget()) {
                        EnchanterEntity.this.setEnchantTarget(list.get(EnchanterEntity.this.rand.nextInt(list.size())));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EnchanterEntity.this.getEnchantTarget() != null && EnchanterEntity.this.getEnchantTarget() != EnchanterEntity.this.getAttackTarget() && this.spellWarmup > 0;
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
                    MobEnchantUtils.addRandomEnchantmentToEntity(entity, cap, entity.getRNG(), 8, false);
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
            return 600;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED;
        }

        protected SpellcastingIllagerEntity.SpellType getSpellType() {
            return SpellcastingIllagerEntity.SpellType.WOLOLO;
        }
    }

    class AvoidTargetEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
        private final EnchanterEntity enchanter;

        public AvoidTargetEntityGoal(EnchanterEntity enchanterIn, Class<T> entityClassToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
            super(enchanterIn, entityClassToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
            this.enchanter = enchanterIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (super.shouldExecute() && this.avoidTarget == this.enchanter.getAttackTarget()) {
                return this.enchanter.getAttackTarget() != null;
            } else {
                return false;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            EnchanterEntity.this.setAttackTarget((LivingEntity) null);
            super.startExecuting();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            EnchanterEntity.this.setAttackTarget((LivingEntity) null);
            super.tick();
        }
    }
}
