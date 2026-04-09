package toflking.smarttriggers.feature.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.DoubleConsumer;

public class HudEditScreen extends Screen {
    private final DoubleConsumer onScroll;

    private final Screen parent;

    private final HudEditController controller;

    protected HudEditScreen(Screen parent, HudEditController controller, DoubleConsumer onScroll) {
        super(Text.literal("HUD Edit"));
        this.parent = parent;
        this.controller = controller;
        this.onScroll = onScroll;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }


    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public void close() {
        controller.toggleEditMode(parent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        onScroll.accept(verticalAmount);
        return true;
    }
}
