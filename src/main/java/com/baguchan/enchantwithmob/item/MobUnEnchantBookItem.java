package com.baguchan.enchantwithmob.item;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MobUnEnchantBookItem extends Item {
    public MobUnEnchantBookItem(Properties group) {
        super(group);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        playerIn.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
        {
            MobEnchantUtils.removeMobEnchantToEntity(playerIn, cap);
        });
        playerIn.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

        stack.damageItem(1, playerIn, (entity) -> entity.sendBreakAnimation(handIn));
        playerIn.getCooldownTracker().setCooldown(stack.getItem(), 80);

        return ActionResult.resultSuccess(stack);

    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
