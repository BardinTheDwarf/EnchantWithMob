package com.baguchan.enchantwithmob.registry;

import com.baguchan.enchantwithmob.EnchantWithMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobItems {
    public static final DeferredRegister<Item> ITEM = new DeferredRegister<>(ForgeRegistries.ITEMS, EnchantWithMob.MODID);

    public static final RegistryObject<Item> ENCHANTER_SPAWNEGG = ITEM.register("enchanter_spawn_egg", () -> new SpawnEggItem(ModEntities.ENCHANTER.get(), 9804699, 0x81052d, (new Item.Properties()).group(ItemGroup.MISC)));

}
