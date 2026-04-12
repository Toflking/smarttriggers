package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.validation.StateOperatorSupport;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

public final class RuleInputEntry extends AbstractTriggerRuleEntry {
    private final RuleEditorState rule;
    private final ButtonWidget inputTypeButton;
    private final ButtonWidget sourceOrOperatorButton;
    private final ButtonWidget matchButton;

    public RuleInputEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        inputTypeButton = addWidget(ButtonWidget.builder(
                Text.literal(rule.getInputType().getDisplay()),
                button -> {
                    rule.setInputType(rule.getInputType().next());
                    if (rule.getInputType() != RuleInputType.TEXT && !StateOperatorSupport.isOperatorSupported(rule.getStateOperator(), rule.getInputType())) {
                        host.cycleStateOperator(rule);
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, host.layout().inputButtonWidth(), 20).build());

        sourceOrOperatorButton = addWidget(ButtonWidget.builder(
                Text.empty(),
                button -> {
                    if (rule.getInputType() == RuleInputType.TEXT) {
                        rule.setSource(rule.getSource().next());
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, host.layout().compactButtonWidth(), 20).build());

        matchButton = addWidget(ButtonWidget.builder(
                Text.empty(),
                button -> {
                    if (rule.getInputType() == RuleInputType.TEXT) {
                        rule.setMatchType(rule.getMatchType().next());
                    } else {
                        host.cycleStateOperator(rule);
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, host.layout().compactButtonWidth(), 20).build());
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
        if (host.isRightClick(click)) {
            if (inputTypeButton.isMouseOver(click.x(), click.y())) {
                host.playButtonClickSound();
                rule.setInputType(rule.getInputType().previous());
                host.markDirty();
                host.rebuildRuleWidgets();
                setFocused(inputTypeButton);
                return true;
            }
            if (rule.getInputType() == RuleInputType.TEXT && sourceOrOperatorButton.isMouseOver(click.x(), click.y())) {
                host.playButtonClickSound();
                rule.setSource(rule.getSource().previous());
                host.markDirty();
                host.rebuildRuleWidgets();
                setFocused(sourceOrOperatorButton);
                return true;
            }
            if (matchButton.isMouseOver(click.x(), click.y())) {
                host.playButtonClickSound();
                if (rule.getInputType() == RuleInputType.TEXT) {
                    rule.setMatchType(rule.getMatchType().previous());
                } else {
                    host.cycleStateOperator(rule, false);
                }
                host.markDirty();
                host.rebuildRuleWidgets();
                setFocused(matchButton);
                return true;
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        inputTypeButton.setMessage(Text.literal(rule.getInputType().getDisplay()));
        inputTypeButton.setWidth(host.layout().inputButtonWidth());
        sourceOrOperatorButton.setMessage(Text.literal(host.getSourceButtonLabel(rule)));
        sourceOrOperatorButton.active = rule.getInputType() == RuleInputType.TEXT;
        sourceOrOperatorButton.visible = rule.getInputType() == RuleInputType.TEXT;
        sourceOrOperatorButton.setWidth(host.layout().compactButtonWidth());
        matchButton.setMessage(Text.literal(host.getMatchButtonLabel(rule)));
        matchButton.setWidth(host.layout().compactButtonWidth());
        matchButton.active = true;
        matchButton.visible = true;

        inputTypeButton.setPosition(host.layout().contentLeft(), y);
        if (rule.getInputType() == RuleInputType.TEXT) {
            sourceOrOperatorButton.setPosition(host.layout().sourceButtonX(), y);
            matchButton.setPosition(host.layout().matchButtonX(), y);
        } else {
            matchButton.setPosition(host.layout().sourceButtonX(), y);
        }

        inputTypeButton.render(ctx, mouseX, mouseY, tickProgress);
        if (rule.getInputType() == RuleInputType.TEXT) {
            sourceOrOperatorButton.render(ctx, mouseX, mouseY, tickProgress);
        }
        matchButton.render(ctx, mouseX, mouseY, tickProgress);

        if (host.hasRuleIssue(rule, ValidationField.INPUT_TYPE)) {
            host.drawErrorOutline(ctx, inputTypeButton);
        }
        if (rule.getInputType() == RuleInputType.TEXT && host.hasRuleIssue(rule, ValidationField.SOURCE)) {
            host.drawErrorOutline(ctx, sourceOrOperatorButton);
        }
        if (host.hasRuleIssue(rule, rule.getInputType() == RuleInputType.TEXT ? ValidationField.MATCH_TYPE : ValidationField.STATE_OPERATOR)) {
            host.drawErrorOutline(ctx, matchButton);
        }
    }
}
