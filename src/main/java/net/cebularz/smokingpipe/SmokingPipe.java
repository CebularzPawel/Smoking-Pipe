package net.cebularz.smokingpipe;

import net.cebularz.smokingpipe.component.ModDataComponents;
import net.cebularz.smokingpipe.effects.ModEffects;
import net.cebularz.smokingpipe.items.ModItems;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.cebularz.smokingpipe.sound.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(SmokingPipe.MOD_ID)
public class SmokingPipe {
    public static final String MOD_ID = "smokingpipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SmokingPipe(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModDataComponents.register(modEventBus);

        ModParticles.register(modEventBus);

        ModEffects.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        ModSounds.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, SmokingPipeConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SMOKING_PIPE);
        }
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.LEAF_OF_WISDOM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
