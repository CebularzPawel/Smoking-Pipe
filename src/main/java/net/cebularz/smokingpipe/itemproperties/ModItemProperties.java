package net.cebularz.smokingpipe.itemproperties;

import net.cebularz.smokingpipe.items.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItemProperties {
    public static void register() {
        Item smokingPipe = ModItems.SMOKING_PIPE.get();

        ItemProperties.register(smokingPipe,
                ResourceLocation.fromNamespaceAndPath("smokingpipe", "smoking"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack) {
                        return 1.0F;
                    }
                    return 0.0F;
                });
    }
}
