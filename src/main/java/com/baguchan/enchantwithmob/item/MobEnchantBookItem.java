package com.baguchan.enchantwithmob.item;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MobEnchantBookItem extends Item {
    public MobEnchantBookItem(Properties group) {
        super(group);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (MobEnchantUtils.hasMobEnchant(stack)) {
            target.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                if (!cap.hasEnchant()) {
                    MobEnchantUtils.addMobEnchantToEntity(stack, target, cap);
                }
            });
            playerIn.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

            stack.damageItem(1, playerIn, (entity) -> entity.sendBreakAnimation(hand));

            return ActionResultType.SUCCESS;
        }

        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (MobEnchant enchant : MobEnchants.getRegistry()) {
                ItemStack stack = new ItemStack(this);
                MobEnchantUtils.addMobEnchantToItemStack(stack, enchant, enchant.getMaxLevel());
                items.add(stack);
            }
        }

    }

    public static ListNBT getEnchantmentList(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null ? compoundnbt.getList(MobEnchantUtils.TAG_STORED_MOBENCHANTS, 10) : new ListNBT();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (MobEnchantUtils.hasMobEnchant(stack)) {
            ListNBT listnbt = MobEnchantUtils.getEnchantmentListForNBT(stack.getTag());

            for (int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt = listnbt.getCompound(i);

                MobEnchant mobEnchant = MobEnchantUtils.getEnchantFromNBT(compoundnbt);
                int level = MobEnchantUtils.getEnchantLevelFromNBT(compoundnbt);

                if (mobEnchant != null) {
                    TextFormatting[] textformatting = new TextFormatting[]{TextFormatting.AQUA};

                    tooltip.add(new TranslationTextComponent("mobenchant.enchantwithmob.name." + mobEnchant.getRegistryName().getNamespace() + "." + mobEnchant.getRegistryName().getPath()).mergeStyle(textformatting).appendString(" ").append(new TranslationTextComponent("enchantment.level." + level).mergeStyle(textformatting)));
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
