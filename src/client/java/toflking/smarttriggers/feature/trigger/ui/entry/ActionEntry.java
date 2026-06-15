package toflking.smarttriggers.feature.trigger.ui.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionUiMeta;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreenHost;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.ui.support.SoundIdFilter;
import toflking.smarttriggers.feature.trigger.ui.support.TriggerRulesUiSupport;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ActionEntry extends AbstractTriggerRuleEntry {
    private static final int ACTION_FIELD_MAX_LENGTH = 512;

    private final RuleEditorState rule;
    private final ActionEditorState action;
    private final int actionIndex;
    private final Button actionTypeButton;
    private final Button timerTypeButton;
    private final Button previewButton;
    private final Button addButton;
    private final Button removeButton;
    private final List<ActionFieldSpec> fieldSpecs;
    private final List<ActionFieldComponent> fieldComponents = new ArrayList<>();

    public ActionEntry(TriggerRulesScreenHost host, RuleEditorState rule, ActionEditorState action, int actionIndex) {
        super(host);
        this.rule = rule;
        this.action = action;
        this.actionIndex = actionIndex;

        actionTypeButton = addWidget(Button.builder(
                Component.literal(action.getType().getDisplay()),
                button -> {
                    action.setType(action.getType().next());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, host.layout().actionTypeWidth(), 20).build());

        timerTypeButton = action.getType() == ActionType.START_TIMER
                ? addWidget(Button.builder(
                Component.literal(host.getTimerTypeLabel(action)),
                button -> {
                    TimerFormat currentType = action.getTimerType();
                    action.setTimerType((currentType == null ? TimerFormat.SECONDS : currentType).next());
                    host.markDirty();
                }
        ).bounds(0, 0, TriggerRulesLayout.ACTION_TIMER_TYPE_WIDTH, 20).build())
                : null;

        previewButton = action.getType() == ActionType.SOUND
                ? addWidget(Button.builder(
                Component.literal("▶"),
                button -> playPreviewSound()
        ).bounds(0, 0, TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH, 20).build())
                : null;

        removeButton = actionIndex == 0 ? null : addWidget(Button.builder(
                Component.literal("-"),
                button -> {
                    rule.getActions().remove(actionIndex);
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH, 20).build());

        addButton = addWidget(Button.builder(
                Component.literal("+"),
                button -> {
                    rule.getActions().add(actionIndex + 1, host.createDefaultAction());
                    host.markDirty();
                    host.rebuildRuleWidgets();
                }
        ).bounds(0, 0, TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH, 20).build());

        fieldSpecs = ActionUiMeta.getFieldSpecs(action.getType());
        for (ActionFieldSpec spec : fieldSpecs) {
            ActionFieldComponent component = createFieldComponent(spec);
            addWidget(component.widget());
            fieldComponents.add(component);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
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
    public Component getNarration() {
        return Component.literal("Action " + (actionIndex + 1));
    }

    @Override
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        int y = getY();
        ctx.text(host.textRenderer(), Component.literal((actionIndex + 1) + "."), host.layout().contentLeft() + TriggerRulesLayout.ACTION_INDEX_X, y + 7, 0xFFFFFFFF, false);

        actionTypeButton.setMessage(Component.literal(action.getType().getDisplay()));
        actionTypeButton.setWidth(host.layout().actionTypeWidth());
        actionTypeButton.setX(host.layout().actionTypeX());
        actionTypeButton.setY(y);
        actionTypeButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        if (host.hasActionIssue(rule, actionIndex, ValidationField.ACTION_TYPE)) {
            host.drawErrorOutline(ctx, actionTypeButton);
        }

        int currentX = host.layout().actionFieldX();
        int[] fieldWidths = TriggerRulesUiSupport.computeFieldWidths(fieldSpecs, actionContentFieldWidth(), TriggerRulesLayout.ACTION_FIELD_GAP);
        for (int i = 0; i < fieldComponents.size(); i++) {
            int width = fieldWidths[i];
            fieldComponents.get(i).extract(ctx, currentX, y, width, mouseX, mouseY, tickProgress);
            ValidationField field = host.toValidationField(fieldSpecs.get(i));
            if (field != null && host.hasActionIssue(rule, actionIndex, field)) {
                host.drawErrorOutline(ctx, fieldComponents.get(i).widget());
            }
            currentX += width + TriggerRulesLayout.ACTION_FIELD_GAP;
        }

        if (timerTypeButton != null) {
            timerTypeButton.setMessage(Component.literal(host.getTimerTypeLabel(action)));
            timerTypeButton.setWidth(TriggerRulesLayout.ACTION_TIMER_TYPE_WIDTH);
            timerTypeButton.setX(host.layout().actionTimerTypeButtonX());
            timerTypeButton.setY(y);
            timerTypeButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
            if (host.hasActionIssue(rule, actionIndex, ValidationField.ACTION_TIMER_TYPE)) {
                host.drawErrorOutline(ctx, timerTypeButton);
            }
        }

        if (previewButton != null) {
            previewButton.setX(actionPreviewButtonX());
            previewButton.setY(y);
            previewButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        }

        if (removeButton != null) {
            removeButton.setX(host.layout().actionRemoveButtonX());
            removeButton.setY(y);
            removeButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        }
        addButton.setX(host.layout().actionAddButtonX());
        addButton.setY(y);
        addButton.extractRenderState(ctx, mouseX, mouseY, tickProgress);
    }

    private ActionFieldComponent createFieldComponent(ActionFieldSpec spec) {
        if (spec.type() == ActionFieldSpec.ActionFieldType.BOOLEAN) {
            Button button = Button.builder(
                    Component.empty(),
                    widget -> {
                        boolean currentValue = Boolean.parseBoolean(TriggerRulesUiSupport.readActionField(action, spec.key()));
                        TriggerRulesUiSupport.writeActionField(action, spec.key(), String.valueOf(!currentValue));
                        host.markDirty();
                        widget.setMessage(Component.literal(spec.label() + ": " + !currentValue));
                    }
            ).bounds(0, 0, 100, 20).build();

            return new ActionFieldComponent(button) {
                @Override
                void beforeRender() {
                    boolean currentValue = Boolean.parseBoolean(TriggerRulesUiSupport.readActionField(action, spec.key()));
                    button.setMessage(Component.literal(spec.label() + ": " + currentValue));
                }
            };
        }

        EditBox field = new EditBox(host.textRenderer(), 0, 0, 100, 20, Component.literal(spec.label()));
        field.setMaxLength(ACTION_FIELD_MAX_LENGTH);
        field.setValue(Objects.toString(TriggerRulesUiSupport.readActionField(action, spec.key()), ""));
        field.setHint(Component.literal(spec.label()));
        if (spec.key().equals("soundId")) {
            return new SoundIdFieldComponent(field, value -> {
                TriggerRulesUiSupport.writeActionField(action, spec.key(), value);
                host.markDirty();
            }) {
                @Override
                void beforeRender() {
                    String expected = Objects.toString(TriggerRulesUiSupport.readActionField(action, spec.key()), "");
                    if (!Objects.equals(field.getValue(), expected) && !field.isFocused()) {
                        field.setValue(expected);
                    }
                }
            };
        }
        field.setResponder(value -> {
            TriggerRulesUiSupport.writeActionField(action, spec.key(), value);
            host.markDirty();
        });
        return new ActionFieldComponent(field) {
            @Override
            void beforeRender() {
                String expected = Objects.toString(TriggerRulesUiSupport.readActionField(action, spec.key()), "");
                if (!Objects.equals(field.getValue(), expected) && !field.isFocused()) {
                    field.setValue(expected);
                }
            }
        };
    }

    private int actionContentFieldWidth() {
        int trailingControlX;
        if (timerTypeButton != null) {
            trailingControlX = host.layout().actionTimerTypeButtonX();
        } else if (previewButton != null) {
            trailingControlX = actionPreviewButtonX();
        } else {
            trailingControlX = host.layout().actionRemoveButtonX();
        }
        return Math.max(70, trailingControlX - TriggerRulesLayout.ACTION_FIELD_GAP - host.layout().actionFieldX());
    }

    private int actionPreviewButtonX() {
        return host.layout().actionRemoveButtonX() - TriggerRulesLayout.ACTION_FIELD_GAP - TriggerRulesLayout.ACTION_ROW_BUTTON_WIDTH;
    }

    private void playPreviewSound() {
        String soundId = Objects.toString(action.getSoundId(), "").trim();
        if (soundId.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        try {
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(soundId));
            if (soundEvent == null) {
                return;
            }
            mc.player.playSound(soundEvent, 1.0F, 1.0F);
        } catch (RuntimeException ignored) {
        }
    }

    private abstract static class ActionFieldComponent {
        private final AbstractWidget widget;

        private ActionFieldComponent(AbstractWidget widget) {
            this.widget = widget;
        }

        void extract(GuiGraphicsExtractor ctx, int x, int y, int width, int mouseX, int mouseY, float tickProgress) {
            widget.setWidth(width);
            widget.setX(x);
            widget.setY(y);
            beforeRender();
            widget.extractRenderState(ctx, mouseX, mouseY, tickProgress);
        }

        AbstractWidget widget() {
            return widget;
        }

        abstract void beforeRender();
    }

    private abstract class SoundIdFieldComponent extends ActionFieldComponent {
        private static final int MAX_SUGGESTIONS = 8;
        private static final int SUGGESTION_OFFSET_Y = 2;

        private final List<String> suggestions = new ArrayList<>();
        private final EditBox field;
        private final Consumer<String> onChanged;

        private SoundIdFieldComponent(EditBox field, Consumer<String> onChanged) {
            super(field);
            this.field = field;
            this.onChanged = onChanged;

            field.setResponder(value -> {
                onChanged.accept(value);
                calculateNewSuggestions(value);
            });
            calculateNewSuggestions(field.getValue());
            host.setOverlaySelectHandler(selectedSuggestion -> {
                field.setValue(selectedSuggestion);
                onChanged.accept(selectedSuggestion);
            });
        }

        public void calculateNewSuggestions(String query) {
            suggestions.clear();
            suggestions.addAll(SoundIdFilter.filterSoundIds(query));
        }

        @Override
        public void extract(GuiGraphicsExtractor ctx, int x, int y, int width, int mouseX, int mouseY, float tickProgress) {
            super.extract(ctx, x, y, width, mouseX, mouseY, tickProgress);
            if (!field.isFocused() || suggestions.isEmpty()) {
                host.setSuggestionsOpened(false);
                return;
            }

            int visibleSuggestions = Math.min(MAX_SUGGESTIONS, suggestions.size());
            host.setOverlaySelectHandler(selectedSuggestion -> {
                field.setValue(selectedSuggestion);
                onChanged.accept(selectedSuggestion);
            });
            host.showOverlaySuggestions(x, y + field.getHeight() + SUGGESTION_OFFSET_Y, width, suggestions.subList(0, visibleSuggestions));
            host.setSuggestionsOpened(true);
        }

    }
}
