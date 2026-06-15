package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.Objects;

public final class RuleSummaryEntry extends AbstractTriggerRuleEntry {
    private static final int RULE_NAME_MAX_LENGTH = 128;

    private final RuleEditorState rule;
    private final EditBox ruleNameField;
    private final Checkbox enabledCheckbox;
    private final Button deleteButton;
    private final Button expandButton;

    public RuleSummaryEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        ruleNameField = addWidget(new EditBox(host.textRenderer(), 0, 0, host.layout().summaryNameWidth(), 20, Component.literal("Name")));
        ruleNameField.setMaxLength(RULE_NAME_MAX_LENGTH);
        ruleNameField.setValue(Objects.toString(rule.getId(), ""));
        ruleNameField.setResponder(value -> {
            rule.setId(value);
            host.markDirty();
        });

        enabledCheckbox = addWidget(Checkbox.builder(
                Component.literal("Enabled"),
                host.textRenderer()
        ).pos(0, 0).selected(rule.isEnabled()).onValueChange((checkbox, checked) -> {
            rule.setEnabled(checked);
            host.markDirty();
        }).build());

        deleteButton = addWidget(Button.builder(
                Component.literal("🗑"),
                button -> {
                    host.removeRule(rule);
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, 20, 20).build());

        expandButton = addWidget(Button.builder(
                Component.literal(rule.isExpandedDisplay()),
                button -> {
                    rule.setExpanded(!rule.isExpanded());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, 20, 20).build());
    }

    @Override
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        ruleNameField.setWidth(host.layout().summaryNameWidth());
        ruleNameField.setX(host.layout().contentLeft());
        ruleNameField.setY(y);
        enabledCheckbox.setX(host.layout().summaryEnabledX());
        enabledCheckbox.setY(y + 2);
        deleteButton.setX(host.layout().summaryDeleteX());
        deleteButton.setY(y);
        expandButton.setX(host.layout().summaryExpandX());
        expandButton.setY(y);

        ruleNameField.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        enabledCheckbox.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        deleteButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        expandButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);

        if (host.hasRuleIssue(rule, ValidationField.RULE_ID)) {
            host.drawErrorOutline(ctx, ruleNameField);
        }
    }
}
