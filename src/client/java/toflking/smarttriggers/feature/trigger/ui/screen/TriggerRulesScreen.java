package toflking.smarttriggers.feature.trigger.ui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import toflking.smarttriggers.core.config.ConfigIO;
import toflking.smarttriggers.feature.trigger.RuntimeReloader;
import toflking.smarttriggers.feature.trigger.ui.TriggerRulesController;
import toflking.smarttriggers.feature.trigger.ui.entry.*;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.support.TriggerRulesUiSupport;
import toflking.smarttriggers.feature.trigger.ui.widget.RuleListWidget;
import toflking.smarttriggers.feature.trigger.validation.StateOperatorSupport;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;
import toflking.smarttriggers.feature.trigger.validation.ValidationResult;
import toflking.smarttriggers.feature.trigger.validation.editor.TriggerEditorValidator;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TriggerRulesScreen extends Screen implements TriggerRulesScreenHost {
    private static final int ROW_HEIGHT = 24;

    private final Screen parent;
    private final TriggerRulesController controller;
    private final RuntimeReloader applier;

    private ButtonWidget newRuleButton;
    private ButtonWidget moveHudButton;
    private ButtonWidget saveButton;
    private ButtonWidget closeButton;
    private RuleListWidget ruleListWidget;
    private final List<ValidationIssue> validationIssues = new ArrayList<>();
    private String validationSummary;
    private TriggerRulesLayout layout;

    public TriggerRulesScreen(Screen parent, TriggerRulesController controller, RuntimeReloader applier) {
        super(Text.literal("Trigger Rules"));
        this.parent = parent;
        this.controller = controller;
        this.applier = applier;
    }

    @Override
    protected void init() {
        clearChildren();
        layout = new TriggerRulesLayout(width);

        newRuleButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("New Rule"),
                button -> {
                    controller.addRule();
                    rebuildRuleWidgets();
                }
        ).dimensions((width / 2) - 106, 20, 100, 20).build());

        moveHudButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("Edit Gui Locations"),
                button -> {
                    if (saveConfig()) {
                        controller.getHudEditController().toggleEditMode(this);
                    }
                }
        ).dimensions((width / 2) + 6, 20, 100, 20).build());

        saveButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                button -> save()
        ).dimensions((width / 2) - 106, height - 24, 100, 20).build());

        closeButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                button -> close()
        ).dimensions((width / 2) + 6, height - 24, 100, 20).build());

        ruleListWidget = addDrawableChild(new RuleListWidget(
                client,
                this,
                width,
                height - 76,
                48,
                ROW_HEIGHT
        ));

        rebuildRuleWidgets();
    }

    public void save() {
        if (saveConfig()) {
            close();
        }
    }

    private boolean saveConfig() {
        List<ValidationIssue> editorIssues = TriggerEditorValidator.validateRules(controller.getRules());
        if (!editorIssues.isEmpty()) {
            validationIssues.clear();
            validationIssues.addAll(editorIssues);
            validationSummary = buildValidationSummary(editorIssues);
            expandRulesWithIssues(editorIssues);
            rebuildRuleWidgets();
            scrollToFirstIssue(editorIssues);
            return false;
        }

        controller.saveToConfig();
        ValidationResult result = applier.reload(controller.getConfig());

        if (result.success()) {
            validationIssues.clear();
            validationSummary = null;
            ConfigIO.save(controller.getConfig());
            return true;
        }
        validationIssues.clear();
        validationIssues.addAll(result.errors());
        validationSummary = buildValidationSummary(result.errors());
        expandRulesWithIssues(result.errors());
        rebuildRuleWidgets();
        scrollToFirstIssue(result.errors());
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void rebuildRuleWidgets() {
        if (ruleListWidget == null) {
            return;
        }

        ruleListWidget.resetEntries();
        for (int ruleIndex = 0; ruleIndex < controller.getRules().size(); ruleIndex++) {
            RuleEditorState rule = controller.getRules().get(ruleIndex);
            ruleListWidget.appendEntry(new RuleSummaryEntry(this, rule));

            if (rule.isExpanded()) {
                ruleListWidget.appendEntry(new LabelEntry(this, "Source:"));
                ruleListWidget.appendEntry(new RuleInputEntry(this, rule));
                ruleListWidget.appendEntry(new RuleOptionsEntry(this, rule));
                ruleListWidget.appendEntry(new LabelEntry(this, "Actions:"));

                for (int actionIndex = 0; actionIndex < rule.getActions().size(); actionIndex++) {
                    ruleListWidget.appendEntry(new ActionEntry(this, rule, rule.getActions().get(actionIndex), actionIndex));
                }
            }

            if (ruleIndex < controller.getRules().size() - 1) {
                ruleListWidget.appendEntry(new RuleDividerEntry(this));
            }
        }
    }

    @Override
    public net.minecraft.client.font.TextRenderer textRenderer() {
        return textRenderer;
    }

    @Override
    public TriggerRulesLayout layout() {
        return layout;
    }

    @Override
    public void markDirty() {
        controller.dirty = true;
    }

    @Override
    public void removeRule(RuleEditorState rule) {
        controller.removeRule(rule);
    }

    @Override
    public int rowHeight() {
        return ROW_HEIGHT;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        String title = "Smart Trigger Configuration Screen";
        int titleX = (width - textRenderer.getWidth(title)) / 2;
        ctx.drawText(textRenderer, title, titleX, 8, 0xFFFFFFFF, false);
        if (validationSummary != null) {
            ctx.drawText(textRenderer, validationSummary, layout.contentLeft(), 8, 0xFFFF7070, false);
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
        if (ruleListWidget != null && ruleListWidget.keyPressed(keyInput)) {
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyInput keyInput) {
        if (ruleListWidget != null && ruleListWidget.keyReleased(keyInput)) {
            return true;
        }
        return super.keyReleased(keyInput);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput charInput) {
        if (ruleListWidget != null && ruleListWidget.charTyped(charInput)) {
            return true;
        }
        return super.charTyped(charInput);
    }

    @Override
    public ActionEditorState createDefaultAction() {
        return TriggerRulesUiSupport.createDefaultAction();
    }

    @Override
    public String getTimerTypeLabel(ActionEditorState action) {
        return TriggerRulesUiSupport.getTimerTypeLabel(action);
    }

    @Override
    public String getCooldownTypeLabel(RuleEditorState rule) {
        return TriggerRulesUiSupport.getCooldownTypeLabel(rule);
    }

    @Override
    public ValidationField toValidationField(ActionFieldSpec spec) {
        return TriggerRulesUiSupport.toValidationField(spec);
    }

    @Override
    public void drawErrorOutline(DrawContext ctx, ClickableWidget widget) {
        TriggerRulesUiSupport.drawErrorOutline(ctx, widget);
    }

    @Override
    public boolean hasRuleIssue(RuleEditorState rule, ValidationField field) {
        int ruleIndex = controller.getRules().indexOf(rule);
        for (ValidationIssue issue : validationIssues) {
            if (issue.ruleIndex() == ruleIndex && issue.actionIndex() == null && issue.field() == field) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasActionIssue(RuleEditorState rule, int actionIndex, ValidationField field) {
        int ruleIndex = controller.getRules().indexOf(rule);
        for (ValidationIssue issue : validationIssues) {
            if (issue.ruleIndex() == ruleIndex && Objects.equals(issue.actionIndex(), actionIndex) && issue.field() == field) {
                return true;
            }
        }
        return false;
    }

    private String buildValidationSummary(List<ValidationIssue> issues) {
        if (issues.isEmpty()) {
            return null;
        }
        String firstMessage = issues.getFirst().message();
        if (issues.size() == 1) {
            return "Save failed: " + firstMessage;
        }
        return "Save failed: " + issues.size() + " issues. First: " + firstMessage;
    }

    private void expandRulesWithIssues(List<ValidationIssue> issues) {
        for (ValidationIssue issue : issues) {
            if (issue.ruleIndex() >= 0 && issue.ruleIndex() < controller.getRules().size()) {
                controller.getRules().get(issue.ruleIndex()).setExpanded(true);
            }
        }
    }

    private void scrollToFirstIssue(List<ValidationIssue> issues) {
        if (ruleListWidget == null || issues.isEmpty()) {
            return;
        }
        ValidationIssue firstRuleIssue = issues.stream()
                .filter(issue -> issue.ruleIndex() >= 0)
                .findFirst()
                .orElse(null);
        if (firstRuleIssue == null) {
            return;
        }
        int targetRuleIndex = firstRuleIssue.ruleIndex();
        int entryIndex = 0;
        for (int i = 0; i < targetRuleIndex; i++) {
            entryIndex++;
            RuleEditorState rule = controller.getRules().get(i);
            if (rule.isExpanded()) {
                entryIndex += 4 + rule.getActions().size();
            }
        }
        ruleListWidget.setScrollY((double) entryIndex * ROW_HEIGHT);
    }

    @Override
    public void cycleStateOperator(RuleEditorState rule) {
        cycleStateOperator(rule, true);
    }

    @Override
    public void cycleStateOperator(RuleEditorState rule, boolean forward) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null) {
            rule.setStateOperator(StateOperator.IS);
            return;
        }

        StateOperator next = forward ? operator.next() : operator.previous();
        while (!StateOperatorSupport.isOperatorSupported(next, rule.getInputType()) && next != operator) {
            next = forward ? next.next() : next.previous();
        }
        rule.setStateOperator(next);
    }

    @Override
    public boolean isRightClick(net.minecraft.client.gui.Click click) {
        return click.button() == 1;
    }

    @Override
    public void playButtonClickSound() {
        if (client == null) {
            return;
        }
        client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public String getSourceButtonLabel(RuleEditorState rule) {
        return rule.getSource().getDisplay();
    }

    @Override
    public String getMatchButtonLabel(RuleEditorState rule) {
        if (rule.getInputType() == RuleInputType.TEXT) {
            return rule.getMatchType().getDisplay();
        }
        StateOperator operator = rule.getStateOperator();
        return operator == null ? StateOperator.IS.getDisplay() : operator.getDisplay();
    }
}
