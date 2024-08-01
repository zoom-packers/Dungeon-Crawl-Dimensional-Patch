package com.pandaismyname1.dungeoncrawldimensionalpatch;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.pandaismyname1.dungeoncrawldimensionalpatch.data.Dimension;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.io.InputStreamReader;
import java.util.stream.Stream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DungeonCrawlDimensionalPatch.MODID)
public class DungeonCrawlDimensionalPatch {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dungeon_crawl_dimensional_patch";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public DungeonCrawlDimensionalPatch() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Dimension.RESOURCE_MANAGER = event.getServer().getResourceManager();
        var holders = event.getServer().registryAccess().registryOrThrow(Registries.DIMENSION).holders();
        scanForDimensions(holders);
    }


    private void onAddReloadListener(final AddReloadListenerEvent event) {
        if (Dimension.RESOURCE_MANAGER == null) {
            return;
        }
        var holders = event.getRegistryAccess().registryOrThrow(Registries.DIMENSION).holders();
        scanForDimensions(holders);
    }

    private void scanForDimensions(Stream<Holder.Reference<Level>> holders) {
        try {

            var dimensionsJsonResourceLocation = DungeonCrawl.locate("dimensions.json");
            var dimensionsJsonResource = Dimension.RESOURCE_MANAGER.getResource(dimensionsJsonResourceLocation).orElseThrow();
            var dimensionsJson = JsonParser.parseReader(new JsonReader(new InputStreamReader(dimensionsJsonResource.open()))).getAsJsonArray();
            dimensionsJson.forEach(jsonElement -> {
                var dimension = jsonElement.getAsString();
                Dimension.DIMENSIONS.put(dimension, new Dimension(dimension));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        var holderList = holders.toList();
//        for (var holder : holderList) {
//            var id = holder.key().location().toString();
//            var dimensionName = id.substring(id.lastIndexOf(":") + 1);
//            Dimension.DIMENSIONS.put(dimensionName, new Dimension(dimensionName));
//        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
