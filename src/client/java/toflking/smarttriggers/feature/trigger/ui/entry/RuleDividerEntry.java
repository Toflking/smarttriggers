package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

public final class RuleDividerEntry extends AbstractTriggerRuleEntry {
    public RuleDividerEntry(TriggerRulesScreenHost host) {
        super(host);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int centerY = getY() + (host.rowHeight() / 2);
        int left = host.layout().contentLeft();
        int right = host.layout().contentRight();
        ctx.fill(left, centerY, right, centerY + 1, 0x44FFFFFF);
    }
}
