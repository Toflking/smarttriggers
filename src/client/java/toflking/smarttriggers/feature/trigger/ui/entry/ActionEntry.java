package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.support.TriggerRulesUiSupport;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionUiMeta;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ActionEntry extends AbstractTriggerRuleEntry {
    private static final int ACTION_FIELD_MAX_LENGTH = 512;

    private final RuleEditorState rule;
    private final ActionEditorState action;
    private final int actionIndex;
    private final ButtonWidget actionTypeButton;
    private final ButtonWidget timerTypeButton;
    private final ButtonWidget addButton;
    private final ButtonWidget removeButton;
    private final List<ActionFieldSpec> fieldSpecs;
    private final List<ActionFieldComponent> fieldComponents = new ArrayList<>();

    public ActionEntry(TriggerRulesScreenHost host, RuleEditorState rule, ActionEditorState action, int actionIndex) {
        super(host);
        this.rule = rule;
        this.action = action;
        this.actionIndex = actionIndex;

        actionTypeButton = addWidget(ButtonWidget.builder(
                Text.literal(action.getType().getDisplay()),
                button -> {
                    action.setType(action.getType().next());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, host.layout().actionTypeWidth(), 20).build());

        timerTypeButton = action.getType() == ActionType.START_TIMER
                ? addWidget(ButtonWidget.builder(
                Text.literal(host.getTimerTypeLabel(action)),
                button -> {
                    TimerFormat currentType = action.getTimerType();
                    action.setTimerType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                    host.markDirty();
                }
        ).dimensions(0, 0, TriggerRulesLayout.ACTION_TIMER_TYPE_WIDTH, 20).build())
                : null;

        removeButton = actionIndex == 0 ? null : addWidget(ButtonWidget.builder(
                Text.literal("-"),
                button -> {
                    rule.getActions().remove(actionIndex);
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH, 20).build());

        addButton = addWidget(ButtonWidget.builder(
                Text.literal("+"),
                button -> {
                    rule.getActions().add(actionIndex + 1, host.createDefaultAction());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).dimensions(0, 0, TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH, 20).build());

        fieldSpecs = ActionUiMeta.getFieldSpecs(action.getType());
        for (ActionFieldSpec spec : fieldSpecs) {
            ActionFieldComponent component = createFieldComponent(spec);
            addWidget(component.widget());
            fieldComponents.add(component);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
        if (host.isRightClick(click)) {
            if (actionTypeButton.isMouseOver(click.x(), click.y())) {
                host.playButtonClickSound();
                action.setType(action.getType().previous());
                host.markDirty();
                host.rebuildRuleWidgets();
                setFocused(actionTypeButton);
                return true;
            }
            if (timerTypeButton != null && timerTypeButton.isMouseOver(click.x(), click.y())) {
                host.playButtonClickSound();
                TimerFormat currentType = action.getTimerType();
                action.setTimerType((currentType == null ? TimerFormat.SECONDS : currentType).previous());
                host.markDirty();
                setFocused(timerTypeButton);
                return true;
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public Text getNarration() {
        return Text.literal("Action " + (actionIndex + 1));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        ctx.drawText(host.textRenderer(), Text.literal((actionIndex + 1) + "."), host.layout().contentLeft() + TriggerRulesLayout.ACTION_INDEX_X, y + 7, 0xFFFFFFFF, false);

        actionTypeButton.setMessage(Text.literal(action.getType().getDisplay()));
        actionTypeButton.setWidth(host.layout().actionTypeWidth());
        actionTypeButton.setPosition(host.layout().actionTypeX(), y);
        actionTypeButton.render(ctx, mouseX, mouseY, tickProgress);
        if (host.hasActionIssue(rule, actionIndex, ValidationField.ACTION_TYPE)) {
            host.drawErrorOutline(ctx, actionTypeButton);
        }

        int currentX = host.layout().actionFieldX();
        int[] fieldWidths = TriggerRulesUiSupport.computeFieldWidths(fieldSpecs, host.layout().actionContentFieldWidth(timerTypeButton != null), TriggerRulesLayout.ACTION_FIELD_GAP);
        for (int i = 0; i < fieldComponents.size(); i++) {
            int width = fieldWidths[i];
            fieldComponents.get(i).render(ctx, currentX, y, width, mouseX, mouseY, tickProgress);
            ValidationField field = host.toValidationField(fieldSpecs.get(i));
            if (field != null && host.hasActionIssue(rule, actionIndex, field)) {
                host.drawErrorOutline(ctx, fieldComponents.get(i).widget());
            }
            currentX += width + TriggerRulesLayout.ACTION_FIELD_GAP;
        }

        if (timerTypeButton != null) {
            timerTypeButton.setMessage(Text.literal(host.getTimerTypeLabel(action)));
            timerTypeButton.setWidth(TriggerRulesLayout.ACTION_TIMER_TYPE_WIDTH);
            timerTypeButton.setPosition(host.layout().actionTimerTypeButtonX(), y);
            timerTypeButton.render(ctx, mouseX, mouseY, tickProgress);
            if (host.hasActionIssue(rule, actionIndex, ValidationField.ACTION_TIMER_TYPE)) {
                host.drawErrorOutline(ctx, timerTypeButton);
            }
        }

        if (removeButton != null) {
            removeButton.setPosition(host.layout().actionRemoveButtonX(), y);
            removeButton.render(ctx, mouseX, mouseY, tickProgress);
        }
        addButton.setPosition(host.layout().actionAddButtonX(), y);
        addButton.render(ctx, mouseX, mouseY, tickProgress);
    }

    private ActionFieldComponent createFieldComponent(ActionFieldSpec spec) {
        if (spec.type() == ActionFieldSpec.ActionFieldType.BOOLEAN) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.empty(),
                    widget -> {
                        boolean currentValue = Boolean.parseBoolean(TriggerRulesUiSupport.readActionField(action, spec.key()));
                        TriggerRulesUiSupport.writeActionField(action, spec.key(), String.valueOf(!currentValue));
                        host.markDirty();
                        widget.setMessage(Text.literal(spec.label() + ": " + !currentValue));
                    }
            ).dimensions(0, 0, 100, 20).build();

            return new ActionFieldComponent(button) {
                @Override
                void beforeRender() {
                    boolean currentValue = Boolean.parseBoolean(TriggerRulesUiSupport.readActionField(action, spec.key()));
                    button.setMessage(Text.literal(spec.label() + ": " + currentValue));
                }
            };
        }

        TextFieldWidget field = new TextFieldWidget(host.textRenderer(), 0, 0, 100, 20, Text.literal(spec.label()));
        field.setMaxLength(ACTION_FIELD_MAX_LENGTH);
        field.setText(Objects.toString(TriggerRulesUiSupport.readActionField(action, spec.key()), ""));
        field.setPlaceholder(Text.literal(spec.label()));
        field.setChangedListener(value -> {
            TriggerRulesUiSupport.writeActionField(action, spec.key(), value);
            host.markDirty();
        });
        return new ActionFieldComponent(field) {
            @Override
            void beforeRender() {
                String expected = Objects.toString(TriggerRulesUiSupport.readActionField(action, spec.key()), "");
                if (!Objects.equals(field.getText(), expected) && !field.isFocused()) {
                    field.setText(expected);
                }
            }
        };
    }

    private abstract static class ActionFieldComponent {
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
}
