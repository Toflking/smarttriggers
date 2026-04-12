package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.support.TriggerRulesUiSupport;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.Objects;

public final class RuleOptionsEntry extends AbstractTriggerRuleEntry {
    private static final int RULE_FIELD_MAX_LENGTH = 512;

    private final RuleEditorState rule;
    private final TextFieldWidget keyField;
    private final TextFieldWidget patternField;
    private final CheckboxWidget caseSensitiveCheckbox;
    private final TextFieldWidget cooldownField;
    private final ButtonWidget cooldownTypeButton;

    public RuleOptionsEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        keyField = addWidget(new TextFieldWidget(host.textRenderer(), 0, 0, 100, 20, Text.literal("Key")));
        keyField.setMaxLength(RULE_FIELD_MAX_LENGTH);
        keyField.setText(Objects.toString(rule.getKey(), ""));
        keyField.setPlaceholder(Text.literal("Key"));
        keyField.setChangedListener(value -> {
            rule.setKey(value);
            host.markDirty();
        });

        patternField = addWidget(new TextFieldWidget(host.textRenderer(), 0, 0, 100, 20, Text.literal("Pattern")));
        patternField.setMaxLength(RULE_FIELD_MAX_LENGTH);
        patternField.setText(Objects.toString(rule.getPattern(), ""));
        patternField.setChangedListener(value -> {
            rule.setPattern(value);
            host.markDirty();
        });

        caseSensitiveCheckbox = addWidget(CheckboxWidget.builder(
                Text.literal("Case Sensitive"),
                host.textRenderer()
        ).pos(0, 0).checked(rule.isCaseSensitive()).callback((checkbox, checked) -> {
            rule.setCaseSensitive(checked);
            host.markDirty();
        }).build());

        cooldownField = addWidget(new TextFieldWidget(host.textRenderer(), 0, 0, 90, 20, Text.literal("Cooldown")));
        cooldownField.setText(Objects.toString(rule.getCooldownString(), "0:00"));
        cooldownField.setPlaceholder(Text.literal("Cooldown"));
        cooldownField.setChangedListener(value -> {
            rule.setCooldownString(value);
            host.markDirty();
        });

        cooldownTypeButton = addWidget(ButtonWidget.builder(
                Text.literal(host.getCooldownTypeLabel(rule)),
                button -> {
                    TimerFormat currentType = rule.getCooldownType();
                    rule.setCooldownType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                    host.markDirty();
                }
        ).dimensions(0, 0, TriggerRulesLayout.COOLDOWN_TIMER_TYPE_WIDTH, 20).build());
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
        if (host.isRightClick(click) && cooldownTypeButton.isMouseOver(click.x(), click.y())) {
            host.playButtonClickSound();
            TimerFormat currentType = rule.getCooldownType();
            rule.setCooldownType((currentType == null ? TimerFormat.SECONDS : currentType).previous());
            host.markDirty();
            setFocused(cooldownTypeButton);
            return true;
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        boolean textInput = rule.getInputType() == RuleInputType.TEXT;
        boolean unaryStateOperator = rule.getInputType() != RuleInputType.TEXT
                && rule.getStateOperator() != null
                && rule.getStateOperator().isUnary();

        TriggerRulesUiSupport.syncField(keyField, Objects.toString(rule.getKey(), ""));
        TriggerRulesUiSupport.syncField(patternField, Objects.toString(rule.getPattern(), ""));
        TriggerRulesUiSupport.syncField(cooldownField, Objects.toString(rule.getCooldownString(), "0:00"));
        int x = host.layout().contentLeft();
        if (textInput) {
            keyField.visible = false;
            patternField.visible = true;
            patternField.active = true;
            patternField.setSuggestion(null);
            patternField.setPlaceholder(Text.literal("Pattern"));
            patternField.setWidth(host.layout().textPatternWidth());
            patternField.setPosition(x, y);
            patternField.render(ctx, mouseX, mouseY, tickProgress);
            x += host.layout().textPatternWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;
        } else {
            keyField.visible = true;
            keyField.active = true;
            keyField.setWidth(host.layout().stateKeyWidth());
            keyField.setPosition(x, y);
            keyField.render(ctx, mouseX, mouseY, tickProgress);
            x += host.layout().stateKeyWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;

            patternField.visible = !unaryStateOperator;
            patternField.active = !unaryStateOperator;
            if (!unaryStateOperator) {
                patternField.setPlaceholder(Text.literal(rule.getInputType() == RuleInputType.FLAG ? "Value" : "Pattern"));
                patternField.setWidth(host.layout().statePatternWidth());
                patternField.setPosition(x, y);
                patternField.render(ctx, mouseX, mouseY, tickProgress);
                x += host.layout().statePatternWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;
            }
        }

        caseSensitiveCheckbox.visible = textInput;
        caseSensitiveCheckbox.active = textInput;
        if (textInput) {
            caseSensitiveCheckbox.setPosition(x, y + 2);
            caseSensitiveCheckbox.render(ctx, mouseX, mouseY, tickProgress);
        }

        cooldownField.setWidth(host.layout().cooldownFieldWidth());
        cooldownField.setPosition(host.layout().cooldownFieldX(), y);
        cooldownField.render(ctx, mouseX, mouseY, tickProgress);
        cooldownTypeButton.setMessage(Text.literal(host.getCooldownTypeLabel(rule)));
        cooldownTypeButton.setWidth(TriggerRulesLayout.COOLDOWN_TIMER_TYPE_WIDTH);
        cooldownTypeButton.setPosition(host.layout().cooldownTypeButtonX(), y);
        cooldownTypeButton.render(ctx, mouseX, mouseY, tickProgress);

        if (!textInput && host.hasRuleIssue(rule, ValidationField.RULE_KEY)) {
            host.drawErrorOutline(ctx, keyField);
        }
        if (host.hasRuleIssue(rule, ValidationField.RULE_PATTERN)) {
            host.drawErrorOutline(ctx, patternField);
        }
        if (host.hasRuleIssue(rule, ValidationField.COOLDOWN)) {
            host.drawErrorOutline(ctx, cooldownField);
            host.drawErrorOutline(ctx, cooldownTypeButton);
        }
    }
}
