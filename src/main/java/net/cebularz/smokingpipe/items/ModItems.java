package net.cebularz.smokingpipe.items;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.items.custom.SmokingPipeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmokingPipe.MOD_ID);

    public static final DeferredItem<SmokingPipeItem> SMOKING_PIPE = ITEMS.registerItem("smoking_pipe",
            SmokingPipeItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));

    public static final DeferredItem<Item> LEAF_OF_WISDOM = ITEMS.registerItem("leaf_of_wisdom",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
