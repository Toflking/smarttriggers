package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

public final class LabelEntry extends AbstractTriggerRuleEntry {
    private final Text label;

    public LabelEntry(TriggerRulesScreenHost host, String label) {
        super(host);
        this.label = Text.literal(label);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        ctx.drawText(host.textRenderer(), label, host.layout().contentLeft(), getY() + 2, 0xFFFFFFFF, false);
    }
}
