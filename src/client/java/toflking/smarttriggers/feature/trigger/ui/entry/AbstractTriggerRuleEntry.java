package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTriggerRuleEntry extends ObjectSelectionList.Entry<AbstractTriggerRuleEntry> implements ContainerEventHandler {
    protected final TriggerRulesScreenHost host;
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private GuiEventListener focused;
    private boolean dragging;

    public AbstractTriggerRuleEntry(TriggerRulesScreenHost host) {
        this.host = host;
    }

    public <T extends AbstractWidget> T addWidget(T widget) {
        widgets.add(widget);
        return widget;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(GuiEventListener focused) {
        if (this.focused instanceof EditBox previousField) {
            previousField.setFocused(false);
        }
        this.focused = focused;
        if (focused instanceof EditBox focusedField) {
            focusedField.setFocused(true);
        }
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        for (AbstractWidget widget : widgets) {
            if (widget.mouseClicked(click, doubleClick)) {
                setFocused(widget);
                return true;
            }
        }
        setFocused(null);
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        return ContainerEventHandler.super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        return ContainerEventHandler.super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent keyInput) {
        return focused != null && focused.keyPressed(keyInput);
    }

    @Override
    public boolean keyReleased(KeyEvent keyInput) {
        return focused != null && focused.keyReleased(keyInput);
    }

    @Override
    public boolean charTyped(CharacterEvent charInput) {
        return focused != null && focused.charTyped(charInput);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        widgets.forEach(consumer);
    }

    @Override
    public Component getNarration() {
        return Component.literal("Trigger rule entry");
    }
}
