package net.cebularz.smokingpipe.events;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.items.tooltip.SmokingPipeTooltipComponent;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.cebularz.smokingpipe.particles.custom.SmokeCircleParticleProvider;
import net.cebularz.smokingpipe.util.SmokingManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SmokingPipe.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SMOKE_CIRCLE.get(), SmokeCircleParticleProvider::new);
        event.registerSpriteSet(ModParticles.WISDOM_PARTICLE.get(), SpellParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SmokingPipeTooltipComponent.Tooltip.class, SmokingPipeTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        var itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (SmokingManager.isSmokable(itemId)) {
            event.getToolTip().add(
                    Component.translatable(
                            SmokingManager.isInfinite(itemId) ? "tooltip.smokingpipe.smokable_infinite" : "tooltip.smokingpipe.smokable"
                    ).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            );
        }
    }
}
