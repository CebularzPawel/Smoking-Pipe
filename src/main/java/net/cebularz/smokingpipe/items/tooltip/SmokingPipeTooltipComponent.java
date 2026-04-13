package net.cebularz.smokingpipe.items.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class SmokingPipeTooltipComponent implements ClientTooltipComponent {
    public record Tooltip(ItemStack smokable) implements TooltipComponent {}

    private final ItemStack smokable;

    public SmokingPipeTooltipComponent(Tooltip tooltip) {
        this.smokable = tooltip.smokable();
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return 16 + 4 + font.width(smokable.getHoverName());
    }

    @Override
    public void renderImage(Font font, int xPosition, int yPosition, GuiGraphics graphics) {
        graphics.renderItem(smokable, xPosition, yPosition + 2);
        graphics.renderItemDecorations(font, smokable, xPosition, yPosition + 2);
        graphics.drawString(font, smokable.getHoverName(), xPosition + 20, yPosition + 6, 0xFFFFFF, true);
    }
}
