package net.cebularz.smokingpipe.effects;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.effects.custom.WisdomEffect;
import net.cebularz.smokingpipe.particles.ModParticles;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SmokingPipe.MOD_ID);

    public static final Holder<MobEffect> WISDOM_EFFECT = MOB_EFFECTS.register("wisdom",
            () -> new WisdomEffect(MobEffectCategory.NEUTRAL, 0x36ebab));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
