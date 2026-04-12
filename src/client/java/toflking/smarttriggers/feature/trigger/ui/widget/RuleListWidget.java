package toflking.smarttriggers.feature.trigger.ui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import toflking.smarttriggers.feature.trigger.ui.entry.AbstractTriggerRuleEntry;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

public final class RuleListWidget extends AlwaysSelectedEntryListWidget<AbstractTriggerRuleEntry> {
    private final TriggerRulesScreenHost host;

    public RuleListWidget(MinecraftClient client, TriggerRulesScreenHost host, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.host = host;
    }

    public void resetEntries() {
        clearEntries();
    }

    public void appendEntry(AbstractTriggerRuleEntry entry) {
        addEntry(entry);
    }

    @Override
    public int getRowWidth() {
        return host.layout().contentWidth();
    }

    @Override
    protected boolean isEntrySelectionAllowed() {
        return false;
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, AbstractTriggerRuleEntry entry, int borderColor) {
    }

    @Override
    protected int getScrollbarX() {
        return host.layout().contentRight() + 10;
    }
}
