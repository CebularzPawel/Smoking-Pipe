package net.cebularz.smokingpipe.items;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.items.custom.SmokingPipeItem;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmokingPipe.MOD_ID);

    public static final DeferredItem<SmokingPipeItem> SMOKING_PIPE = ITEMS.registerItem("smoking_pipe",
            SmokingPipeItem::new, new Item.Properties());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
