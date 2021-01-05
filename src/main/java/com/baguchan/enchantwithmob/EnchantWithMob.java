package com.baguchan.enchantwithmob;

import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.capability.MobEnchantStorage;
import com.baguchan.enchantwithmob.client.ClientRegistrar;
import com.baguchan.enchantwithmob.message.MobEnchantedMessage;
import com.baguchan.enchantwithmob.message.RemoveAllMobEnchantMessage;
import com.baguchan.enchantwithmob.registry.ModEntities;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnchantWithMob.MODID)
public class EnchantWithMob
{

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "enchantwithmob";
    public static final String NETWORK_PROTOCOL = "2";

    @CapabilityInject(MobEnchantCapability.class)
    public static final Capability<MobEnchantCapability> MOB_ENCHANT_CAP = null;

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
            .networkProtocolVersion(() -> NETWORK_PROTOCOL)
            .clientAcceptedVersions(NETWORK_PROTOCOL::equals)
            .serverAcceptedVersions(NETWORK_PROTOCOL::equals)
            .simpleChannel();

    public EnchantWithMob() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        this.setupMessages();
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientRegistrar::loadComplete));


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EnchantConfig.COMMON_SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(MobEnchantCapability.class, new MobEnchantStorage<>(), MobEnchantCapability::new);
    }

    private void setupMessages() {
        CHANNEL.messageBuilder(MobEnchantedMessage.class, 0)
                .encoder(MobEnchantedMessage::serialize).decoder(MobEnchantedMessage::deserialize)
                .consumer(MobEnchantedMessage::handle)
                .add();
        CHANNEL.messageBuilder(RemoveAllMobEnchantMessage.class, 1)
                .encoder(RemoveAllMobEnchantMessage::serialize).decoder(RemoveAllMobEnchantMessage::deserialize)
                .consumer(RemoveAllMobEnchantMessage::handle)
                .add();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ModEntities.setupEntitiesClient();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }
}
