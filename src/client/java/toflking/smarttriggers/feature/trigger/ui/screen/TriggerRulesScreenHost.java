package toflking.smarttriggers.feature.trigger.ui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.List;
import java.util.function.Consumer;

public interface TriggerRulesScreenHost {
    Font textRenderer();

    TriggerRulesLayout layout();

    void markDirty();

    void rebuildRuleWidgets();

    void removeRule(RuleEditorState rule);

    boolean hasRuleIssue(RuleEditorState rule, ValidationField field);

    boolean hasActionIssue(RuleEditorState rule, int actionIndex, ValidationField field);

    void drawErrorOutline(GuiGraphicsExtractor ctx, AbstractWidget widget);

    void cycleStateOperator(RuleEditorState rule);

    void cycleStateOperator(RuleEditorState rule, boolean forward);

    boolean isRightClick(MouseButtonEvent click);

    void playButtonClickSound();

    String getSourceButtonLabel(RuleEditorState rule);

    String getMatchButtonLabel(RuleEditorState rule);

    ActionEditorState createDefaultAction();

    String getTimerTypeLabel(ActionEditorState action);

    String getCooldownTypeLabel(RuleEditorState rule);

    ValidationField toValidationField(ActionFieldSpec spec);

    int rowHeight();

    void clearOverlaySuggestions();

    void showOverlaySuggestions(int x, int y, int width, List<String> suggestions);

    void renderOverlaySuggestions(GuiGraphicsExtractor ctx);

    void setSuggestionsOpened(boolean suggestionsOpened);

    void setOverlaySelectHandler(Consumer<String> overlaySelectHandler);
}
