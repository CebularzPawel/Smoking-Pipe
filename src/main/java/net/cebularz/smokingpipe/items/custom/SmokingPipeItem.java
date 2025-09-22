package net.cebularz.smokingpipe.items.custom;

import net.cebularz.smokingpipe.particles.ModParticles;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SmokingPipeItem extends Item {
    public SmokingPipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    // Called every tick while the player is holding right-click
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide) {
            return; // particles should only spawn on the client
        }

        // spawn every 10 ticks (0.5s)
        if (entity.tickCount % 20 == 0) {

            // Distance in front of the face (pipe tip)
            double distance = 0.3;

            // Convert rotation to radians
            float yawRad = (float) Math.toRadians(entity.yRotO);
            float pitchRad = (float) Math.toRadians(entity.xRotO);

            // Calculate forward offset
            double offsetX = -Math.sin(yawRad) * Math.cos(pitchRad) * distance;
            double offsetY = -Math.sin(pitchRad) * distance + 0.1; // slightly above mouth
            double offsetZ = Math.cos(yawRad) * Math.cos(pitchRad) * distance;

            double x = entity.getX() + offsetX;
            double y = entity.getEyeY() + offsetY;
            double z = entity.getZ() + offsetZ;

            // small upward motion
            double dx = 0.0;
            double dy = 0.02;
            double dz = 0.0;

            for(int i =0; i<4;i++) {
                level.addParticle(ModParticles.SMOKE_CIRCLE.get(),
                        x, y, z,
                        dx, dy, dz);
            }
        }
    }

}
