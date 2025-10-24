package net.cebularz.smokingpipe.items.custom;

import net.cebularz.smokingpipe.effects.ModEffects;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.minecraft.core.Holder;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Objects;

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


    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {

        if (!level.isClientSide) {
            if (entity.tickCount % 20 == 0) {
                if (entity instanceof LivingEntity player) {
                    MobEffectInstance mobEffectInstance = new MobEffectInstance(ModEffects.WISDOM_EFFECT, 200);
                    player.addEffect(mobEffectInstance);
                }
            }
            return;
        }

        if (entity.tickCount % 20 == 0) {

            double distance = 0.3;

            float yawRad = (float) Math.toRadians(entity.yRotO);
            float pitchRad = (float) Math.toRadians(entity.xRotO);

            double offsetX = -Math.sin(yawRad) * Math.cos(pitchRad) * distance;
            double offsetY = -Math.sin(pitchRad) * distance + 0.1;
            double offsetZ = Math.cos(yawRad) * Math.cos(pitchRad) * distance;

            double x = entity.getX() + offsetX;
            double y = entity.getEyeY() + offsetY;
            double z = entity.getZ() + offsetZ;


            double dx = 0.0;
            double dy = 0.02;
            double dz = 0.0;

            for(int i =0; i< 1 + level.random.nextInt(3);i++) {
                level.addParticle(ModParticles.SMOKE_CIRCLE.get(),
                        x, y, z,
                        dx, dy, dz);
            }
        }
    }
    private void addWisdomEffect(Player player){
        if (player.hasEffect(ModEffects.WISDOM_EFFECT)){

            Holder<MobEffect> wisdomEffectHolder = ModEffects.WISDOM_EFFECT;
            MobEffectInstance wisdomEffect = Objects.requireNonNull(player.getEffect(ModEffects.WISDOM_EFFECT));
            int wisdomDuration = wisdomEffect.getDuration();
            MobEffectInstance mobEffectInstance = new MobEffectInstance(ModEffects.WISDOM_EFFECT, wisdomDuration+200);
            player.addEffect(mobEffectInstance);
        }
        else {
            MobEffectInstance mobEffectInstance = new MobEffectInstance(ModEffects.WISDOM_EFFECT, 200);
            player.addEffect(mobEffectInstance);

        }
    }
}
