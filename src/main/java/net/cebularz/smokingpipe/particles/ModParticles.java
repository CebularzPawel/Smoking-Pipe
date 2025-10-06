package net.cebularz.smokingpipe.particles;

import net.cebularz.smokingpipe.SmokingPipe;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SmokingPipe.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMOKE_CIRCLE =
            PARTICLES.register("smoke_circle", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WISDOM_PARTICLE =
            PARTICLES.register("wisdom_particle", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}