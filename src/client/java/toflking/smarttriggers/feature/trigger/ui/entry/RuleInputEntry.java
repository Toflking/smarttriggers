package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.StateOperatorSupport;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

public final class RuleInputEntry extends AbstractTriggerRuleEntry {
    private final RuleEditorState rule;
    private final Button inputTypeButton;
    private final Button sourceOrOperatorButton;
    private final Button matchButton;

    public RuleInputEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        inputTypeButton = addWidget(Button.builder(
                Component.literal(rule.getInputType().getDisplay()),
                button -> {
                    rule.setInputType(rule.getInputType().next());
                    if (rule.getInputType() != RuleInputType.TEXT && !StateOperatorSupport.isOperatorSupported(rule.getStateOperator(), rule.getInputType())) {
                        host.cycleStateOperator(rule);
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, host.layout().inputButtonWidth(), 20).build());

        sourceOrOperatorButton = addWidget(Button.builder(
                Component.empty(),
                button -> {
                    if (rule.getInputType() == RuleInputType.TEXT) {
                        rule.setSource(rule.getSource().next());
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, host.layout().compactButtonWidth(), 20).build());

        matchButton = addWidget(Button.builder(
                Component.empty(),
                button -> {
                    if (rule.getInputType() == RuleInputType.TEXT) {
                        rule.setMatchType(rule.getMatchType().next());
                    } else {
                        host.cycleStateOperator(rule);
                    }
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, host.layout().compactButtonWidth(), 20).build());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
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
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        inputTypeButton.setMessage(Component.literal(rule.getInputType().getDisplay()));
        inputTypeButton.setWidth(host.layout().inputButtonWidth());
        sourceOrOperatorButton.setMessage(Component.literal(host.getSourceButtonLabel(rule)));
        sourceOrOperatorButton.active = rule.getInputType() == RuleInputType.TEXT;
        sourceOrOperatorButton.visible = rule.getInputType() == RuleInputType.TEXT;
        sourceOrOperatorButton.setWidth(host.layout().compactButtonWidth());
        matchButton.setMessage(Component.literal(host.getMatchButtonLabel(rule)));
        matchButton.setWidth(host.layout().compactButtonWidth());
        matchButton.active = true;
        matchButton.visible = true;

        inputTypeButton.setX(host.layout().contentLeft());
        inputTypeButton.setY(y);
        if (rule.getInputType() == RuleInputType.TEXT) {
            sourceOrOperatorButton.setX(host.layout().sourceButtonX());
            sourceOrOperatorButton.setY(y);
            matchButton.setX(host.layout().matchButtonX());
            matchButton.setY(y);
        } else {
            matchButton.setX(host.layout().sourceButtonX());
            matchButton.setY(y);
        }

        inputTypeButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        if (rule.getInputType() == RuleInputType.TEXT) {
            sourceOrOperatorButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        }
        matchButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);

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
