package net.soko.pyrotechnics;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.soko.pyrotechnics.block.ModBlocks;
import net.soko.pyrotechnics.events.ClientEvents;
import net.soko.pyrotechnics.entity.ModEntities;
import net.soko.pyrotechnics.events.CommonEvents;
import net.soko.pyrotechnics.item.ModCreativeModeTab;
import net.soko.pyrotechnics.item.ModItems;
import net.soko.pyrotechnics.recipe.ModRecipes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(net.soko.pyrotechnics.PyroTechnics.MOD_ID)
public class PyroTechnics {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "pyrotechnics";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "pyrotechnics" namespace

    public PyroTechnics() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Aspects of Mod on the Event Bus
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);


        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModCreativeModeTab::register);
        modEventBus.addListener(ModCreativeModeTab::buildContents);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::onEntityDeath);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::onEntityDrops);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::onItemRightClick);

        if(FMLEnvironment.dist.isClient()) {
            modEventBus.register(ClientEvents.class);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

}
