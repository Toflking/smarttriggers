package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.Objects;

public final class RuleSummaryEntry extends AbstractTriggerRuleEntry {
    private static final int RULE_NAME_MAX_LENGTH = 128;

    private final RuleEditorState rule;
    private final TextFieldWidget ruleNameField;
    private final CheckboxWidget enabledCheckbox;
    private final ButtonWidget deleteButton;
    private final ButtonWidget expandButton;

    public RuleSummaryEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        ruleNameField = addWidget(new TextFieldWidget(host.textRenderer(), 0, 0, host.layout().summaryNameWidth(), 20, Text.literal("Name")));
        ruleNameField.setMaxLength(RULE_NAME_MAX_LENGTH);
        ruleNameField.setText(Objects.toString(rule.getId(), ""));
        ruleNameField.setChangedListener(value -> {
            rule.setId(value);
            host.markDirty();
        });

        enabledCheckbox = addWidget(CheckboxWidget.builder(
                Text.literal("Enabled"),
                host.textRenderer()
        ).pos(0, 0).checked(rule.isEnabled()).callback((checkbox, checked) -> {
            rule.setEnabled(checked);
            host.markDirty();
        }).build());

        deleteButton = addWidget(ButtonWidget.builder(
                Text.literal("🗑"),
                button -> {
                    host.removeRule(rule);
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, 20, 20).build());

        expandButton = addWidget(ButtonWidget.builder(
                Text.literal(rule.isExpandedDisplay()),
                button -> {
                    rule.setExpanded(!rule.isExpanded());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, 20, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        ruleNameField.setWidth(host.layout().summaryNameWidth());
        ruleNameField.setPosition(host.layout().contentLeft(), y);
        enabledCheckbox.setPosition(host.layout().summaryEnabledX(), y + 2);
        deleteButton.setPosition(host.layout().summaryDeleteX(), y);
        expandButton.setPosition(host.layout().summaryExpandX(), y);

        ruleNameField.render(ctx, mouseX, mouseY, tickProgress);
        enabledCheckbox.render(ctx, mouseX, mouseY, tickProgress);
        deleteButton.render(ctx, mouseX, mouseY, tickProgress);
        expandButton.render(ctx, mouseX, mouseY, tickProgress);

        if (host.hasRuleIssue(rule, ValidationField.RULE_ID)) {
            host.drawErrorOutline(ctx, ruleNameField);
        }
    }
}
