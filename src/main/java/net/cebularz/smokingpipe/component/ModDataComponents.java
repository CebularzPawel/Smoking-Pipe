package net.cebularz.smokingpipe.component;

import net.cebularz.smokingpipe.SmokingPipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SmokingPipe.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SmokableContent>> SMOKABLE =
            DATA_COMPONENTS.register("smokable", () ->
                    DataComponentType.<SmokableContent>builder()
                            .persistent(SmokableContent.CODEC)
                            .networkSynchronized(SmokableContent.STREAM_CODEC)
                            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
