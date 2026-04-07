package net.cebularz.smokingpipe.events;

import net.cebularz.smokingpipe.SmokingPipe;
import net.cebularz.smokingpipe.effects.ModEffects;
import net.cebularz.smokingpipe.items.ModItems;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.List;

@EventBusSubscriber(modid = SmokingPipe.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    private static void onPlayerXPPickUp(PlayerXpEvent.PickupXp event){
        Player player = event.getEntity();

        if (player.hasEffect(ModEffects.WISDOM_EFFECT)) {
            int originalXp = event.getOrb().value;

            int boostedXp = Math.round(originalXp * 1.25f);

            event.getOrb().value = boostedXp;
        }
    }
    @SubscribeEvent
    public static void addWanderingTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        rareTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                new ItemStack(ModItems.SMOKING_PIPE.get(), 1), 1, 10, 0.2f));

    }
}
