package net.cebularz.smokingpipe.items.custom;

import net.cebularz.smokingpipe.SmokingPipeConfig;
import net.cebularz.smokingpipe.component.ModDataComponents;
import net.cebularz.smokingpipe.component.SmokableContent;
import net.cebularz.smokingpipe.effects.ModEffects;
import net.cebularz.smokingpipe.items.tooltip.SmokingPipeTooltipComponent;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.cebularz.smokingpipe.sound.ModSounds;
import net.cebularz.smokingpipe.util.SmokingManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmokingPipeItem extends Item {
    public static final int MAX_SMOKABLE = 64;

    public SmokingPipeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStack getSmokable(ItemStack pipeStack) {
        SmokableContent content = pipeStack.get(ModDataComponents.SMOKABLE.get());
        return content != null ? content.stack() : ItemStack.EMPTY;
    }

    private void saveSmokable(ItemStack pipeStack, ItemStack smokable) {
        if (smokable.isEmpty()) {
            pipeStack.remove(ModDataComponents.SMOKABLE.get());
        } else {
            pipeStack.set(ModDataComponents.SMOKABLE.get(), new SmokableContent(smokable));
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pipeStack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack slotStack = slot.getItem();
        if (slotStack.isEmpty()) {
            ejectToSlot(pipeStack, slot, player);
        } else {
            insertFromSlot(pipeStack, slot, player);
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pipeStack, ItemStack cursorStack, Slot slot, ClickAction action, Player player, SlotAccess slotAccess) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }
        if (cursorStack.isEmpty()) {
            ItemStack smokable = getSmokable(pipeStack);
            if (!smokable.isEmpty()) {
                saveSmokable(pipeStack, ItemStack.EMPTY);
                slotAccess.set(smokable);
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                broadcastChanges(player);
            }
        } else {
            insertFromCursor(pipeStack, cursorStack, player);
        }
        return true;
    }

    private void insertFromSlot(ItemStack pipeStack, Slot slot, Player player) {
        int amountInserted = tryInsert(pipeStack, slot.getItem());
        if (amountInserted > 0) {
            slot.remove(amountInserted);
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            broadcastChanges(player);
        }
    }

    private void insertFromCursor(ItemStack pipeStack, ItemStack cursorStack, Player player) {
        int amountInserted = tryInsert(pipeStack, cursorStack);
        if (amountInserted > 0) {
            cursorStack.shrink(amountInserted);
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            broadcastChanges(player);
        }
    }

    private int tryInsert(ItemStack pipeStack, ItemStack incoming) {
        if (incoming.isEmpty()) {
            return 0;
        }
        ItemStack currentSmokable = getSmokable(pipeStack);
        if (!currentSmokable.isEmpty() && !ItemStack.isSameItemSameComponents(currentSmokable, incoming)) {
            return 0;
        }
        int availableSpace = MAX_SMOKABLE - currentSmokable.getCount();
        if (availableSpace <= 0) {
            return 0;
        }
        int amountToInsert = Math.min(availableSpace, incoming.getCount());
        ItemStack updatedSmokable = currentSmokable.isEmpty()
                ? incoming.copyWithCount(amountToInsert)
                : currentSmokable.copyWithCount(currentSmokable.getCount() + amountToInsert);
        saveSmokable(pipeStack, updatedSmokable);
        return amountToInsert;
    }

    private void ejectToSlot(ItemStack pipeStack, Slot slot, Player player) {
        ItemStack smokable = getSmokable(pipeStack);
        if (smokable.isEmpty()) {
            return;
        }
        saveSmokable(pipeStack, ItemStack.EMPTY);
        slot.safeInsert(smokable);
        player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
        broadcastChanges(player);
    }

    private void broadcastChanges(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        containerMenu.slotsChanged(player.getInventory());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack pipeStack = player.getItemInHand(usedHand);
        InteractionHand otherHand = usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherHandStack = player.getItemInHand(otherHand);

        if (!otherHandStack.isEmpty()) {
            if (!level.isClientSide()) {
                int amountInserted = tryInsert(pipeStack, otherHandStack);
                if (amountInserted > 0) {
                    otherHandStack.shrink(amountInserted);
                    player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
                }
            }
            return InteractionResultHolder.sidedSuccess(pipeStack, level.isClientSide());
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ItemStack smokable = getSmokable(pipeStack);
                if (!smokable.isEmpty()) {
                    saveSmokable(pipeStack, ItemStack.EMPTY);
                    if (!player.addItem(smokable)) {
                        player.drop(smokable, false);
                    }
                    player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
                }
            }
            return InteractionResultHolder.sidedSuccess(pipeStack, level.isClientSide());
        }

        level.playSound(null, player.blockPosition(), ModSounds.SMOKING_PIPE_USE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        player.startUsingItem(usedHand);
        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(pipeStack);
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
            if ((getUseDuration(stack, entity) - remainingUseDuration) % getSmokingTime(stack) == 0) {
                if (entity instanceof Player player) {
                    applySmokingEffects(player, stack);
                }
            }
            if (entity.tickCount % 20 == 0) {
                if (entity instanceof Player player) {
                    level.playSound(null, player.blockPosition(), ModSounds.SMOKING_PIPE_PUFF.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return;
        }

        if (entity.tickCount % 20 == 0) {
            double distance = 0.3;
            float yawRadians = (float) Math.toRadians(entity.yRotO);
            float pitchRadians = (float) Math.toRadians(entity.xRotO);
            double offsetX = -Math.sin(yawRadians) * Math.cos(pitchRadians) * distance;
            double offsetY = -Math.sin(pitchRadians) * distance + 0.1;
            double offsetZ = Math.cos(yawRadians) * Math.cos(pitchRadians) * distance;
            double particleX = entity.getX() + offsetX;
            double particleY = entity.getEyeY() + offsetY;
            double particleZ = entity.getZ() + offsetZ;
            for (int particleCount = 0; particleCount < 1 + level.random.nextInt(3); particleCount++) {
                level.addParticle(ModParticles.SMOKE_CIRCLE.get(), particleX, particleY, particleZ, 0.0, 0.02, 0.0);
            }
        }
    }

    private void applySmokingEffects(Player player, ItemStack pipeStack) {
        List<SmokingManager.SmokingEffect> effects = SmokingManager.getEffects(getSmokableId(pipeStack));
        if (effects.isEmpty()) {
            addWisdomEffect(player);
        } else {
            for (SmokingManager.SmokingEffect effect : effects) {
                if (effect instanceof SmokingManager.ApplyEffect applyEffect) {
                    BuiltInRegistries.MOB_EFFECT
                            .getHolder(ResourceLocation.parse(applyEffect.effect))
                            .ifPresent(effectHolder -> {
                                if (player.hasEffect(effectHolder)) {
                                    MobEffectInstance currentEffect = Objects.requireNonNull(player.getEffect(effectHolder));
                                    player.addEffect(new MobEffectInstance(effectHolder, currentEffect.getDuration() + applyEffect.duration, applyEffect.amplifier));
                                } else {
                                    player.addEffect(new MobEffectInstance(effectHolder, applyEffect.duration, applyEffect.amplifier));
                                }
                            });

                }
            }
        }
        if (SmokingPipeConfig.CONSUME_SMOKABLE_ON_USE.get() && !player.isCreative()) {
            consumeSmokable(pipeStack);
        }
    }

    private void consumeSmokable(ItemStack pipeStack) {
        ItemStack smokable = getSmokable(pipeStack);
        if (smokable.isEmpty()) {
            return;
        }
        if (smokable.getCount() <= 1) {
            saveSmokable(pipeStack, ItemStack.EMPTY);
        } else {
            saveSmokable(pipeStack, smokable.copyWithCount(smokable.getCount() - 1));
        }
    }

    private int getSmokingTime(ItemStack pipeStack) {
        for (SmokingManager.SmokingEffect effect : SmokingManager.getEffects(getSmokableId(pipeStack))) {
            if (effect instanceof SmokingManager.SmokingSpeed smokingSpeed) {
                return Math.max(1, (int) (40 / smokingSpeed.multiplier));
            }
        }
        return 40;
    }

    private String getSmokableId(ItemStack pipeStack) {
        ItemStack smokable = getSmokable(pipeStack);
        if (smokable.isEmpty()) {
            return "";
        }
        return BuiltInRegistries.ITEM.getKey(smokable.getItem()).toString();
    }

    private void addWisdomEffect(Player player) {
        if (player.hasEffect(ModEffects.WISDOM_EFFECT)) {
            MobEffectInstance currentWisdomEffect = Objects.requireNonNull(player.getEffect(ModEffects.WISDOM_EFFECT));
            player.addEffect(new MobEffectInstance(ModEffects.WISDOM_EFFECT, currentWisdomEffect.getDuration() + 300));
        } else {
            player.addEffect(new MobEffectInstance(ModEffects.WISDOM_EFFECT, 300));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        for (SmokingManager.SmokingEffect effect : SmokingManager.getEffects(getSmokableId(stack))) {
            if (effect instanceof SmokingManager.ApplyEffect applyEffect) {
                return BuiltInRegistries.MOB_EFFECT
                        .getHolder(ResourceLocation.parse(applyEffect.effect))
                        .map(effectHolder -> effectHolder.value().getColor())
                        .orElse(0xFFFFFF);
            }
        }
        return 0xFFFFFF;
    }
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !getSmokable(stack).isEmpty();
    }
    @Override
    public int getBarWidth(ItemStack stack) {
        ItemStack smokable = getSmokable(stack);

        if (smokable.isEmpty()) {
            return 0;
        }

        float fill = (float) smokable.getCount() / MAX_SMOKABLE;

        return Math.min(1 + Mth.floor(fill * 12), 13);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStack smokable = getSmokable(stack);
        if (smokable.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SmokingPipeTooltipComponent.Tooltip(smokable));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ItemStack smokable = getSmokable(stack);
        if (smokable.isEmpty()) {
            return;
        }

        tooltipComponents.add(Component.translatable("item.smokingpipe.when_smoked").withStyle(ChatFormatting.GRAY));
        String smokableItemId = BuiltInRegistries.ITEM.getKey(smokable.getItem()).toString();
        for (SmokingManager.SmokingEffect effect : SmokingManager.getEffects(smokableItemId)) {
            if (effect instanceof SmokingManager.ApplyEffect applyEffect) {
                BuiltInRegistries.MOB_EFFECT
                        .getHolder(ResourceLocation.parse(applyEffect.effect))
                        .ifPresent(effectHolder -> {
                            MutableComponent effectName = Component.translatable(effectHolder.value().getDescriptionId());
                            if (applyEffect.amplifier > 0) {
                                effectName = Component.translatable("potion.withAmplifier", effectName,
                                        Component.translatable("potion.potency." + applyEffect.amplifier));
                            }
                            int totalSeconds = applyEffect.duration / 20;
                            String durationText = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
                            effectName = Component.translatable("potion.withDuration", effectName, durationText);
                            tooltipComponents.add(effectName.withStyle(style ->
                                    style.withColor(TextColor.fromRgb(effectHolder.value().getColor()))));
                        });
            }
        }

        tooltipComponents.add(Component.literal(" "));
        ItemStack cleanPipeStack = new ItemStack(stack.getItem());
        for (CreativeModeTab creativeTab : BuiltInRegistries.CREATIVE_MODE_TAB) {
            if (creativeTab.getType() == CreativeModeTab.Type.CATEGORY && creativeTab.contains(cleanPipeStack)) {
                tooltipComponents.add(creativeTab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
                break;
            }
        }
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemStack smokable = getSmokable(itemEntity.getItem());
        if (!smokable.isEmpty()) {
            itemEntity.getItem().remove(ModDataComponents.SMOKABLE.get());
            ItemUtils.onContainerDestroyed(itemEntity, List.of(smokable));
        }
    }
}
