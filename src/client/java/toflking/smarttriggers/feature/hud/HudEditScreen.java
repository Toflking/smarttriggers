package toflking.smarttriggers.feature.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;

public class HudEditScreen extends Screen {
    private final DoubleConsumer onScroll;

    private final Screen parent;

    private final HudEditController controller;

    protected HudEditScreen(Screen parent, HudEditController controller, DoubleConsumer onScroll) {
        super(Component.literal("HUD Edit"));
        this.parent = parent;
        this.controller = controller;
        this.onScroll = onScroll;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public void onClose() {
        controller.toggleEditMode(parent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        onScroll.accept(verticalAmount);
        return true;
    }
}
