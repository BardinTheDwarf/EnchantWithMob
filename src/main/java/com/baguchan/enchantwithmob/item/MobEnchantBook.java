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

public class MobEnchantBook extends Item {
    public MobEnchantBook(Properties group) {
        super(group);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (stack.hasTag()) {
            target.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                MobEnchant mobEnchant = MobEnchantUtils.getEnchantTypeFromNBT(stack.getTag());

                if (mobEnchant != null) {
                    cap.setMobEnchant(target, mobEnchant);
                }

            });
            playerIn.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

            stack.shrink(1);

            return ActionResultType.SUCCESS;
        }

        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (MobEnchant enchant : MobEnchants.getRegistry()) {
                items.add(MobEnchantUtils.addMobEnchantToItemStack(new ItemStack(this), enchant));

            }
        }

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (MobEnchantUtils.hasMobEnchant(stack)) {
            MobEnchant mobEnchant = MobEnchantUtils.getEnchantTypeFromNBT(stack.getTag());

            if (mobEnchant != null) {
                TextFormatting[] textformatting = new TextFormatting[]{TextFormatting.AQUA};

                tooltip.add(new TranslationTextComponent("mobenchant.enchantwithmob.name." + mobEnchant.getRegistryName().getNamespace() + "." + mobEnchant.getRegistryName().getPath()).func_240701_a_(textformatting));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
