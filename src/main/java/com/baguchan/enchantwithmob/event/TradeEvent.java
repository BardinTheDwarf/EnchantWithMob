package com.baguchan.enchantwithmob.event;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.mobenchant.MobEnchant;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import com.baguchan.enchantwithmob.registry.ModItems;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class TradeEvent {
    @SubscribeEvent
    public static void wanderTradeEvent(WandererTradesEvent event) {
        List<VillagerTrades.ITrade> trades = event.getGenericTrades();
        trades.add(new MobEnchantedBookForEmeraldsTrade(15));
    }

    static class MobEnchantedBookForEmeraldsTrade implements VillagerTrades.ITrade {
        private final int xpValue;

        public MobEnchantedBookForEmeraldsTrade(int xpValueIn) {
            this.xpValue = xpValueIn;
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            List<MobEnchant> list = MobEnchants.getRegistry().getValues().stream().collect(Collectors.toList());
            MobEnchant enchantment = list.get(rand.nextInt(list.size()));
            int i = MathHelper.nextInt(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = new ItemStack(ModItems.MOB_ENCHANT_BOOK);
            MobEnchantUtils.addMobEnchantToItemStack(itemstack, enchantment, i);
            int j = 2 + rand.nextInt(5 + i * 10) + 3 * i;

            if (j > 64) {
                j = 64;
            }

            return new MerchantOffer(new ItemStack(Items.EMERALD, j), ItemStack.EMPTY, itemstack, 12, this.xpValue, 0.2F);
        }
    }
}
