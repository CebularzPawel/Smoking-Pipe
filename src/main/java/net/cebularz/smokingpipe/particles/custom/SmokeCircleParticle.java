package net.cebularz.smokingpipe.particles.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SmokeCircleParticle extends TextureSheetParticle {
    private int randomvalue = 0;
    protected SmokeCircleParticle(ClientLevel world, double x, double y, double z,
                                  double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);
        this.randomvalue = 5;
        this.gravity = 0.0F;
        this.lifetime = 60 + this.random.nextInt(45);
        this.quadSize = 0.08F + this.random.nextFloat() * 0.12F;
        this.setAlpha(0.8F + this.random.nextFloat() * 0.4F);

        this.xd = vx+ this.random.nextFloat()*0.05F;
        this.yd = vy + 0.02+ this.random.nextFloat()*0.05F;
        this.zd = vz + this.random.nextFloat()*0.05F;
    }
    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quat, camera, partialTicks) -> {
            quat.identity().rotateX((float)Math.toRadians(90));
        };
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Camera camera, Quaternionf quaternion, float partialTicks) {
        float f = this.getQuadSize(partialTicks);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(partialTicks);
        Vec3 camPos = camera.getPosition();

        float x = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - camPos.z());

        // Front face
        this.renderVertex(buffer, quaternion, x, y, z,  1.0F, -1.0F, f, f2, f4, i);
        this.renderVertex(buffer, quaternion, x, y, z,  1.0F,  1.0F, f, f2, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F,  1.0F, f, f1, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F, -1.0F, f, f1, f4, i);

        // Back face (reverse order so it’s visible from the other side)
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F, -1.0F, f, f1, f4, i);
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F,  1.0F, f, f1, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z,  1.0F,  1.0F, f, f2, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z,  1.0F, -1.0F, f, f2, f4, i);
    }
    private void renderVertex(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float xOffset, float yOffset, float quadSize, float u, float v, int packedLight) {
        Vector3f vector3f = (new Vector3f(xOffset, yOffset, 0.0F)).rotate(quaternion).mul(quadSize).add(x, y, z);
        buffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        float fadeStart = 0.2F;
        if (lifeRatio > fadeStart) {
            float fadeRatio = (lifeRatio - fadeStart) / (1.0F - fadeStart);
            this.setAlpha(1.0F - fadeRatio);
        } else {
            this.setAlpha(1.0F);
        }


        // Slight growth
        this.quadSize *= 1.015F;

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
