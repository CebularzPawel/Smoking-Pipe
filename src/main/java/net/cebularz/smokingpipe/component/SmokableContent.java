package net.cebularz.smokingpipe.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record SmokableContent(ItemStack stack) {
    public static final Codec<SmokableContent> CODEC =
            ItemStack.CODEC.xmap(SmokableContent::new, SmokableContent::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, SmokableContent> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(SmokableContent::new, SmokableContent::stack);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SmokableContent(ItemStack itemStack))) return false;
        return ItemStack.matches(this.stack, itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getItem(), stack.getCount());
    }
}
