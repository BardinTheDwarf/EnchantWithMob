package com.baguchan.enchantwithmob.event;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.google.common.collect.Sets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class LootEvent {
    private static final Set<ResourceLocation> STRONG_LOOT = Sets.newHashSet(LootTables.CHESTS_STRONGHOLD_LIBRARY);
    private static final Set<ResourceLocation> OUTPOST_LOOT = Sets.newHashSet(LootTables.CHESTS_PILLAGER_OUTPOST);

    @SubscribeEvent
    public static void onInjectLoot(LootTableLoadEvent event) {

        if (STRONG_LOOT.contains(event.getName())) {
            LootPool pool = LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(EnchantWithMob.MODID, "injections/mobbook_library"))).name("mobbook_library").build();
            event.getTable().addPool(pool);
        }

        if (OUTPOST_LOOT.contains(event.getName())) {
            LootPool pool = LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(EnchantWithMob.MODID, "injections/mobbook_outpost"))).name("mobbook_outpost").build();
            event.getTable().addPool(pool);
        }

        /*if (event.getName().equals(tall_grass_drops)) {
        }*/
    }
}