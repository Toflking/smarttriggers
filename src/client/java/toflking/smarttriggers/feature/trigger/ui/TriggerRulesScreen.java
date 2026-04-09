package toflking.smarttriggers.feature.trigger.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.core.config.ConfigIO;
import toflking.smarttriggers.feature.trigger.RuntimeReloader;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;
import toflking.smarttriggers.feature.trigger.validation.ValidationResult;
import toflking.smarttriggers.feature.trigger.validation.editor.TriggerEditorValidator;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionUiMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TriggerRulesScreen extends Screen {
    private static final int ROW_HEIGHT = 24;

    private static final int ACTION_INDEX_X = 0;
    private static final int ACTION_FIELD_GAP = 8;
    private static final int ACTION_LABEL_WIDTH = 24;
    private static final int ACTION_ROW_BUTTON_WIDTH = 20;
    private static final int ACTION_ROW_BUTTON_GAP = 4;
    private static final int ACTION_TIMER_TYPE_WIDTH = 40;
    private static final int COOLDOWN_TIMER_TYPE_WIDTH = 40;

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

    public TriggerRulesScreen(Screen parent, TriggerRulesController controller, RuntimeReloader applier) {
        super(Text.literal("Trigger Rules"));
        this.parent = parent;
        this.controller = controller;
        this.applier = applier;
    }

    @Override
    protected void init() {
        clearChildren();

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

    private void rebuildRuleWidgets() {
        if (ruleListWidget == null) {
            return;
        }

        ruleListWidget.resetEntries();
        for (int ruleIndex = 0; ruleIndex < controller.getRules().size(); ruleIndex++) {
            RuleEditorState rule = controller.getRules().get(ruleIndex);
            ruleListWidget.appendEntry(new RuleSummaryEntry(rule));

            if (rule.isExpanded()) {
                ruleListWidget.appendEntry(new LabelEntry("Source:"));
                ruleListWidget.appendEntry(new RuleInputEntry(rule));
                ruleListWidget.appendEntry(new RuleOptionsEntry(rule));
                ruleListWidget.appendEntry(new LabelEntry("Actions:"));

                for (int actionIndex = 0; actionIndex < rule.getActions().size(); actionIndex++) {
                    ruleListWidget.appendEntry(new ActionEntry(rule, rule.getActions().get(actionIndex), actionIndex));
                }
            }

            if (ruleIndex < controller.getRules().size() - 1) {
                ruleListWidget.appendEntry(new RuleDividerEntry());
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        String title = "Smart Trigger Configuration Screen";
        int titleX = (width - textRenderer.getWidth(title)) / 2;
        ctx.drawText(textRenderer, title, titleX, 8, 0xFFFFFFFF, false);
        if (validationSummary != null) {
            ctx.drawText(textRenderer, validationSummary, contentLeft(), 8, 0xFFFF7070, false);
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

    private abstract class BaseEntry extends AlwaysSelectedEntryListWidget.Entry<BaseEntry> implements ParentElement {
        private final List<ClickableWidget> widgets = new ArrayList<>();
        private Element focused;
        private boolean dragging;

        protected <T extends ClickableWidget> T addWidget(T widget) {
            widgets.add(widget);
            return widget;
        }

        @Override
        public List<? extends Element> children() {
            return widgets;
        }

        @Override
        public Element getFocused() {
            return focused;
        }

        @Override
        public void setFocused(Element focused) {
            if (this.focused instanceof TextFieldWidget previousField) {
                previousField.setFocused(false);
            }
            this.focused = focused;
            if (focused instanceof TextFieldWidget focusedField) {
                focusedField.setFocused(true);
            }
        }

        @Override
        public boolean isDragging() {
            return dragging;
        }

        @Override
        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
            for (ClickableWidget widget : widgets) {
                if (widget.mouseClicked(click, doubleClick)) {
                    setFocused(widget);
                    return true;
                }
            }
            setFocused(null);
            return false;
        }

        @Override
        public boolean mouseReleased(net.minecraft.client.gui.Click click) {
            return ParentElement.super.mouseReleased(click);
        }

        @Override
        public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
            return ParentElement.super.mouseDragged(click, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            return ParentElement.super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
            return focused != null && focused.keyPressed(keyInput);
        }

        @Override
        public boolean keyReleased(net.minecraft.client.input.KeyInput keyInput) {
            return focused != null && focused.keyReleased(keyInput);
        }

        @Override
        public boolean charTyped(net.minecraft.client.input.CharInput charInput) {
            return focused != null && focused.charTyped(charInput);
        }

        @Override
        public void forEachChild(Consumer<ClickableWidget> consumer) {
            widgets.forEach(consumer);
        }

        @Override
        public Text getNarration() {
            return Text.literal("Trigger rule entry");
        }
    }

    private final class LabelEntry extends BaseEntry {
        private final Text label;

        private LabelEntry(String label) {
            this.label = Text.literal(label);
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            ctx.drawText(textRenderer, label, contentLeft(), getY() + 2, 0xFFFFFFFF, false);
        }
    }

    private final class RuleDividerEntry extends BaseEntry {
        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int centerY = getY() + (ROW_HEIGHT / 2);
            int left = contentLeft();
            int right = contentRight();
            ctx.fill(left, centerY, right, centerY + 1, 0x44FFFFFF);
        }
    }

    private final class RuleSummaryEntry extends BaseEntry {
        private final RuleEditorState rule;
        private final TextFieldWidget ruleNameField;
        private final CheckboxWidget enabledCheckbox;
        private final ButtonWidget deleteButton;
        private final ButtonWidget expandButton;

        private RuleSummaryEntry(RuleEditorState rule) {
            this.rule = rule;

            ruleNameField = addWidget(new TextFieldWidget(textRenderer, 0, 0, summaryNameWidth(), 20, Text.literal("Name")));
            ruleNameField.setText(Objects.toString(rule.getId(), ""));
            ruleNameField.setChangedListener(value -> {
                rule.setId(value);
                controller.dirty = true;
            });

            enabledCheckbox = addWidget(CheckboxWidget.builder(
                    Text.literal("Enabled"),
                    textRenderer
            ).pos(0, 0).checked(rule.isEnabled()).callback((checkbox, checked) -> {
                rule.setEnabled(checked);
                controller.dirty = true;
            }).build());

            deleteButton = addWidget(ButtonWidget.builder(
                    Text.literal("🗑"),
                    button -> {
                        controller.removeRule(rule);
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, 20, 20).build());

            expandButton = addWidget(ButtonWidget.builder(
                    Text.literal(rule.isExpandedDisplay()),
                    button -> {
                        rule.setExpanded(!rule.isExpanded());
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, 20, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int y = getY();
            ruleNameField.setWidth(summaryNameWidth());
            ruleNameField.setPosition(contentLeft(), y);
            enabledCheckbox.setPosition(summaryEnabledX(), y + 2);
            deleteButton.setPosition(summaryDeleteX(), y);
            expandButton.setPosition(summaryExpandX(), y);

            ruleNameField.render(ctx, mouseX, mouseY, tickProgress);
            enabledCheckbox.render(ctx, mouseX, mouseY, tickProgress);
            deleteButton.render(ctx, mouseX, mouseY, tickProgress);
            expandButton.render(ctx, mouseX, mouseY, tickProgress);

            if (hasRuleIssue(rule, ValidationField.RULE_ID)) {
                drawErrorOutline(ctx, ruleNameField);
            }
        }
    }

    private final class RuleInputEntry extends BaseEntry {
        private final RuleEditorState rule;
        private final ButtonWidget inputTypeButton;
        private final ButtonWidget sourceOrOperatorButton;
        private final ButtonWidget matchButton;

        private RuleInputEntry(RuleEditorState rule) {
            this.rule = rule;

            inputTypeButton = addWidget(ButtonWidget.builder(
                    Text.literal(rule.getInputType().getDisplay()),
                    button -> {
                        rule.setInputType(rule.getInputType().next());
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, inputButtonWidth(), 20).build());

            sourceOrOperatorButton = addWidget(ButtonWidget.builder(
                    Text.empty(),
                    button -> {
                        if (rule.getInputType() == RuleInputType.TEXT) {
                            rule.setSource(rule.getSource().next());
                        }
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, compactButtonWidth(), 20).build());

            matchButton = addWidget(ButtonWidget.builder(
                    Text.empty(),
                    button -> {
                        if (rule.getInputType() == RuleInputType.TEXT) {
                            rule.setMatchType(rule.getMatchType().next());
                        } else {
                            cycleStateOperator(rule);
                        }
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, compactButtonWidth(), 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int y = getY();
            inputTypeButton.setMessage(Text.literal(rule.getInputType().getDisplay()));
            inputTypeButton.setWidth(inputButtonWidth());
            sourceOrOperatorButton.setMessage(Text.literal(getSourceButtonLabel(rule)));
            sourceOrOperatorButton.active = rule.getInputType() == RuleInputType.TEXT;
            sourceOrOperatorButton.visible = rule.getInputType() == RuleInputType.TEXT;
            sourceOrOperatorButton.setWidth(compactButtonWidth());
            matchButton.setMessage(Text.literal(getMatchButtonLabel(rule)));
            matchButton.setWidth(compactButtonWidth());
            matchButton.active = true;
            matchButton.visible = true;

            inputTypeButton.setPosition(contentLeft(), y);
            if (rule.getInputType() == RuleInputType.TEXT) {
                sourceOrOperatorButton.setPosition(sourceButtonX(), y);
                matchButton.setPosition(matchButtonX(), y);
            } else {
                matchButton.setPosition(sourceButtonX(), y);
            }

            inputTypeButton.render(ctx, mouseX, mouseY, tickProgress);
            if (rule.getInputType() == RuleInputType.TEXT) {
                sourceOrOperatorButton.render(ctx, mouseX, mouseY, tickProgress);
            }
            matchButton.render(ctx, mouseX, mouseY, tickProgress);

            if (hasRuleIssue(rule, ValidationField.INPUT_TYPE)) {
                drawErrorOutline(ctx, inputTypeButton);
            }
            if (rule.getInputType() == RuleInputType.TEXT && hasRuleIssue(rule, ValidationField.SOURCE)) {
                drawErrorOutline(ctx, sourceOrOperatorButton);
            }
            if (hasRuleIssue(rule, rule.getInputType() == RuleInputType.TEXT ? ValidationField.MATCH_TYPE : ValidationField.STATE_OPERATOR)) {
                drawErrorOutline(ctx, matchButton);
            }
        }
    }

    private final class RuleOptionsEntry extends BaseEntry {
        private final RuleEditorState rule;
        private final TextFieldWidget keyField;
        private final TextFieldWidget patternField;
        private final CheckboxWidget caseSensitiveCheckbox;
        private final TextFieldWidget cooldownField;
        private final ButtonWidget cooldownTypeButton;

        private RuleOptionsEntry(RuleEditorState rule) {
            this.rule = rule;

            keyField = addWidget(new TextFieldWidget(textRenderer, 0, 0, 100, 20, Text.literal("Key")));
            keyField.setText(Objects.toString(rule.getKey(), ""));
            keyField.setPlaceholder(Text.literal("Key"));
            keyField.setChangedListener(value -> {
                rule.setKey(value);
                controller.dirty = true;
            });

            patternField = addWidget(new TextFieldWidget(textRenderer, 0, 0, 100, 20, Text.literal("Pattern")));
            patternField.setText(Objects.toString(rule.getPattern(), ""));
            patternField.setChangedListener(value -> {
                rule.setPattern(value);
                controller.dirty = true;
            });

            caseSensitiveCheckbox = addWidget(CheckboxWidget.builder(
                    Text.literal("Case Sensitive"),
                    textRenderer
            ).pos(0, 0).checked(rule.isCaseSensitive()).callback((checkbox, checked) -> {
                rule.setCaseSensitive(checked);
                controller.dirty = true;
            }).build());

            cooldownField = addWidget(new TextFieldWidget(textRenderer, 0, 0, 90, 20, Text.literal("Cooldown")));
            cooldownField.setText(Objects.toString(rule.getCooldownString(), "0"));
            cooldownField.setPlaceholder(Text.literal("Cooldown"));
            cooldownField.setChangedListener(value -> {
                rule.setCooldownString(value);
                controller.dirty = true;
            });

            cooldownTypeButton = addWidget(ButtonWidget.builder(
                    Text.literal(getCooldownTypeLabel(rule)),
                    button -> {
                        TimerFormat currentType = rule.getCooldownType();
                        rule.setCooldownType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                        controller.dirty = true;
                    }
            ).dimensions(0, 0, COOLDOWN_TIMER_TYPE_WIDTH, 20).build());
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int y = getY();
            boolean textInput = rule.getInputType() == RuleInputType.TEXT;
            boolean unaryStateOperator = rule.getInputType() != RuleInputType.TEXT
                    && rule.getStateOperator() != null
                    && rule.getStateOperator().isUnary();

            syncField(keyField, Objects.toString(rule.getKey(), ""));
            syncField(patternField, Objects.toString(rule.getPattern(), ""));
            syncField(cooldownField, Objects.toString(rule.getCooldownString(), "0"));
            int x = contentLeft();
            if (textInput) {
                keyField.visible = false;
                patternField.visible = true;
                patternField.active = true;
                patternField.setSuggestion(null);
                patternField.setPlaceholder(Text.literal("Pattern"));
                patternField.setWidth(textPatternWidth());
                patternField.setPosition(x, y);
                patternField.render(ctx, mouseX, mouseY, tickProgress);
                x += textPatternWidth() + 8;
            } else {
                keyField.visible = true;
                keyField.active = true;
                keyField.setWidth(stateKeyWidth());
                keyField.setPosition(x, y);
                keyField.render(ctx, mouseX, mouseY, tickProgress);
                x += stateKeyWidth() + 8;

                patternField.visible = !unaryStateOperator;
                patternField.active = !unaryStateOperator;
                if (!unaryStateOperator) {
                    patternField.setPlaceholder(Text.literal(rule.getInputType() == RuleInputType.FLAG ? "Value" : "Pattern"));
                    patternField.setWidth(statePatternWidth());
                    patternField.setPosition(x, y);
                    patternField.render(ctx, mouseX, mouseY, tickProgress);
                    x += statePatternWidth() + 8;
                }
            }

            caseSensitiveCheckbox.visible = textInput;
            caseSensitiveCheckbox.active = textInput;
            if (textInput) {
                caseSensitiveCheckbox.setPosition(x, y + 2);
                caseSensitiveCheckbox.render(ctx, mouseX, mouseY, tickProgress);
                x += 128;
            }

            cooldownField.setWidth(cooldownFieldWidth());
            cooldownField.setPosition(cooldownFieldX(), y);
            cooldownField.render(ctx, mouseX, mouseY, tickProgress);
            cooldownTypeButton.setMessage(Text.literal(getCooldownTypeLabel(rule)));
            cooldownTypeButton.setWidth(COOLDOWN_TIMER_TYPE_WIDTH);
            cooldownTypeButton.setPosition(cooldownTypeButtonX(), y);
            cooldownTypeButton.render(ctx, mouseX, mouseY, tickProgress);

            if (!textInput && hasRuleIssue(rule, ValidationField.RULE_KEY)) {
                drawErrorOutline(ctx, keyField);
            }
            if (hasRuleIssue(rule, ValidationField.RULE_PATTERN)) {
                drawErrorOutline(ctx, patternField);
            }
            if (hasRuleIssue(rule, ValidationField.COOLDOWN)) {
                drawErrorOutline(ctx, cooldownField);
                drawErrorOutline(ctx, cooldownTypeButton);
            }
        }
    }

    private final class ActionEntry extends BaseEntry {
        private final RuleEditorState rule;
        private final ActionEditorState action;
        private final int actionIndex;
        private final ButtonWidget actionTypeButton;
        private final ButtonWidget timerTypeButton;
        private final ButtonWidget addButton;
        private final ButtonWidget removeButton;
        private final List<ActionFieldSpec> fieldSpecs;
        private final List<ActionFieldComponent> fieldComponents = new ArrayList<>();

        private ActionEntry(RuleEditorState rule, ActionEditorState action, int actionIndex) {
            this.rule = rule;
            this.action = action;
            this.actionIndex = actionIndex;

            actionTypeButton = addWidget(ButtonWidget.builder(
                    Text.literal(action.getType().getDisplay()),
                    button -> {
                        action.setType(action.getType().next());
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, actionTypeWidth(), 20).build());

            timerTypeButton = action.getType() == ActionType.START_TIMER
                    ? addWidget(ButtonWidget.builder(
                    Text.literal(getTimerTypeLabel(action)),
                    button -> {
                        TimerFormat currentType = action.getTimerType();
                        action.setTimerType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                        controller.dirty = true;
                    }
            ).dimensions(0, 0, ACTION_TIMER_TYPE_WIDTH, 20).build())
                    : null;

            removeButton = actionIndex == 0 ? null : addWidget(ButtonWidget.builder(
                    Text.literal("-"),
                    button -> {
                        rule.getActions().remove(actionIndex);
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, ACTION_ROW_BUTTON_WIDTH, 20).build());

            addButton = addWidget(ButtonWidget.builder(
                    Text.literal("+"),
                    button -> {
                        rule.getActions().add(actionIndex + 1, createDefaultAction());
                        controller.dirty = true;
                        rebuildRuleWidgets();
                    }
            ).dimensions(0, 0, ACTION_ROW_BUTTON_WIDTH, 20).build());

            fieldSpecs = ActionUiMeta.getFieldSpecs(action.getType());
            for (ActionFieldSpec spec : fieldSpecs) {
                ActionFieldComponent component = createFieldComponent(action, spec);
                addWidget(component.widget());
                fieldComponents.add(component);
            }
        }

        @Override
        public Text getNarration() {
            return Text.literal("Action " + (actionIndex + 1));
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int y = getY();
            ctx.drawText(textRenderer, Text.literal((actionIndex + 1) + "."), contentLeft() + ACTION_INDEX_X, y + 7, 0xFFFFFFFF, false);

            actionTypeButton.setMessage(Text.literal(action.getType().getDisplay()));
            actionTypeButton.setWidth(actionTypeWidth());
            actionTypeButton.setPosition(actionTypeX(), y);
            actionTypeButton.render(ctx, mouseX, mouseY, tickProgress);
            if (hasActionIssue(rule, actionIndex, ValidationField.ACTION_TYPE)) {
                drawErrorOutline(ctx, actionTypeButton);
            }

            int currentX = actionFieldX();
            int[] fieldWidths = computeFieldWidths(fieldSpecs, actionContentFieldWidth(timerTypeButton != null), ACTION_FIELD_GAP);
            for (int i = 0; i < fieldComponents.size(); i++) {
                int width = fieldWidths[i];
                fieldComponents.get(i).render(ctx, currentX, y, width, mouseX, mouseY, tickProgress);
                ValidationField field = toValidationField(fieldSpecs.get(i));
                if (field != null && hasActionIssue(rule, actionIndex, field)) {
                    drawErrorOutline(ctx, fieldComponents.get(i).widget());
                }
                currentX += width + ACTION_FIELD_GAP;
            }

            if (timerTypeButton != null) {
                timerTypeButton.setMessage(Text.literal(getTimerTypeLabel(action)));
                timerTypeButton.setWidth(ACTION_TIMER_TYPE_WIDTH);
                timerTypeButton.setPosition(actionTimerTypeButtonX(), y);
                timerTypeButton.render(ctx, mouseX, mouseY, tickProgress);
                if (hasActionIssue(rule, actionIndex, ValidationField.ACTION_TIMER_TYPE)) {
                    drawErrorOutline(ctx, timerTypeButton);
                }
            }

            int removeButtonX = actionRemoveButtonX();
            int addButtonX = actionAddButtonX();
            if (removeButton != null) {
                removeButton.setPosition(removeButtonX, y);
                removeButton.render(ctx, mouseX, mouseY, tickProgress);
            }
            addButton.setPosition(addButtonX, y);
            addButton.render(ctx, mouseX, mouseY, tickProgress);
        }
    }

    private ActionEditorState createDefaultAction() {
        ActionEditorState action = new ActionEditorState();
        action.setType(ActionType.CHAT);
        action.setText("");
        return action;
    }

    private String getTimerTypeLabel(ActionEditorState action) {
        TimerFormat timerType = action.getTimerType();
        return (timerType == null ? TimerFormat.SECONDS : timerType).getDisplay();
    }

    private String getCooldownTypeLabel(RuleEditorState rule) {
        TimerFormat timerType = rule.getCooldownType();
        return (timerType == null ? TimerFormat.SECONDS : timerType).getDisplay();
    }

    private ValidationField toValidationField(ActionFieldSpec spec) {
        return switch (spec.key()) {
            case "text" -> ValidationField.ACTION_TEXT;
            case "key" -> ValidationField.ACTION_KEY;
            case "value" -> ValidationField.ACTION_VALUE;
            case "soundId" -> ValidationField.ACTION_SOUND_ID;
            default -> null;
        };
    }

    private ActionFieldComponent createFieldComponent(ActionEditorState action, ActionFieldSpec spec) {
        if (spec.type() == ActionFieldSpec.ActionFieldType.BOOLEAN) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.empty(),
                    widget -> {
                        boolean currentValue = Boolean.parseBoolean(readActionField(action, spec.key()));
                        writeActionField(action, spec.key(), String.valueOf(!currentValue));
                        controller.dirty = true;
                        widget.setMessage(Text.literal(spec.label() + ": " + !currentValue));
                    }
            ).dimensions(0, 0, 100, 20).build();

            return new ActionFieldComponent(button) {
                @Override
                void beforeRender() {
                    boolean currentValue = Boolean.parseBoolean(readActionField(action, spec.key()));
                    button.setMessage(Text.literal(spec.label() + ": " + currentValue));
                }
            };
        }

        TextFieldWidget field = new TextFieldWidget(textRenderer, 0, 0, 100, 20, Text.literal(spec.label()));
        field.setText(Objects.toString(readActionField(action, spec.key()), ""));
        field.setPlaceholder(Text.literal(spec.label()));
        field.setChangedListener(value -> {
            writeActionField(action, spec.key(), value);
            controller.dirty = true;
        });
        return new ActionFieldComponent(field) {
            @Override
            void beforeRender() {
                String expected = Objects.toString(readActionField(action, spec.key()), "");
                if (!Objects.equals(field.getText(), expected) && !field.isFocused()) {
                    field.setText(expected);
                }
            }
        };
    }

    private abstract class ActionFieldComponent {
        private final ClickableWidget widget;

        private ActionFieldComponent(ClickableWidget widget) {
            this.widget = widget;
        }

        void render(DrawContext ctx, int x, int y, int width, int mouseX, int mouseY, float tickProgress) {
            widget.setWidth(width);
            widget.setPosition(x, y);
            beforeRender();
            widget.render(ctx, mouseX, mouseY, tickProgress);
        }

        ClickableWidget widget() {
            return widget;
        }

        abstract void beforeRender();
    }

    private int[] computeFieldWidths(List<ActionFieldSpec> specs, int availableWidth, int fieldGap) {
        int count = specs.size();
        int[] widths = new int[count];
        if (count == 0) {
            return widths;
        }

        int totalGapWidth = fieldGap * (count - 1);
        int usableWidth = Math.max(count * 40, availableWidth - totalGapWidth);

        int totalWeight = 0;
        for (ActionFieldSpec spec : specs) {
            totalWeight += fieldWeight(spec);
        }

        int assignedWidth = 0;
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                widths[i] = Math.max(40, usableWidth - assignedWidth);
                continue;
            }

            int width = (usableWidth * fieldWeight(specs.get(i))) / totalWeight;
            widths[i] = Math.max(40, width);
            assignedWidth += widths[i];
            totalWeight -= fieldWeight(specs.get(i));
        }

        return widths;
    }

    private int fieldWeight(ActionFieldSpec spec) {
        return switch (spec.type()) {
            case BOOLEAN -> 1;
            case INTEGER -> 1;
            case TEXT -> "key".equals(spec.key()) ? 2 : 1;
        };
    }

    private String readActionField(ActionEditorState action, String key) {
        return switch (key) {
            case "text" -> action.getText();
            case "key" -> action.getKey();
            case "value" -> action.getValue();
            case "soundId" -> action.getSoundId();
            default -> "";
        };
    }

    private void writeActionField(ActionEditorState action, String key, String value) {
        switch (key) {
            case "text" -> action.setText(value);
            case "key" -> action.setKey(value);
            case "value" -> action.setValue(value);
            case "soundId" -> action.setSoundId(value);
            default -> {
            }
        }
    }

    private void syncField(TextFieldWidget field, String value) {
        if (!Objects.equals(field.getText(), value) && !field.isFocused()) {
            field.setText(value);
        }
    }

    private void drawErrorOutline(DrawContext ctx, ClickableWidget widget) {
        int color = 0xFFFF5555;
        int x = widget.getX();
        int y = widget.getY();
        int right = x + widget.getWidth();
        int bottom = y + widget.getHeight();
        ctx.fill(x, y, right, y + 1, color);
        ctx.fill(x, bottom - 1, right, bottom, color);
        ctx.fill(x, y, x + 1, bottom, color);
        ctx.fill(right - 1, y, right, bottom, color);
    }

    private boolean hasRuleIssue(RuleEditorState rule, ValidationField field) {
        int ruleIndex = controller.getRules().indexOf(rule);
        for (ValidationIssue issue : validationIssues) {
            if (issue.ruleIndex() == ruleIndex && issue.actionIndex() == null && issue.field() == field) {
                return true;
            }
        }
        return false;
    }

    private boolean hasActionIssue(RuleEditorState rule, int actionIndex, ValidationField field) {
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
        String firstMessage = issues.get(0).message();
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

    private void cycleStateOperator(RuleEditorState rule) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null) {
            rule.setStateOperator(StateOperator.IS);
            return;
        }

        StateOperator next = operator.next();
        while (!supportsOperator(rule.getInputType(), next) && next != operator) {
            next = next.next();
        }
        rule.setStateOperator(next);
    }

    private boolean supportsOperator(RuleInputType inputType, StateOperator operator) {
        if (inputType == RuleInputType.FLAG) {
            return operator == StateOperator.IS
                    || operator == StateOperator.IS_NOT
                    || operator == StateOperator.EXISTS
                    || operator == StateOperator.MISSING;
        }
        if (inputType == RuleInputType.COUNTER) {
            return operator != StateOperator.RUNNING && operator != StateOperator.STOPPED;
        }
        if (inputType == RuleInputType.TIMER) {
            return operator != StateOperator.EXISTS && operator != StateOperator.MISSING;
        }
        return false;
    }

    private String getSourceButtonLabel(RuleEditorState rule) {
        return rule.getSource().getDisplay();
    }

    private String getMatchButtonLabel(RuleEditorState rule) {
        if (rule.getInputType() == RuleInputType.TEXT) {
            return rule.getMatchType().getDisplay();
        }
        StateOperator operator = rule.getStateOperator();
        return operator == null ? StateOperator.IS.getDisplay() : operator.getDisplay();
    }

    private int contentLeft() {
        return Math.max(12, Math.min(20, width / 40));
    }

    private int contentRight() {
        return Math.max(contentLeft() + 320, width - contentLeft() - 28);
    }

    private int contentWidth() {
        return contentRight() - contentLeft();
    }

    private int summaryExpandX() {
        return contentRight() - 20;
    }

    private int summaryDeleteX() {
        return summaryExpandX() - 25;
    }

    private int summaryEnabledX() {
        return summaryDeleteX() - 115;
    }

    private int summaryNameWidth() {
        return Math.max(180, summaryEnabledX() - contentLeft() - 10);
    }

    private int compactButtonWidth() {
        return Math.max(90, Math.min(118, (contentWidth() - 220) / 3));
    }

    private int inputButtonWidth() {
        return Math.max(100, Math.min(128, (contentWidth() - 220) / 3 + 16));
    }

    private int sourceButtonX() {
        return contentLeft() + inputButtonWidth() + 4;
    }

    private int matchButtonX() {
        return sourceButtonX() + compactButtonWidth() + 4;
    }

    private int fieldX() {
        return matchButtonX() + compactButtonWidth() + 5;
    }

    private int contentFieldWidth() {
        return Math.max(120, contentRight() - fieldX());
    }

    private int actionRowButtonsWidth() {
        return (ACTION_ROW_BUTTON_WIDTH * 2) + ACTION_ROW_BUTTON_GAP + ACTION_FIELD_GAP;
    }

    private int actionFieldX() {
        return actionTypeX() + actionTypeWidth() + ACTION_FIELD_GAP;
    }

    private int actionContentFieldWidth(boolean hasTimerTypeButton) {
        int trailingControlX = hasTimerTypeButton ? actionTimerTypeButtonX() : actionRemoveButtonX();
        return Math.max(70, trailingControlX - ACTION_FIELD_GAP - actionFieldX());
    }

    private int actionRemoveButtonX() {
        return contentRight() - ((ACTION_ROW_BUTTON_WIDTH * 2) + ACTION_ROW_BUTTON_GAP);
    }

    private int actionAddButtonX() {
        return contentRight() - ACTION_ROW_BUTTON_WIDTH;
    }

    private int actionTimerTypeButtonX() {
        return actionRemoveButtonX() - ACTION_FIELD_GAP - ACTION_TIMER_TYPE_WIDTH;
    }

    private int actionTypeX() {
        return contentLeft() + ACTION_LABEL_WIDTH;
    }

    private int actionTypeWidth() {
        int availableWidth = Math.max(140, actionRemoveButtonX() - actionTypeX() - ACTION_FIELD_GAP);
        return Math.max(90, Math.min(150, availableWidth / 2));
    }

    private int textPatternWidth() {
        return Math.max(180, contentWidth() - 230);
    }

    private int stateKeyWidth() {
        return Math.max(130, Math.min(180, contentWidth() / 4));
    }

    private int statePatternWidth() {
        return Math.max(100, contentWidth() - stateKeyWidth() - cooldownControlsWidth() - 24);
    }

    private int cooldownControlsWidth() {
        return cooldownFieldWidth() + ACTION_FIELD_GAP + COOLDOWN_TIMER_TYPE_WIDTH;
    }

    private int cooldownFieldWidth() {
        return 96;
    }

    private int cooldownFieldX() {
        return cooldownTypeButtonX() - ACTION_FIELD_GAP - cooldownFieldWidth();
    }

    private int cooldownTypeButtonX() {
        return contentRight() - COOLDOWN_TIMER_TYPE_WIDTH;
    }

    private final class RuleListWidget extends AlwaysSelectedEntryListWidget<BaseEntry> {
        private RuleListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
        }

        private void resetEntries() {
            clearEntries();
        }

        private void appendEntry(BaseEntry entry) {
            addEntry(entry);
        }

        @Override
        public int getRowWidth() {
            return contentWidth();
        }

        @Override
        protected boolean isEntrySelectionAllowed() {
            return false;
        }

        @Override
        protected void drawSelectionHighlight(DrawContext context, BaseEntry entry, int borderColor) {
        }

        @Override
        protected int getScrollbarX() {
            return contentRight() + 10;
        }
    }
}
