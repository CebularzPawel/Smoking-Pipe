package net.cebularz.smokingpipe.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SmokeCircleParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public SmokeCircleParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel world,
                                   double x, double y, double z,
                                   double vx, double vy, double vz) {
        SmokeCircleParticle particle = new SmokeCircleParticle(world, x, y, z, vx, vy, vz);
        particle.pickSprite(this.spriteSet);
        return particle;
    }
}
