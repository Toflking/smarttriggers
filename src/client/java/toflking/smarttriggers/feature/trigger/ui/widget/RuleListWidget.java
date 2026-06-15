package toflking.smarttriggers.feature.trigger.ui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import toflking.smarttriggers.feature.trigger.ui.entry.AbstractTriggerRuleEntry;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

public final class RuleListWidget extends ObjectSelectionList<AbstractTriggerRuleEntry> {
    private final TriggerRulesScreenHost host;

    public RuleListWidget(Minecraft client, TriggerRulesScreenHost host, int width, int height, int y, int itemHeight) {
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
    protected boolean entriesCanBeSelected() {
        return false;
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor context, AbstractTriggerRuleEntry entry, int borderColor) {
    }

    @Override
    protected int scrollBarX() {
        return host.layout().contentRight() + 10;
    }
}
