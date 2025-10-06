package net.cebularz.smokingpipe.effects.custom;

import net.cebularz.smokingpipe.particles.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class WisdomEffect extends MobEffect {
    public WisdomEffect(MobEffectCategory category, int color ) {
        super(category, color);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return ModParticles.WISDOM_PARTICLE.get();
    }
}
