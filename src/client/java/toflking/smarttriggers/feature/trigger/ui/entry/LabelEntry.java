package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;

public final class LabelEntry extends AbstractTriggerRuleEntry {
    private final Component label;

    public LabelEntry(TriggerRulesScreenHost host, String label) {
        super(host);
        this.label = Component.literal(label);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        ctx.text(host.textRenderer(), label, host.layout().contentLeft(), getY() + 2, 0xFFFFFFFF, false);
    }
}
