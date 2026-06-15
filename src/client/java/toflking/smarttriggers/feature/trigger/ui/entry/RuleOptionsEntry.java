package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.ui.support.TriggerRulesUiSupport;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.Objects;

public final class RuleOptionsEntry extends AbstractTriggerRuleEntry {
    private static final int RULE_FIELD_MAX_LENGTH = 512;

    private final RuleEditorState rule;
    private final EditBox keyField;
    private final EditBox patternField;
    private final Checkbox caseSensitiveCheckbox;
    private final EditBox cooldownField;
    private final Button cooldownTypeButton;

    public RuleOptionsEntry(TriggerRulesScreenHost host, RuleEditorState rule) {
        super(host);
        this.rule = rule;

        keyField = addWidget(new EditBox(host.textRenderer(), 0, 0, 100, 20, Component.literal("Key")));
        keyField.setMaxLength(RULE_FIELD_MAX_LENGTH);
        keyField.setValue(Objects.toString(rule.getKey(), ""));
        keyField.setHint(Component.literal("Key"));
        keyField.setResponder(value -> {
            rule.setKey(value);
            host.markDirty();
        });

        patternField = addWidget(new EditBox(host.textRenderer(), 0, 0, 100, 20, Component.literal("Pattern")));
        patternField.setMaxLength(RULE_FIELD_MAX_LENGTH);
        patternField.setValue(Objects.toString(rule.getPattern(), ""));
        patternField.setResponder(value -> {
            rule.setPattern(value);
            host.markDirty();
        });

        caseSensitiveCheckbox = addWidget(Checkbox.builder(
                Component.literal("Case Sensitive"),
                host.textRenderer()
        ).pos(0, 0).selected(rule.isCaseSensitive()).onValueChange((checkbox, checked) -> {
            rule.setCaseSensitive(checked);
            host.markDirty();
        }).build());

        cooldownField = addWidget(new EditBox(host.textRenderer(), 0, 0, 90, 20, Component.literal("Cooldown")));
        cooldownField.setValue(Objects.toString(rule.getCooldownString(), "0:00"));
        cooldownField.setHint(Component.literal("Cooldown"));
        cooldownField.setResponder(value -> {
            rule.setCooldownString(value);
            host.markDirty();
        });

        cooldownTypeButton = addWidget(Button.builder(
                Component.literal(host.getCooldownTypeLabel(rule)),
                button -> {
                    TimerFormat currentType = rule.getCooldownType();
                    rule.setCooldownType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                    host.markDirty();
                }
        ).bounds(0, 0, TriggerRulesLayout.COOLDOWN_TIMER_TYPE_WIDTH, 20).build());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
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
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
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
            patternField.setHint(Component.literal("Pattern"));
            patternField.setWidth(host.layout().textPatternWidth());
            patternField.setX(x);
            patternField.setY(y);
            patternField.extractRenderState(ctx, mouseX, mouseY, tickProgress);
            x += host.layout().textPatternWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;
        } else {
            keyField.visible = true;
            keyField.active = true;
            keyField.setWidth(host.layout().stateKeyWidth());
            keyField.setX(x);
            keyField.setY(y);
            keyField.extractRenderState(ctx, mouseX, mouseY, tickProgress);
            x += host.layout().stateKeyWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;

            patternField.visible = !unaryStateOperator;
            patternField.active = !unaryStateOperator;
            if (!unaryStateOperator) {
                patternField.setHint(Component.literal(rule.getInputType() == RuleInputType.FLAG ? "Value" : "Pattern"));
                patternField.setWidth(host.layout().statePatternWidth());
                patternField.setX(x);
                patternField.setY(y);
                patternField.extractRenderState(ctx, mouseX, mouseY, tickProgress);
                x += host.layout().statePatternWidth() + TriggerRulesLayout.ACTION_FIELD_GAP;
            }
        }

        caseSensitiveCheckbox.visible = textInput;
        caseSensitiveCheckbox.active = textInput;
        if (textInput) {
            caseSensitiveCheckbox.setX(x);
            caseSensitiveCheckbox.setY(y + 2);
            caseSensitiveCheckbox.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        }

        cooldownField.setWidth(host.layout().cooldownFieldWidth());
        cooldownField.setX(host.layout().cooldownFieldX());
        cooldownField.setY(y);
        cooldownField.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        cooldownTypeButton.setMessage(Component.literal(host.getCooldownTypeLabel(rule)));
        cooldownTypeButton.setWidth(TriggerRulesLayout.COOLDOWN_TIMER_TYPE_WIDTH);
        cooldownTypeButton.setX(host.layout().cooldownTypeButtonX());
        cooldownTypeButton.setY(y);
        cooldownTypeButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);

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
