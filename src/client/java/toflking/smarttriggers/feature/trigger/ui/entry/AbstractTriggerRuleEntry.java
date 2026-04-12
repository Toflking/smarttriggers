package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTriggerRuleEntry extends AlwaysSelectedEntryListWidget.Entry<AbstractTriggerRuleEntry> implements ParentElement {
    protected final TriggerRulesScreenHost host;
    private final List<ClickableWidget> widgets = new ArrayList<>();
    private Element focused;
    private boolean dragging;

    public AbstractTriggerRuleEntry(TriggerRulesScreenHost host) {
        this.host = host;
    }

    public <T extends ClickableWidget> T addWidget(T widget) {
        widgets.add(widget);
        return widget;
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }

    @Override
    public Element getFocused() {
        return focused;
    }

    @Override
    public void setFocused(Element focused) {
        if (this.focused instanceof TextFieldWidget previousField) {
            previousField.setFocused(false);
        }
        this.focused = focused;
        if (focused instanceof TextFieldWidget focusedField) {
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
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
        for (ClickableWidget widget : widgets) {
            if (widget.mouseClicked(click, doubleClick)) {
                setFocused(widget);
                return true;
            }
        }
        setFocused(null);
        return false;
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        return ParentElement.super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        return ParentElement.super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return ParentElement.super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
        return focused != null && focused.keyPressed(keyInput);
    }

    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyInput keyInput) {
        return focused != null && focused.keyReleased(keyInput);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput charInput) {
        return focused != null && focused.charTyped(charInput);
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        widgets.forEach(consumer);
    }

    @Override
    public Text getNarration() {
        return Text.literal("Trigger rule entry");
    }
}
