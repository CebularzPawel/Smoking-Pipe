package net.cebularz.smokingpipe.events;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.cebularz.smokingpipe.particles.custom.SmokeCircleParticleProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = SmokingPipe.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SMOKE_CIRCLE.get(), SmokeCircleParticleProvider::new);
    }
}
