package net.cebularz.smokingpipe.items.custom;

import net.cebularz.smokingpipe.effects.ModEffects;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.cebularz.smokingpipe.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmokingPipeItem extends Item {
    public SmokingPipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        level.playSound(null,
                player.blockPosition(),
                ModSounds.SMOKING_PIPE_USE.get(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F);
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
                if (entity instanceof Player player) {
                    level.playSound(null,
                            player.blockPosition(),
                            ModSounds.SMOKING_PIPE_PUFF.get(),
                            SoundSource.BLOCKS,
                            1.0F,
                            1.0F);
                    addWisdomEffect(player);
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
            MobEffectInstance mobEffectInstance = new MobEffectInstance(ModEffects.WISDOM_EFFECT, wisdomDuration+300);
            player.addEffect(mobEffectInstance);
        }
        else {
            MobEffectInstance mobEffectInstance = new MobEffectInstance(ModEffects.WISDOM_EFFECT, 300);
            player.addEffect(mobEffectInstance);

        }
    }
    private static final int BAR_COLOR = Mth.color(0.75F, 0.95F, 0.4F);
    private static final int TOOLTIP_MAX_WEIGHT = 64;
    public static float getFullnessDisplay(ItemStack stack) {
        BundleContents bundlecontents = (BundleContents)stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        return bundlecontents.weight().floatValue();
    }
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }
    public int getBarWidth(ItemStack stack) {
        BundleContents bundlecontents = (BundleContents)stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        return Math.min(1 + Mth.mulAndTruncate(bundlecontents.weight(), 12), 13);
    }
    public boolean isBarVisible(ItemStack stack) {
        BundleContents bundlecontents = (BundleContents)stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        return bundlecontents.weight().compareTo(Fraction.ZERO) > 0;
    }
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ? Optional.ofNullable((BundleContents)stack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new) : Optional.empty();
    }
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BundleContents bundlecontents = (BundleContents)stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundlecontents != null) {
            int i = Mth.mulAndTruncate(bundlecontents.weight(), 64);
            tooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", new Object[]{i, 64}).withStyle(ChatFormatting.GRAY));
        }

    }
    public void onDestroyed(ItemEntity itemEntity) {
        BundleContents bundlecontents = (BundleContents)itemEntity.getItem().get(DataComponents.BUNDLE_CONTENTS);
        if (bundlecontents != null) {
            itemEntity.getItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            ItemUtils.onContainerDestroyed(itemEntity, bundlecontents.itemsCopy());
        }

    }
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (stack.getCount() == 1 && action == ClickAction.SECONDARY) {
            BundleContents bundlecontents = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundlecontents == null) {
                return false;
            } else {
                ItemStack slotStack = slot.getItem();
                BundleContents.Mutable mutable = new BundleContents.Mutable(bundlecontents);

                if (slotStack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    ItemStack removed = mutable.removeOne();
                    if (removed != null) {
                        ItemStack remainder = slot.safeInsert(removed);
                        mutable.tryInsert(remainder);
                    }
                } else if (slotStack.canFitInsideContainerItems()) {
                    if (!bundlecontents.isEmpty()) {
                        return false;
                    }

                    ItemStack single = slotStack.copyWithCount(1);
                    int inserted = mutable.tryInsert(single);

                    if (inserted > 0) {
                        slotStack.shrink(1);
                        this.playInsertSound(player);
                    }
                }

                stack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (stack.getCount() != 1) {
            return false;
        } else if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            BundleContents bundlecontents = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundlecontents == null) {
                return false;
            } else {
                BundleContents.Mutable mutable = new BundleContents.Mutable(bundlecontents);

                if (other.isEmpty()) {
                    ItemStack removed = mutable.removeOne();
                    if (removed != null) {
                        this.playRemoveOneSound(player);
                        access.set(removed);
                    }
                } else {
                    if (!bundlecontents.isEmpty()) {
                        return false;
                    }

                    ItemStack single = other.copyWithCount(1);
                    int inserted = mutable.tryInsert(single);

                    if (inserted > 0) {
                        other.shrink(1);
                        this.playInsertSound(player);
                    }
                }

                stack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }
}
