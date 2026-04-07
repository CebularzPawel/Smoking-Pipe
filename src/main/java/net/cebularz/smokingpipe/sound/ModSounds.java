package net.cebularz.smokingpipe.sound;

import net.cebularz.smokingpipe.SmokingPipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SmokingPipe.MOD_ID);

    public static final Supplier<SoundEvent> SMOKING_PIPE_USE = registerSoundEvent("smoking_pipe_use");
    public static final Supplier<SoundEvent> SMOKING_PIPE_PUFF = registerSoundEvent("smoking_pipe_puff");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SmokingPipe.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }


}
