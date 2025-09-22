package net.cebularz.smokingpipe.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class SmokeCircleParticle extends TextureSheetParticle {

    protected SmokeCircleParticle(ClientLevel world, double x, double y, double z,
                                  double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);

        this.gravity = 0.0F;
        this.lifetime = 60 + this.random.nextInt(45);
        this.quadSize = 0.08F + this.random.nextFloat() * 0.12F;
        this.setAlpha(0.8F + this.random.nextFloat() * 0.4F);

        this.xd = vx+ this.random.nextFloat()*0.05F;
        this.yd = vy + 0.02+ this.random.nextFloat()*0.05F;
        this.zd = vz + this.random.nextFloat()*0.05F;
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        // Fade out
        this.setAlpha(1.0F - lifeRatio);

        // Slight growth
        this.quadSize *= 1.01F;

        // Upward drift
        this.yd += 0.002;

        // Disable rotation to camera
        this.roll = 0.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * Override rotation for quad vertices so the particle stays flat in world space.
     * This prevents the particle from rotating to face the camera.
     */
    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize;
    }


}
